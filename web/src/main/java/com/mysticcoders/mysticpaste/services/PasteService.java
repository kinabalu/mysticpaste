package com.mysticcoders.mysticpaste.services;

import com.mysticcoders.mysticpaste.model.PasteItem;

import java.util.List;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @version $Revision$ $Date$
 */
public interface PasteService {

    List<PasteItem> getLatestItems(String clientToken, int count, int startIndex, boolean threaded) throws InvalidClientException;

    PasteItem getItem(String clientToken, long id) throws InvalidClientException;

    PasteItem findPrivateItem(String clientToken, String privateToken) throws InvalidClientException;

    List<PasteItem> findItemsByLanguage(String clientToken, String languageType, int count, int startIndex,
                                        boolean threaded)
            throws InvalidClientException;

    long createItem(String clientToken, PasteItem item) throws InvalidClientException;

    long createItem(String clientToken, PasteItem item, boolean twitter) throws InvalidClientException;

    long createReplyItem(String clientToken, PasteItem item, long parentId)
            throws InvalidClientException, ParentNotFoundException;

    List<PasteItem> getItemsForUser(String clientToken, String userToken) throws InvalidClientException;

    long getLatestItemsCount(String clientToken) throws InvalidClientException;

    void markAbuse(PasteItem pasteItem);

    List<PasteItem> hasChildren(PasteItem pasteItem);

    void detachItem(PasteItem pasteItem);
}
