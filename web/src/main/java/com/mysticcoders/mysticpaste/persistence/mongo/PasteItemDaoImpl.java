package com.mysticcoders.mysticpaste.persistence.mongo;

import com.google.code.morphia.DatastoreImpl;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.mongodb.Mongo;
import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import com.mysticcoders.mysticpaste.utils.TokenGenerator;
import org.msgpack.MessagePack;
import org.msgpack.annotation.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 */
public class PasteItemDaoImpl implements PasteItemDao {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected DatastoreImpl ds;

    protected JedisPool jedisPool;

    protected MessagePack msgpack;

    private int tokenLength = 10;

    public PasteItemDaoImpl(Mongo mongo, Morphia morphia, String dbName, JedisPool jedisPool) {
        ds = new DatastoreImpl(morphia, mongo, dbName);
        ds.getMapper().addMappedClass(PasteItem.class);
        this.jedisPool = jedisPool;
        msgpack = new MessagePack();
    }

    public void appendIpAddress(String ipAddress) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.sadd("ipAddresses", ipAddress);
        } catch (JedisConnectionException je) {
            if (null != jedis) {
                jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (null != jedis) {
                jedisPool.returnResource(jedis);
            }
        }
    }


    public String create(PasteItem item) {
        Jedis jedis = jedisPool.getResource();
        try {
            long pasteIndex = jedis.incr("pasteIndex");
            String tokenId = TokenGenerator.generateToken(tokenLength);
            item.setItemId(tokenId);
            item.setPasteIndex(pasteIndex);

            logger.info("Creating paste with itemId: " + tokenId + " pasteIndex: " + pasteIndex);
            ds.insert(item);

            if(!item.isPrivate()) {
                logger.info("ITEM IS NOT PRIVATE");
                jedis.lpush("pasteHistory", "" + pasteIndex);
                byte[] packedEntry = packEntry(item);
                logger.info(new String(packedEntry));
                jedis.rpop("pasteHistoryCache".getBytes());
                jedis.lpush("pasteHistoryCache".getBytes(), packedEntry);
            }

            return tokenId;
        } catch (JedisConnectionException je) {
            if (null != jedis) {
                jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != jedis) {
                jedisPool.returnResource(jedis);
            }
        }
        return null;
    }

    public String getAdminPassword() {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.get("pasteAdminPw");
        } catch (JedisConnectionException je) {
            if (null != jedis) {
                jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (null != jedis) {
                jedisPool.returnResource(jedis);
            }
        }

        return null;
    }

    public PasteItem get(String id) {
        logger.info("Retrieving paste ID: " + id);
        PasteItem pasteItem =
                ds.find(PasteItem.class).field("itemId").equal(id).get();
        return pasteItem;
    }

    private long getPasteIndex() {
        Jedis jedis = jedisPool.getResource();
        try {
            return Long.parseLong(jedis.get("pasteIndex"));
        } catch (JedisConnectionException je) {
            if (null != jedis) {
                jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (null != jedis) {
                jedisPool.returnResource(jedis);
            }
        }

        return -1;
    }

    @Message
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

    private byte[] packEntry(PasteItem item) throws IOException {
        PasteItemCache cache = new PasteItemCache();
        cache.itemId = item.getItemId();
        cache.numberOfLines = item.getContentLineCount();
        cache.timestamp = item.getTimestamp();
        cache.content = item.getContent();
        return msgpack.write(cache);
    }

    private List<byte[]> getHistoryPacked(List<PasteItem> historyList) {
        try {
            List<byte[]> cacheItems = new ArrayList<byte[]>();
            for(PasteItem item : historyList) {
                cacheItems.add(packEntry(item));
            }
            return cacheItems;
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<PasteItem> getUnpackedHistory(List<byte[]> packedHistory) {
        List<PasteItem> pasteItems = new ArrayList<PasteItem>();
        try {
            for(byte[] packed : packedHistory) {
                PasteItemCache itemCache = msgpack.read(packed, PasteItemCache.class);
                PasteItem item = new PasteItem();
                item.setItemId(itemCache.itemId);
                item.setTimestamp(itemCache.timestamp);
                item.setContent(itemCache.content);
                pasteItems.add(item);
            }

            return pasteItems;
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<PasteItem> find(int count, int startIndex, String filter) {

        logger.info("Pulling paste history: " + count + " record(s) starting " + startIndex);
        Jedis jedis = jedisPool.getResource();

        try {
            if(startIndex == 0 && jedis.exists("pasteHistoryCache")) {
                List<byte[]> packedBytes = jedis.lrange("pasteHistoryCache".getBytes(), 0, 4);
                return getUnpackedHistory(packedBytes);
            }
            long beginItemIdsRedis = System.currentTimeMillis();
            List<String> itemIds = jedis.lrange("pasteHistory", startIndex, startIndex + count);
            List<Long> itemsAsLong = new ArrayList<Long>(itemIds.size());
            // Convert from String to Long so Mongo doesn't complain (TODO change this cause it sucks)
            for (String itemId : itemIds) {
                itemsAsLong.add(Long.parseLong(itemId));
            }
            logger.debug("Time taken to grab redis:pasteHistory:" + (System.currentTimeMillis() - beginItemIdsRedis));

            long beginMongoQuery = System.currentTimeMillis();
            Query<PasteItem> query = ds.find(PasteItem.class).field("pasteIndex").in(itemsAsLong)
                    .order("-pasteIndex")
                    .limit(count);

            logger.debug("Time taken to query and retrieve pastes:" + (System.currentTimeMillis() - beginMongoQuery));

            List<PasteItem> historyList = query.asList();

            // The cache hasn't been filled yet, so fills it
            if(startIndex == 0) {
                List<byte[]> packedHistory = getHistoryPacked(historyList);
                for(int i=packedHistory.size() - 1; i>=0; i--) {
                    byte[] pack = packedHistory.get(i);
                    jedis.lpush("pasteHistoryCache".getBytes(), pack);
                }
            }
            return historyList;
        } catch (JedisConnectionException je) {
            if (null != jedis) {
                jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (null != jedis) {
                jedisPool.returnResource(jedis);
            }
        }

        return null;

    }

    public long count() {
        Jedis jedis = jedisPool.getResource();
        try {
            long beginGetCount = System.currentTimeMillis();
            long count = jedis.llen("pasteHistory");
            logger.debug("Time taken to get redis:pasteHistory count:" + (System.currentTimeMillis() - beginGetCount));
            return count;
        } catch (JedisConnectionException je) {
            if (null != jedis) {
                jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (null != jedis) {
                jedisPool.returnResource(jedis);
            }
        }

        return -1;
    }

    public int incViewCount(PasteItem pasteItem) {
        logger.info("Incrementing view count for pasteId:" + pasteItem.getItemId());
        try {
            Query<PasteItem> query = ds.find(PasteItem.class).field("itemId").equal(pasteItem.getItemId());
            UpdateOperations<PasteItem> updateOp = ds.createUpdateOperations(PasteItem.class).inc("viewCount", 1);
            PasteItem resultItem = ds.findAndModify(query, updateOp);

            return resultItem.getViewCount();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void increaseAbuseCount(PasteItem pasteItem) {
        logger.info("Incrementing abuse count for pasteId:" + pasteItem.getItemId());
        Jedis jedis = jedisPool.getResource();

        try {
            Query<PasteItem> query = ds.find(PasteItem.class).field("itemId").equal(pasteItem.getItemId());
            UpdateOperations<PasteItem> updateOp = ds.createUpdateOperations(PasteItem.class).inc("abuseCount", 1);

            PasteItem modifiedPasteItem = ds.findAndModify(query, updateOp);
            if (modifiedPasteItem.getAbuseCount() > 1) {
                logger.info("Removing paste [" + modifiedPasteItem.getItemId() + "] because of abuseCount:" + modifiedPasteItem.getAbuseCount());
                jedis.lrem("pasteHistory", 1, "" + modifiedPasteItem.getPasteIndex());
            }
        } catch (JedisConnectionException je) {
            if (null != jedis) {
                jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != jedis) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public void decreaseAbuseCount(PasteItem pasteItem) {
        logger.info("Decrementing view count for pasteId:" + pasteItem.getItemId());
        try {
            Query<PasteItem> query = ds.find(PasteItem.class).field("itemId").equal(pasteItem.getItemId());
            UpdateOperations<PasteItem> updateOp = ds.createUpdateOperations(PasteItem.class).dec("abuseCount");

            PasteItem modifiedPasteItem = ds.findAndModify(query, updateOp);
            if (modifiedPasteItem.getAbuseCount() > 1) {
                logger.info("Restoring paste [" + modifiedPasteItem.getItemId() + "] because of abuseCount:" + modifiedPasteItem.getAbuseCount());
// TODO figure out how we can fix the list and how to include the paste in the proper spot
//                redisTemplate.opsForList().
//                redisTemplate.opsForList().remove("pasteHistory", 1, "" + modifiedPasteItem.getPasteIndex());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PasteItem> getChildren(PasteItem pasteItem) {
        logger.info("Getting children with pasteId:" + pasteItem.getItemId());
        Query<PasteItem> queryItems = ds.createQuery(PasteItem.class)
                .retrievedFields(false, "content")
                .field("parent").equal(pasteItem.getItemId())
                .field("abuseCount").lessThan(2)
                .order("-timestamp");

        return queryItems.asList();
    }

}
