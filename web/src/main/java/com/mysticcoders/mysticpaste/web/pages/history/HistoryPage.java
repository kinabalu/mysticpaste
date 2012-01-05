package com.mysticcoders.mysticpaste.web.pages.history;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.PasteService;
import com.mysticcoders.mysticpaste.web.components.highlighter.HighlighterPanel;
import com.mysticcoders.mysticpaste.web.pages.BasePage;
import com.mysticcoders.mysticpaste.web.pages.view.ViewPublicPage;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * List the paste history.
 *
 * @author Steve Forsyth
 * Date: Mar 8, 2009
 */
public class HistoryPage extends BasePage {

    @SpringBean
    PasteService pasteService;

    DataView historyDataView;

    protected String getTitle() {
        return "Pastes - Mystic Paste";
    }

    public HistoryPage() {
        super(HistoryPage.class);

        final HistoryDataProvider historyDataProvider = new HistoryDataProvider(pasteService);

        historyDataView = new DataView<PasteItem>("history", historyDataProvider, 5) {
            protected void populateItem(Item<PasteItem> item) {
                PasteItem pasteItem = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("0", Long.toString(pasteItem.getId()));
                item.add(new BookmarkablePageLink<Void>("viewLink", ViewPublicPage.class, params));

                final String[] contentLines = pasteItem.getContent().split("\n");
                item.add(new Label("lineCount", "(" + contentLines.length + " Line" +
                        (contentLines.length > 1 ? "s" : "") + ")"));

                item.add(new Label("posted", PasteItem.getElapsedTimeSincePost(pasteItem)));

                item.add(new HighlighterPanel("content",
                        new PropertyModel<String>(pasteItem, "previewContent"), pasteItem.getType()));
            }
        };

        WebMarkupContainer historyDataViewContainer = new WebMarkupContainer("historyContainer");
        historyDataViewContainer.add(historyDataView);
        historyDataViewContainer.setOutputMarkupId(true);
        add(historyDataViewContainer);

        add(new AjaxPagingNavigator("pageNav", historyDataView) {
            @Override
            public boolean isVisible() {
                return historyDataProvider.isVisible();
            }
        });
        add(new AjaxPagingNavigator("pageNav2", historyDataView) {
            @Override
            public boolean isVisible() {
                return historyDataProvider.isVisible();
            }
        });

        add(new Label("noPastesFound", "No Pastes Found") {
            @Override
            public boolean isVisible() {
                return !historyDataProvider.isVisible();
            }
        });
    }

}
