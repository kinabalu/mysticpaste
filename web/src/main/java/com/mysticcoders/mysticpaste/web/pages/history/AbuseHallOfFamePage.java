package com.mysticcoders.mysticpaste.web.pages.history;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.InvalidClientException;
import com.mysticcoders.mysticpaste.services.PasteService;
import com.mysticcoders.mysticpaste.web.pages.BasePage;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: 2/11/11
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class AbuseHallOfFamePage extends BasePage {

    @SpringBean
    private PasteService pasteService;


    protected String getTitle() {
        return "Pastes - Spam Hall of Shame";
    }

    public AbuseHallOfFamePage() {
        super(AbuseHallOfFamePage.class);

        final AbuseDataProvider abuseProvider = new AbuseDataProvider(pasteService);

        DataView<PasteItem> abuseDataView = new DataView<PasteItem>("abuseList", abuseProvider, 10) {
            protected void populateItem(Item<PasteItem> item) {
                PasteItem abuseItem = item.getModelObject();

                StringBuilder urls = new StringBuilder();
                String abuseItemText = abuseItem.getContent();

                int httpIndex = 0;

                while(abuseItemText.indexOf("http://", httpIndex)!=-1) {
                    try {
                        urls.append(abuseItemText.substring(abuseItemText.indexOf("http://", httpIndex), abuseItemText.indexOf("\"", abuseItemText.indexOf("http://", httpIndex)+1)));
                        urls.append("<br />");
                    } catch(Exception e) {
                        e.printStackTrace();
                        break;
                    }
                    httpIndex = abuseItemText.indexOf("\"", httpIndex)+1;
                }

                item.add(new Label("abuseItem", urls.toString()).setEscapeModelStrings(false));

            }
        };

        add(abuseDataView);

        add(new PagingNavigator("pageNav", abuseDataView) {
            @Override
            public boolean isVisible() {
                return abuseProvider.isVisible();
            }
        });
    }


    private class AbuseDataProvider implements IDataProvider<PasteItem> {

    PasteService pasteService;

    boolean visible = false;

    public AbuseDataProvider(PasteService pasteService) {
        this.pasteService = pasteService;
    }

    public Iterator<PasteItem> iterator(int first, int count) {
        return pasteService.getAbusePastes(first, count).iterator();
    }

    public int size() {
        int count = pasteService.getAbusePastesCount();

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

}