package com.mysticcoders.mysticpaste.persistence.mongo;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import com.mysticcoders.mysticpaste.utils.TokenGenerator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created with IntelliJ IDEA.
 * User: kinabalu
 * Date: 11/11/12
 * Time: 8:42 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository("mongoPasteItemDao")
public class PasteItemDaoImpl implements PasteItemDao {

    @Resource(name = "mongoTemplate")
    private MongoTemplate mongoTemplate;

    private int tokenLength = 10;

    public String create(PasteItem item) {
        try {
            String tokenId = TokenGenerator.generateToken(tokenLength);
            item.setItemId(tokenId);
            mongoTemplate.insert(item, "pastes");
            return tokenId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public PasteItem get(String id) {
        Query query = new Query(where("itemId").is(id));
        // Execute the query and find one matching entry
        return mongoTemplate.findOne(query, PasteItem.class, "pastes");
    }

    public List<PasteItem> find(int count, int startIndex) {
        Query query = new Query(where("abuseFlag").is(false).and("private").is(false));
        query.sort().on("timestamp", Order.DESCENDING);
        query.skip(startIndex);
        // Execute the query and find all matching entries
        List<PasteItem> items = mongoTemplate.find(query, PasteItem.class, "pastes");
        return items;
    }

    public long count() {
        Query query = new Query(where("abuseFlag").is(false).and("private").is(false));
        long count = mongoTemplate.count(query, "pastes");
        return count;
    }

    public void markAbuse(PasteItem pasteItem) {
        try {
            Query query = new Query(where("itemId").is(pasteItem.getItemId()));
            Update update = new Update();
            update.set("abuseFlag", true);
            mongoTemplate.upsert(query, update, "pastes");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PasteItem> getChildren(PasteItem pasteItem) {
        Query query = new Query(where("abuseFlag").is(false).and("parent").is(pasteItem.getItemId()));
        query.sort().on("timestamp", Order.DESCENDING);
        // Execute the query and find all matching entries
        List<PasteItem> items = mongoTemplate.find(query, PasteItem.class, "pastes");
        return items;
    }

}
