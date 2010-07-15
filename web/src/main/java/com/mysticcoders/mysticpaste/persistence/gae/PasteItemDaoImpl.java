package com.mysticcoders.mysticpaste.persistence.gae;

import com.google.appengine.api.datastore.Key;
import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.model.gae.SimpleObject;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import org.simpleds.EntityManager;
import org.simpleds.SimpleQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: Andrew Lombardi
 * Date: May 8, 2010
 * Time: 6:42:46 PM
 * <p/>
 * Copyright 2010, Mystic Coders, LLC
 */
public class PasteItemDaoImpl implements PasteItemDao {

    String namespace = "default";

//    @Autowired
    private EntityManager entityManager;


    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Long createSimpleObject(String content) {
        SimpleObject so = new SimpleObject();
        so.setContent(content);

        Key key = entityManager.put(so);

        return key.getId();
    }

    public SimpleObject retrieveSimpleObject(Long id) {
        SimpleQuery simpleQuery = entityManager.createQuery(SimpleObject.class);


        List<SimpleObject> list = entityManager.find(simpleQuery);

        SimpleObject foundObj;
        System.out.println("list:"+list);

        for(SimpleObject simpleObject : list) {
            System.out.println("simpleObject:"+simpleObject);
            if(simpleObject.getId().getId() == id) {
                return simpleObject;
            }
        }

        return null;
    }

    public Long create(PasteItem item) {

        System.out.println("entityManager:" + entityManager);

        Key key = entityManager.put("hithere");
        System.out.println("key:" + key);
/*
        IMap map = Hazelcast.getMap(namespace);

        item.setId(((MysticPasteHazelcastApplication) Application.get()).nextId());

        map.put(item.getId(), item);

        if(item.isPrivate()) {
            map.put(item.getPrivateToken(), item.getId());
        } else {
            List<Long> indexList = ((MysticPasteHazelcastApplication) Application.get()).getIndexList();
            indexList.add(item.getId());
        }

        return item.getId();
*/

        return 0L;
    }

    public void update(PasteItem item) {
        throw new UnsupportedOperationException("update is not used");
    }

    public PasteItem get(long id) {
/*
        IMap map = Hazelcast.getMap(namespace);
        return (PasteItem) map.get(id);
*/

        return null;
    }

    public List<PasteItem> find(int count, int startIndex) {
/*
        IMap map = Hazelcast.getMap(namespace);

        List<Long> list = ((MysticPasteHazelcastApplication) Application.get()).getIndexList();

        if(list == null) return Collections.EMPTY_LIST;

        // CopyOnWriteArrayList doesn't give us the ability to sort and reverse, so we put in a regular ArrayList for the "find"
        List<Long> unsafeList = new ArrayList<Long>(list);
        Collections.sort(unsafeList);
        Collections.reverse(unsafeList);

        List<Long> pageIds = unsafeList.subList(startIndex, (unsafeList.size() < (startIndex + count) ? unsafeList.size() : (startIndex + count)));
        List<PasteItem> pagedList = new ArrayList<PasteItem>();

        for (Long pasteItemId : pageIds) {
            pagedList.add((PasteItem) map.get(pasteItemId));
        }

        return pagedList;
*/

        return null;
    }

    public List<PasteItem> findThreaded(int count, int startIndex) {
        throw new UnsupportedOperationException("findThreaded is not supported yet");
    }

    public PasteItem findByToken(String privateToken) {
/*
        IMap map = Hazelcast.getMap(namespace);

        Long itemId = (Long)map.get(privateToken);

        return (PasteItem)map.get(itemId);
*/

        return null;
    }

    public List<PasteItem> findByUser(String userToken) {
        throw new UnsupportedOperationException("findByUser is not supported yet");
    }

    public long getPasteCount() {
/*
//        IMap map = Hazelcast.getMap(namespace);

        List list = ((MysticPasteHazelcastApplication) Application.get()).getIndexList();

//        List list = (List)map.get("index");
        return list!=null ? list.size() : 0;
*/
        return 0L;
    }

    public void markAbuse(PasteItem pasteItem) {
/*
        IMap map = Hazelcast.getMap(namespace);

        PasteItem item = (PasteItem) map.get(pasteItem.getId());

        item.markAbuse();
*/
    }

}