package com.mysticcoders.mysticpaste.persistence.mongo;

import com.google.code.morphia.DatastoreImpl;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.mongodb.Mongo;
import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import com.mysticcoders.mysticpaste.utils.TokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 */
public class PasteItemDaoImpl implements PasteItemDao {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected DatastoreImpl ds;

    protected JedisPool jedisPool;

    private int tokenLength = 10;

    public PasteItemDaoImpl(Mongo mongo, Morphia morphia, String dbName, JedisPool jedisPool) {
        ds = new DatastoreImpl(morphia, mongo, dbName);
        ds.getMapper().addMappedClass(PasteItem.class);
        this.jedisPool = jedisPool;
    }

    public String create(PasteItem item) {
        Jedis jedis = jedisPool.getResource();
        try {
            long pasteIndex = jedis.incr("pasteIndex");
            jedis.lpush("pasteHistory", "" + pasteIndex);
            String tokenId = TokenGenerator.generateToken(tokenLength);
            item.setItemId(tokenId);
            item.setPasteIndex(pasteIndex);

            ds.insert(item);
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

    public List<PasteItem> find(int count, int startIndex, String filter) {

        Jedis jedis = jedisPool.getResource();
        try {
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
            return query.asList();
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
        Jedis jedis = jedisPool.getResource();

        try {
            Query<PasteItem> query = ds.find(PasteItem.class).field("itemId").equal(pasteItem.getItemId());
            UpdateOperations<PasteItem> updateOp = ds.createUpdateOperations(PasteItem.class).inc("abuseCount", 1);

            PasteItem modifiedPasteItem = ds.findAndModify(query, updateOp);
            if (modifiedPasteItem.getAbuseCount() > 1) {
                System.out.println("Removing paste [" + modifiedPasteItem.getItemId() + "] because of abuseCount:" + modifiedPasteItem.getAbuseCount());
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
        try {
            Query<PasteItem> query = ds.find(PasteItem.class).field("itemId").equal(pasteItem.getItemId());
            UpdateOperations<PasteItem> updateOp = ds.createUpdateOperations(PasteItem.class).dec("abuseCount");

            PasteItem modifiedPasteItem = ds.findAndModify(query, updateOp);
            if (modifiedPasteItem.getAbuseCount() > 1) {
                System.out.println("Restoring paste [" + modifiedPasteItem.getItemId() + "] because of abuseCount:" + modifiedPasteItem.getAbuseCount());
// TODO figure out how we can fix the list and how to include the paste in the proper spot
//                redisTemplate.opsForList().
//                redisTemplate.opsForList().remove("pasteHistory", 1, "" + modifiedPasteItem.getPasteIndex());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PasteItem> getChildren(PasteItem pasteItem) {
        Query<PasteItem> queryItems = ds.createQuery(PasteItem.class)
                .retrievedFields(false, "content")
                .field("parent").equal(pasteItem.getItemId())
                .field("abuseCount").lessThan(2)
                .order("-timestamp");

        return queryItems.asList();
    }

}
