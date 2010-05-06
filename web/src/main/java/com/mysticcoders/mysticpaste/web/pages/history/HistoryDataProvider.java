package com.mysticcoders.mysticpaste.web.pages.history;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.InvalidClientException;
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
    }

    public Iterator<PasteItem> iterator(int first, int count) {
        try {
            return pasteService.getLatestItems("web", count, first, false).iterator();
        } catch (InvalidClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int size() {
        try {
            int count = new Long(pasteService.getLatestItemsCount("web")).intValue();

            visible = count > 0;

            return count;
        } catch (InvalidClientException e) {
            e.printStackTrace();
        }
        return 0;
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
