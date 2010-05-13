package com.mysticcoders.mysticpaste.persistence.gae;

import com.mysticcoders.mysticpaste.model.PasteComment;
import com.mysticcoders.mysticpaste.persistence.PasteCommentDao;

import java.util.List;

/**
 * User: Andrew Lombardi
 * Date: May 8, 2010
 * Time: 6:42:16 PM
 * <p/>
 * Copyright 2010, Mystic Coders, LLC
 */
public class PasteCommentDaoImpl implements PasteCommentDao {

    String namespace = "default";

    public PasteCommentDaoImpl() {
    }

    //TODO check for nulls people

    public void save(PasteComment comment) {
/*
        Long pasteItemId = comment.getItem().getId();
        IMap map = Hazelcast.getMap(namespace);

        PasteItem pasteItem = (PasteItem)map.get(pasteItemId);

        pasteItem.addComment( comment );

        map.put(pasteItemId, pasteItem);
*/
    }

    @SuppressWarnings("unchecked")
    public int countComments(Long pasteItemId) {
/*
        IMap map = Hazelcast.getMap(namespace);

        PasteItem pasteItem = (PasteItem)map.get(pasteItemId);

        if(pasteItem.getComments() != null) {
            return pasteItem.getComments().size();
        }
*/

        return 0;
    }

    public List<PasteComment> findByPasteId(Long pasteItemId) {
/*
        IMap map = Hazelcast.getMap(namespace);

        PasteItem pasteItem = (PasteItem)map.get(pasteItemId);

        return pasteItem.getComments();
*/

        return null;
    }
}