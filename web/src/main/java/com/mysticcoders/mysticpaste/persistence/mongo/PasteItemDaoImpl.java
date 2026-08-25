package com.mysticcoders.mysticpaste.persistence.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.ReturnDocument;
import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import com.mysticcoders.mysticpaste.utils.TokenGenerator;
import dev.morphia.Datastore;
import dev.morphia.ModifyOptions;
import dev.morphia.Morphia;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.Sort;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import org.msgpack.jackson.dataformat.MessagePackMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * MongoDB backed implementation of the paste item store, using Redis for the paste index,
 * the public paste history list and a small cache of the most recent pastes.
 */
public class PasteItemDaoImpl implements PasteItemDao {

    private static final String KEY_IP_ADDRESSES = "ipAddresses";
    private static final String KEY_PASTE_INDEX = "pasteIndex";
    private static final String KEY_PASTE_HISTORY = "pasteHistory";
    private static final String KEY_PASTE_HISTORY_CACHE = "pasteHistoryCache";
    private static final String KEY_ADMIN_PASSWORD = "pasteAdminPw";

    private static final int HISTORY_CACHE_SIZE = 5;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final Datastore ds;

    protected final JedisPool jedisPool;

    protected final ObjectMapper msgpack;

    private final int tokenLength = 10;

    public PasteItemDaoImpl(MongoClient mongoClient, String dbName, JedisPool jedisPool) {
        ds = Morphia.createDatastore(mongoClient, dbName);
        ds.getMapper().map(PasteItem.class);
        this.jedisPool = jedisPool;
        this.msgpack = new MessagePackMapper();
    }

    @Override
    public void appendIpAddress(String ipAddress) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd(KEY_IP_ADDRESSES, ipAddress);
        } catch (JedisConnectionException je) {
            logger.warn("Unable to record client IP address", je);
        }
    }

    @Override
    public String create(PasteItem item) {
        try (Jedis jedis = jedisPool.getResource()) {
            long pasteIndex = jedis.incr(KEY_PASTE_INDEX);
            String tokenId = TokenGenerator.generateToken(tokenLength);
            item.setItemId(tokenId);
            item.setPasteIndex(pasteIndex);

            logger.info("Creating paste with itemId: {} pasteIndex: {}", tokenId, pasteIndex);
            ds.insert(item);

            if (!item.isPrivate()) {
                jedis.lpush(KEY_PASTE_HISTORY, Long.toString(pasteIndex));
                byte[] packedEntry = packEntry(item);
                jedis.rpop(KEY_PASTE_HISTORY_CACHE.getBytes(StandardCharsets.UTF_8));
                jedis.lpush(KEY_PASTE_HISTORY_CACHE.getBytes(StandardCharsets.UTF_8), packedEntry);
            }

            return tokenId;
        } catch (JedisConnectionException je) {
            logger.error("Unable to reach Redis while creating a paste", je);
        } catch (Exception e) {
            logger.error("Unable to create paste", e);
        }
        return null;
    }

    @Override
    public String getAdminPassword() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(KEY_ADMIN_PASSWORD);
        } catch (JedisConnectionException je) {
            logger.error("Unable to reach Redis while reading the admin password", je);
        }

        return null;
    }

    @Override
    public PasteItem get(String id) {
        logger.info("Retrieving paste ID: {}", id);
        return ds.find(PasteItem.class)
                .filter(Filters.eq("itemId", id))
                .first();
    }

    /**
     * Serializes the fields of a paste needed to render the history list into a MessagePack payload.
     */
    private byte[] packEntry(PasteItem item) throws IOException {
        PasteItemCache cache = new PasteItemCache();
        cache.itemId = item.getItemId();
        cache.numberOfLines = item.getContentLineCount();
        cache.timestamp = item.getTimestamp();
        cache.content = item.getContent();
        return msgpack.writeValueAsBytes(cache);
    }

    private List<byte[]> getHistoryPacked(List<PasteItem> historyList) {
        try {
            List<byte[]> cacheItems = new ArrayList<>();
            for (PasteItem item : historyList) {
                cacheItems.add(packEntry(item));
            }
            return cacheItems;
        } catch (IOException e) {
            logger.error("Unable to pack paste history for the cache", e);
        }

        return null;
    }

    private List<PasteItem> getUnpackedHistory(List<byte[]> packedHistory) {
        List<PasteItem> pasteItems = new ArrayList<>();
        try {
            for (byte[] packed : packedHistory) {
                PasteItemCache itemCache = msgpack.readValue(packed, PasteItemCache.class);
                PasteItem item = new PasteItem();
                item.setItemId(itemCache.itemId);
                item.setTimestamp(itemCache.timestamp);
                item.setContent(itemCache.content);
                pasteItems.add(item);
            }

            return pasteItems;
        } catch (IOException e) {
            logger.error("Unable to unpack the cached paste history", e);
        }

        return null;
    }

    @Override
    public List<PasteItem> find(int count, int startIndex, String filter) {

        logger.info("Pulling paste history: {} record(s) starting {}", count, startIndex);

        try (Jedis jedis = jedisPool.getResource()) {
            if (startIndex == 0 && jedis.exists(KEY_PASTE_HISTORY_CACHE)) {
                List<byte[]> packedBytes = jedis.lrange(KEY_PASTE_HISTORY_CACHE.getBytes(StandardCharsets.UTF_8),
                        0, HISTORY_CACHE_SIZE - 1);
                return getUnpackedHistory(packedBytes);
            }

            List<String> itemIds = jedis.lrange(KEY_PASTE_HISTORY, startIndex, startIndex + count);
            List<Long> itemsAsLong = new ArrayList<>(itemIds.size());
            for (String itemId : itemIds) {
                itemsAsLong.add(Long.parseLong(itemId));
            }

            List<PasteItem> historyList = ds.find(PasteItem.class)
                    .filter(Filters.in("pasteIndex", itemsAsLong))
                    .stream(new FindOptions()
                            .sort(Sort.descending("pasteIndex"))
                            .limit(count))
                    .toList();

            // The cache hasn't been filled yet, so fill it
            if (startIndex == 0) {
                List<byte[]> packedHistory = getHistoryPacked(historyList);
                if (packedHistory != null) {
                    for (int i = packedHistory.size() - 1; i >= 0; i--) {
                        jedis.lpush(KEY_PASTE_HISTORY_CACHE.getBytes(StandardCharsets.UTF_8), packedHistory.get(i));
                    }
                }
            }
            return historyList;
        } catch (JedisConnectionException je) {
            logger.error("Unable to reach Redis while reading the paste history", je);
        }

        return null;
    }

    @Override
    public long count() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.llen(KEY_PASTE_HISTORY);
        } catch (JedisConnectionException je) {
            logger.error("Unable to reach Redis while counting pastes", je);
        }

        return -1;
    }

    @Override
    public int incViewCount(PasteItem pasteItem) {
        logger.info("Incrementing view count for pasteId: {}", pasteItem.getItemId());
        try {
            PasteItem resultItem = ds.find(PasteItem.class)
                    .filter(Filters.eq("itemId", pasteItem.getItemId()))
                    .modify(new ModifyOptions().returnDocument(ReturnDocument.AFTER),
                            UpdateOperators.inc("viewCount", 1));

            return resultItem != null ? resultItem.getViewCount() : -1;
        } catch (Exception e) {
            logger.error("Unable to increment the view count", e);
            return -1;
        }
    }

    @Override
    public void increaseAbuseCount(PasteItem pasteItem) {
        logger.info("Incrementing abuse count for pasteId: {}", pasteItem.getItemId());

        try (Jedis jedis = jedisPool.getResource()) {
            PasteItem modifiedPasteItem = ds.find(PasteItem.class)
                    .filter(Filters.eq("itemId", pasteItem.getItemId()))
                    .modify(new ModifyOptions().returnDocument(ReturnDocument.AFTER),
                            UpdateOperators.inc("abuseCount", 1));

            if (modifiedPasteItem != null && modifiedPasteItem.getAbuseCount() > 1) {
                logger.info("Removing paste [{}] because of abuseCount: {}",
                        modifiedPasteItem.getItemId(), modifiedPasteItem.getAbuseCount());
                jedis.lrem(KEY_PASTE_HISTORY, 1, Long.toString(modifiedPasteItem.getPasteIndex()));
            }
        } catch (JedisConnectionException je) {
            logger.error("Unable to reach Redis while flagging a paste for abuse", je);
        } catch (Exception e) {
            logger.error("Unable to increase the abuse count", e);
        }
    }

    @Override
    public void decreaseAbuseCount(PasteItem pasteItem) {
        logger.info("Decrementing abuse count for pasteId: {}", pasteItem.getItemId());
        try {
            PasteItem modifiedPasteItem = ds.find(PasteItem.class)
                    .filter(Filters.eq("itemId", pasteItem.getItemId()))
                    .modify(new ModifyOptions().returnDocument(ReturnDocument.AFTER),
                            UpdateOperators.dec("abuseCount"));

            if (modifiedPasteItem != null && modifiedPasteItem.getAbuseCount() > 1) {
                logger.info("Restoring paste [{}] because of abuseCount: {}",
                        modifiedPasteItem.getItemId(), modifiedPasteItem.getAbuseCount());
            }
        } catch (Exception e) {
            logger.error("Unable to decrease the abuse count", e);
        }
    }

    @Override
    public List<PasteItem> getChildren(PasteItem pasteItem) {
        logger.info("Getting children with pasteId: {}", pasteItem.getItemId());
        Query<PasteItem> queryItems = ds.find(PasteItem.class)
                .filter(Filters.eq("parent", pasteItem.getItemId()),
                        Filters.lt("abuseCount", 2));

        return queryItems.stream(new FindOptions()
                        .projection().exclude("content")
                        .sort(Sort.descending("timestamp")))
                .toList();
    }

    /**
     * Trimmed down projection of a paste used for the Redis backed history cache.
     */
    public static class PasteItemCache {
        public String itemId;
        public int numberOfLines;
        public Date timestamp;
        public String content;

        @Override
        public String toString() {
            return "PasteItemCache{" +
                    "itemId='" + itemId + '\'' +
                    ", numberOfLines=" + numberOfLines +
                    ", timestamp=" + timestamp +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

}
