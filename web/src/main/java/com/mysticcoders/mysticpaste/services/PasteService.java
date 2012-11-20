package com.mysticcoders.mysticpaste.services;

import com.mysticcoders.mysticpaste.model.PasteItem;

import java.util.List;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @version $Revision$ $Date$
 */
public interface PasteService {

    List<PasteItem> getLatestItems(String clientToken, int count, int startIndex) ;

    PasteItem getItem(String clientToken, String id) ;

    String createItem(String clientToken, PasteItem item);

    String createItem(String clientToken, PasteItem item, boolean twitter);

    String createReplyItem(String clientToken, PasteItem item, String parentId)
            throws ParentNotFoundException;

    long getLatestItemsCount(String clientToken);

    int incViewCount(PasteItem pasteItem);

    void markAbuse(PasteItem pasteItem);

    List<PasteItem> hasChildren(PasteItem pasteItem);

}
