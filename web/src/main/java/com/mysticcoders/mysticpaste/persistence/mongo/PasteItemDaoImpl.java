package com.mysticcoders.mysticpaste.persistence.mongo;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import com.mysticcoders.mysticpaste.utils.TokenGenerator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created with IntelliJ IDEA.
 */
@Repository("mongoPasteItemDao")
public class PasteItemDaoImpl implements PasteItemDao {

    @Resource(name = "mongoTemplate")
    private MongoTemplate mongoTemplate;

    @Resource(name = "redisTemplate")
    private StringRedisTemplate redisTemplate;

    private int tokenLength = 10;

    public String create(PasteItem item) {
        try {
            long pasteIndex = redisTemplate.opsForValue().increment("pasteIndex", 1);
            redisTemplate.opsForList().leftPush("pasteHistory", "" + pasteIndex);
            String tokenId = TokenGenerator.generateToken(tokenLength);
            item.setItemId(tokenId);
            item.setPasteIndex(pasteIndex);
            mongoTemplate.insert(item, "pastes");
            return tokenId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAdminPassword() {
        return redisTemplate.opsForValue().get("pasteAdminPw");
    }

    public PasteItem get(String id) {
        Query query = new Query(where("itemId").is(id));
        return mongoTemplate.findOne(query, PasteItem.class, "pastes");
    }

    private long getPasteIndex() {
        return Long.parseLong(redisTemplate.opsForValue().get("pasteIndex"));
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
        List<String> itemIds = redisTemplate.opsForList().range("pasteHistory", startIndex, startIndex + count);
        List<Long> itemsAsLong = new ArrayList<Long>(itemIds.size());
        // Convert from String to Long so Mongo doesn't complain (TODO change this cause it sucks)
        for (String itemId : itemIds) {
            itemsAsLong.add(Long.parseLong(itemId));
        }
        Query query = new Query(where("pasteIndex").in(itemsAsLong));
        query.sort().on("pasteIndex", Order.DESCENDING);
        query.limit(count);
        return mongoTemplate.find(query, PasteItem.class, "pastes");
    }

    public long count() {
        return redisTemplate.opsForList().size("pasteHistory");
/*
        Query query = new Query(where("abuseCount").lte(2).and("privateFlag").is(false));
        long count = mongoTemplate.count(query, "pastes");
        return count;
*/
    }

    public int incViewCount(PasteItem pasteItem) {
        try {
            Query query = new Query(where("itemId").is(pasteItem.getItemId()));
            Update update = new Update();
            update.inc("viewCount", 1);
            PasteItem resultItem = mongoTemplate.findAndModify(query, update, PasteItem.class, "pastes");
            return resultItem.getViewCount();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void increaseAbuseCount(PasteItem pasteItem) {
        try {
            Query query = new Query(where("itemId").is(pasteItem.getItemId()));
            Update update = new Update();
            update.inc("abuseCount", 1);
            PasteItem modifiedPasteItem = mongoTemplate.findAndModify(query, update, PasteItem.class, "pastes");
            if (modifiedPasteItem.getAbuseCount() > 1) {
                System.out.println("Removing paste [" + modifiedPasteItem.getItemId() + "] because of abuseCount:" + modifiedPasteItem.getAbuseCount());
                redisTemplate.opsForList().remove("pasteHistory", 1, "" + modifiedPasteItem.getPasteIndex());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decreaseAbuseCount(PasteItem pasteItem) {
        try {
            Query query = new Query(where("itemId").is(pasteItem.getItemId()));
            Update update = new Update();
            update.inc("abuseCount", -1);
            PasteItem modifiedPasteItem = mongoTemplate.findAndModify(query, update, PasteItem.class, "pastes");
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
        Query query = new Query(where("abuseCount").lt(2).and("parent").is(pasteItem.getItemId()));
        query.sort().on("timestamp", Order.DESCENDING);
        return mongoTemplate.find(query, PasteItem.class, "pastes");
    }

}
