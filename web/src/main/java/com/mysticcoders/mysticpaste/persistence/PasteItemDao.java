package com.mysticcoders.mysticpaste.persistence;

import com.mysticcoders.mysticpaste.model.PasteItem;

import java.util.List;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @version $Revision$ $Date$
 */
public interface PasteItemDao {

    String create(PasteItem item);

    PasteItem get(String id);

    List<PasteItem> find(int count, int startIndex);

    long count();

    int incViewCount(PasteItem pasteItem);

    void markAbuse(PasteItem pasteItem);

    List<PasteItem> getChildren(PasteItem pasteItem);
}
