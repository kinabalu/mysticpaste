package com.mysticcoders.mysticpaste.services;

import com.mysticcoders.mysticpaste.model.PasteItem;

import java.util.List;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @version $Revision$ $Date$
 */
public interface PasteService {

    List<PasteItem> getLatestItems(int count, int startIndex);

    List<PasteItem> getLatestItems(int count, int startIndex, String filter);

    PasteItem getItem(String id);

    String createItem(PasteItem item);

    String createItem(PasteItem item, boolean twitter);

    String createReplyItem(PasteItem item, String parentId)
            throws ParentNotFoundException;

    long getLatestItemsCount();

    int incViewCount(PasteItem pasteItem);

    void increaseAbuseCount(PasteItem pasteItem);

    void decreaseAbuseCount(PasteItem pasteItem);

    List<PasteItem> hasChildren(PasteItem pasteItem);

    String getAdminPassword();
}
