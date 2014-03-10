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

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 */
//@Repository("mongoPasteItemDao")
public class PasteItemDaoImpl implements PasteItemDao {

    private final Logger logger = LoggerFactory.getLogger(getClass());

//    @Resource(name = "mongoTemplate")
//    private MongoTemplate mongoTemplate;

//    @Resource(name = "redisTemplate")
//    private StringRedisTemplate redisTemplate;

//    @Resource(name = "jedisPool")
//    private JedisPool jedisPool;

    protected DatastoreImpl ds;

//    @Resource(name = "mongo")
//    private Mongo mongo;

//    @Resource(name = "morphia")
//    private Morphia morphia;

    protected Jedis jedis;
    private int tokenLength = 10;

    public PasteItemDaoImpl(Mongo mongo, Morphia morphia, String dbName, JedisPool jedisPool) {
        ds = new DatastoreImpl(morphia, mongo, dbName);
        ds.getMapper().addMappedClass(PasteItem.class);
        jedis = jedisPool.getResource();

    }

    public String create(PasteItem item) {
        try {
            long pasteIndex = jedis.incr("pasteIndex");
            jedis.lpush("pasteHistory", "" + pasteIndex);
//                    redisTemplate.opsForValue().increment("pasteIndex", 1);
//            redisTemplate.opsForList().leftPush("pasteHistory", "" + pasteIndex);
            String tokenId = TokenGenerator.generateToken(tokenLength);
            item.setItemId(tokenId);
            item.setPasteIndex(pasteIndex);

            ds.insert(item);
//            mongoTemplate.insert(item, "pastes");
            return tokenId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAdminPassword() {
        return jedis.get("pasteAdminPw");
//        return redisTemplate.opsForValue().get("pasteAdminPw");
    }

    public PasteItem get(String id) {
        PasteItem pasteItem =
                ds.find(PasteItem.class).field("itemId").equal(id).get();
        return pasteItem;
//        Query query = new Query(where("itemId").is(id));
//        return mongoTemplate.findOne(query, PasteItem.class, "pastes");
    }

    private long getPasteIndex() {
        return Long.parseLong(jedis.get("pasteIndex"));
//        return Long.parseLong(redisTemplate.opsForValue().get("pasteIndex"));
    }

    public List<PasteItem> find(int count, int startIndex) {
/*
        Query query = new Query(where("abuseCount").lte(2).and("privateFlag").is(false));
        query.sort().on("timestamp", Order.DESCENDING);
        query.skip(startIndex);
        query.limit(count);
        // Execute the query and find all matching entries
        List<PasteItem> items = mongoTemplate.find(query, PasteItem.class, "pastes");
        return items;
*/
        long beginItemIdsRedis = System.currentTimeMillis();
        List<String> itemIds = jedis.lrange("pasteHistory", startIndex, startIndex + count);
//        List<String> itemIds = redisTemplate.opsForList().range("pasteHistory", startIndex, startIndex + count);
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

        /*
        Query query = new Query(where("pasteIndex").in(itemsAsLong));
        query.sort().on("pasteIndex", Order.DESCENDING);
        query.limit(count);
        List<PasteItem> items = mongoTemplate.find(query, PasteItem.class, "pastes");
        */
        logger.debug("Time taken to query and retrieve pastes:" + (System.currentTimeMillis() - beginMongoQuery));
        return query.asList();
//        return items;
    }

    public long count() {
        long beginGetCount = System.currentTimeMillis();
        long count = jedis.llen("pasteHistory");
//        long count = redisTemplate.opsForList().size("pasteHistory");
        logger.debug("Time taken to get redis:pasteHistory count:" + (System.currentTimeMillis() - beginGetCount));
        return count;
/*
        Query query = new Query(where("abuseCount").lte(2).and("privateFlag").is(false));
        long count = mongoTemplate.count(query, "pastes");
        return count;
*/
    }

    public int incViewCount(PasteItem pasteItem) {
        try {
            Query<PasteItem> query = ds.find(PasteItem.class).field("itemId").equal(pasteItem.getItemId());
            UpdateOperations<PasteItem> updateOp = ds.createUpdateOperations(PasteItem.class).inc("viewCount", 1);
//            Query query = new Query(where("itemId").is(pasteItem.getItemId()));
            PasteItem resultItem = ds.findAndModify(query, updateOp);

/*
            Update update = new Update();
            update.inc("viewCount", 1);
            PasteItem resultItem = mongoTemplate.findAndModify(query, update, PasteItem.class, "pastes");
*/
            return resultItem.getViewCount();
//            return resultItem.getViewCount();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void increaseAbuseCount(PasteItem pasteItem) {
        try {
            Query<PasteItem> query = ds.find(PasteItem.class).field("itemId").equal(pasteItem.getItemId());
            UpdateOperations<PasteItem> updateOp = ds.createUpdateOperations(PasteItem.class).inc("abuseCount", 1);

/*
            Query query = new Query(where("itemId").is(pasteItem.getItemId()));
            Update update = new Update();
            update.inc("abuseCount", 1);
            PasteItem modifiedPasteItem = mongoTemplate.findAndModify(query, update, PasteItem.class, "pastes");
*/
            PasteItem modifiedPasteItem = ds.findAndModify(query, updateOp);
            if (modifiedPasteItem.getAbuseCount() > 1) {
                System.out.println("Removing paste [" + modifiedPasteItem.getItemId() + "] because of abuseCount:" + modifiedPasteItem.getAbuseCount());
                jedis.lrem("pasteHistory", 1, "" + modifiedPasteItem.getPasteIndex());
//                redisTemplate.opsForList().remove("pasteHistory", 1, "" + modifiedPasteItem.getPasteIndex());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decreaseAbuseCount(PasteItem pasteItem) {
        try {
            Query<PasteItem> query = ds.find(PasteItem.class).field("itemId").equal(pasteItem.getItemId());
            UpdateOperations<PasteItem> updateOp = ds.createUpdateOperations(PasteItem.class).dec("abuseCount");

/*
            Query query = new Query(where("itemId").is(pasteItem.getItemId()));
            Update update = new Update();
            update.inc("abuseCount", -1);
            PasteItem modifiedPasteItem = mongoTemplate.findAndModify(query, update, PasteItem.class, "pastes");
*/
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
//        System.out.println("pasteItem.id:"+pasteItem.getItemId());
        Query<PasteItem> queryItems = ds.createQuery(PasteItem.class)
                .retrievedFields(false, "content")
                .field("parent").equal(pasteItem.getItemId())
                .field("abuseCount").lessThan(2)
                .order("-timestamp")
                ;

        List<PasteItem> items = queryItems.asList();
//        System.out.println("pasteitem.list:" + items);

        return items;
/*
        Query query = new Query(where("abuseCount").lt(2).and("parent").is(pasteItem.getItemId()));
        query.sort().on("timestamp", Order.DESCENDING);
        return mongoTemplate.find(query, PasteItem.class, "pastes");
*/
//        return new ArrayList<PasteItem>();
    }

}
