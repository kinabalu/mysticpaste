package com.mysticcoders.mysticpaste.web.pages.history;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.PasteService;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Iterator;

/**
 * Data Provider for display of History data.
 *
 * @author Steve Forsyth
 * Date: Mar 9, 2009
 */
public class HistoryDataProvider implements IDataProvider<PasteItem> {

    PasteService pasteService;

    boolean visible = false;

    public HistoryDataProvider(PasteService pasteService) {
        this.pasteService = pasteService;
        System.out.println("pasteService:"+pasteService);
    }

    public Iterator<PasteItem> iterator(long first, long count) {
        System.out.println("First: " + first + ", Count:" + count);
        Iterator<PasteItem> items = pasteService.getLatestItems("web", (int)count, (int)first).iterator();
        System.out.println("items:"+items.hasNext());
        return items;
    }

    public long size() {
        int count = new Long(pasteService.getLatestItemsCount("web")).intValue();

        System.out.println("size:"+count);
        visible = count > 0;

        return count;
    }

    public boolean isVisible() {
        return visible;
    }

    public IModel<PasteItem> model(PasteItem pasteItem) {
        return new Model<PasteItem>(pasteItem);
    }

    /**
     * @see org.apache.wicket.model.IDetachable#detach()
     */
    public void detach() {
    }
}
