package com.mysticcoders.mysticpaste.persistence;

import com.google.appengine.api.datastore.Key;
import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.model.gae.SimpleObject;

import java.util.List;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @version $Revision$ $Date$
 */
public interface PasteItemDao {

    public Long createSimpleObject(String content);

    public SimpleObject retrieveSimpleObject(Long id);
    

    Long create(PasteItem item);

    void update(PasteItem item);

    PasteItem get(long id);

/*
    List<PasteItem> findByLanguage(LanguageEnum languageType, int count, int startIndex);

    List<PasteItem> findByLanguageThreaded(LanguageEnum languageType, int count, int startIndex);
*/

    List<PasteItem> find(int count, int startIndex);

    List<PasteItem> findThreaded(int count, int startIndex);

    PasteItem findByToken(String privateToken);

    List<PasteItem> findByUser(String userToken);

    long getPasteCount();

    void markAbuse(PasteItem pasteItem);
}
