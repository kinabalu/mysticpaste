/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Oct 15, 2009
 * Time: 11:23:31 AM
 */
package com.mysticcoders.mysticpaste.persistence.hazelcast;

import com.hazelcast.core.*;
import com.mysticcoders.mysticpaste.MysticPasteApplication;
import com.mysticcoders.mysticpaste.MysticPasteHazelcastApplication;
import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import org.apache.wicket.Application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public class PasteItemDaoImpl implements PasteItemDao {

    String namespace = "default";

    public Long create(PasteItem item) {
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
    }

    public void update(PasteItem item) {
        throw new UnsupportedOperationException("update is not used");
    }

    public PasteItem get(long id) {
        IMap map = Hazelcast.getMap(namespace);
        return (PasteItem) map.get(id);
    }

    public List<PasteItem> find(int count, int startIndex) {
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
    }

    public List<PasteItem> findThreaded(int count, int startIndex) {
        throw new UnsupportedOperationException("findThreaded is not supported yet");
    }

    public PasteItem findByToken(String privateToken) {
        IMap map = Hazelcast.getMap(namespace);

        Long itemId = (Long)map.get(privateToken);
                
        return (PasteItem)map.get(itemId);
    }

    public List<PasteItem> findByUser(String userToken) {
        throw new UnsupportedOperationException("findByUser is not supported yet");
    }

    public long getPasteCount() {
//        IMap map = Hazelcast.getMap(namespace);

        List list = ((MysticPasteHazelcastApplication) Application.get()).getIndexList();

//        List list = (List)map.get("index");
        return list!=null ? list.size() : 0;
    }

    public void markAbuse(PasteItem pasteItem) {
        IMap map = Hazelcast.getMap(namespace);

        PasteItem item = (PasteItem) map.get(pasteItem.getId());

        item.markAbuse();
    }
}