package com.mysticcoders.mysticpaste.web.pages.admin;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.PasteService;
import com.mysticcoders.mysticpaste.web.components.highlighter.HighlighterPanel;
import com.mysticcoders.mysticpaste.web.pages.BasePage;
import com.mysticcoders.mysticpaste.web.pages.history.HistoryDataProvider;
import com.mysticcoders.mysticpaste.web.pages.history.PastePagingNavigator;
import com.mysticcoders.mysticpaste.web.pages.view.ViewPublicPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.pages.AccessDeniedPage;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Paste Admin
 *
 * @author Andrew Lombardi <andrew@mysticcoders.com>
 */
public class OverviewPage extends BasePage {

    @SpringBean
    PasteService pasteService;

    DataView historyDataView;

    private final int ITEMS_PER_PAGE = 50;

    private int itemsPerPage;

    protected String getTitle() {
        return "Paste Admin - Mystic Paste";
    }

    public OverviewPage() {
        super(OverviewPage.class);

        IRequestParameters params = getRequestCycle().getRequest().getQueryParameters();
        String pw = pasteService.getAdminPassword();
        if(pw == null || params.getParameterValue("pw").isEmpty() ||
                !params.getParameterValue("pw").toString().equals(pw)) {
            throw new RestartResponseException(AccessDeniedPage.class);
        }

        if(!params.getParameterValue("itemsPerPage").isEmpty()) {
            itemsPerPage = params.getParameterValue("itemsPerPage").toInt(ITEMS_PER_PAGE);
        }
        final HistoryDataProvider historyDataProvider = new HistoryDataProvider(pasteService);
        final WebMarkupContainer historyDataViewContainer = new WebMarkupContainer("historyContainer");

        historyDataView = new DataView<PasteItem>("history", historyDataProvider, itemsPerPage) {
            protected void populateItem(Item<PasteItem> item) {
                final PasteItem pasteItem = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("0", pasteItem.getItemId());
                item.add(new BookmarkablePageLink<Void>("viewLink", ViewPublicPage.class, params));

                item.add(new AjaxLink<Void>("increaseAbuseCount") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        pasteService.increaseAbuseCount(pasteItem);
                        target.add(historyDataViewContainer);
                    }
                });
/*
                item.add(new AjaxLink<Void>("decreaseAbuseCount") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        pasteService.decreaseAbuseCount(pasteItem);
                        target.add(historyDataView);
                    }
                });
*/
                final String[] contentLines = pasteItem.getContent().split("\n");
                item.add(new Label("lineCount", "(" + contentLines.length + " Line" +
                        (contentLines.length > 1 ? "s" : "") + ")"));

                item.add(new Label("posted", PasteItem.getElapsedTimeSincePost(pasteItem)));
                item.add(new Label("clientIp", new PropertyModel<String>(item.getModel(), "clientIp")));

                WebMarkupContainer hasImage = new WebMarkupContainer("hasImage") {
                    @Override
                    public boolean isVisible() {
                        return pasteItem.hasImage();
                    }
                };
                item.add(hasImage);
                item.add(new Label("content",
                        new PropertyModel<String>(pasteItem, "previewContent")));
/*
                item.add(new HighlighterPanel("content",
                        new PropertyModel<String>(pasteItem, "previewContent"), pasteItem.getType()));
*/
            }
        };

        historyDataViewContainer.add(historyDataView);
        historyDataViewContainer.setOutputMarkupId(true);
        add(historyDataViewContainer);

        PastePagingNavigator pageNav = new PastePagingNavigator("pageNav", historyDataView) {
            @Override
            public boolean isVisible() {
                return historyDataProvider.isVisible();
            }
        };
        PastePagingNavigator pageNav2 = new PastePagingNavigator("pageNav2", historyDataView) {
            @Override
            public boolean isVisible() {
                return historyDataProvider.isVisible();
            }
        };

        pageNav.setDependentNavigator(pageNav2);
        pageNav2.setDependentNavigator(pageNav);
        add(pageNav);
        add(pageNav2);

        add(new Label("noPastesFound", "No Pastes Found") {
            @Override
            public boolean isVisible() {
                return !historyDataProvider.isVisible();
            }
        });
    }
}