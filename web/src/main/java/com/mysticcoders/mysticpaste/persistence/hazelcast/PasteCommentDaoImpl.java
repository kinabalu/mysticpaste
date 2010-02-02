/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Oct 15, 2009
 * Time: 11:34:21 AM
 */
package com.mysticcoders.mysticpaste.persistence.hazelcast;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;
import com.mysticcoders.mysticpaste.model.PasteComment;
import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.persistence.PasteCommentDao;

import java.util.List;

public class PasteCommentDaoImpl implements PasteCommentDao {

    String namespace = "default";

    public PasteCommentDaoImpl() {
    }

    //TODO check for nulls people
    public void save(PasteComment comment) {
        Long pasteItemId = comment.getItem().getId();
        IMap map = Hazelcast.getMap(namespace);

        PasteItem pasteItem = (PasteItem)map.get(pasteItemId);

        pasteItem.addComment( comment );

        map.put(pasteItemId, pasteItem);
    }

    @SuppressWarnings("unchecked")
    public int countComments(Long pasteItemId) {
        IMap map = Hazelcast.getMap(namespace);

        PasteItem pasteItem = (PasteItem)map.get(pasteItemId);

        if(pasteItem.getComments() != null) {
            return pasteItem.getComments().size();
        }

        return 0;
    }

    public List<PasteComment> findByPasteId(Long pasteItemId) {
        IMap map = Hazelcast.getMap(namespace);

        PasteItem pasteItem = (PasteItem)map.get(pasteItemId);

        return pasteItem.getComments();
    }
}