package com.mysticcoders.mysticpaste.web.pages.history;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.PasteService;
import com.mysticcoders.mysticpaste.web.pages.BasePage;
import com.mysticcoders.mysticpaste.web.pages.view.ViewPublicPage;
import com.mysticcoders.wicket.mousetrap.KeyBinding;
import de.agilecoders.wicket.markup.html.bootstrap.dialog.Alert;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * List the paste history.
 *
 * @author Steve Forsyth
 *         Date: Mar 8, 2009
 */
public class HistoryPage extends BasePage {

    @SpringBean
    PasteService pasteService;

    DataView historyDataView;

    private final int ITEMS_PER_PAGE = 5;

    protected String getTitle() {
        return "Paste History - Mystic Paste";
    }

    public HistoryPage() {
        super(HistoryPage.class);

        final HistoryDataProvider historyDataProvider = new HistoryDataProvider(pasteService);

        Alert newFeatureAlert;
        add(newFeatureAlert = new Alert("newFeatureAlert", Model.of("Check out our <a href=\"/help\"><strong>New Features</strong></a> like <code>image upload</code> via clipboard or drag and drop, <code>keyboard shortcuts</code>, and more!")));
//        newFeatureAlert.getMessageLabel().setEscapeModelStrings(false);

        historyDataView = new DataView<PasteItem>("history", historyDataProvider, ITEMS_PER_PAGE) {
            protected void populateItem(Item<PasteItem> item) {
                final PasteItem pasteItem = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("0", pasteItem.getItemId());
                item.add(new BookmarkablePageLink<Void>("viewLink", ViewPublicPage.class, params));

                final String[] contentLines = pasteItem.getContent().split("\n");
                item.add(new Label("lineCount", "(" + contentLines.length + " Line" +
                        (contentLines.length > 1 ? "s" : "") + ")"));

                item.add(new Label("posted", PasteItem.getElapsedTimeSincePost(pasteItem)));
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

        final WebMarkupContainer historyDataViewContainer = new WebMarkupContainer("historyContainer");
        historyDataViewContainer.add(historyDataView);
        historyDataViewContainer.setOutputMarkupId(true);
        add(historyDataViewContainer);

        final PastePagingNavigator pageNav = new PastePagingNavigator("pageNav", historyDataView) {
            @Override
            public boolean isVisible() {
                return historyDataProvider.isVisible();
            }
        };
        final PastePagingNavigator pageNav2 = new PastePagingNavigator("pageNav2", historyDataView) {
            @Override
            public boolean isVisible() {
                return historyDataProvider.isVisible();
            }
        };

        final AbstractDefaultAjaxBehavior historyNextPageNav = new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                System.out.println("Next Page");
                if (pageNav.getPageable().getCurrentPage() < pageNav.getPageable().getPageCount() - 1) {
                    pageNav.getPageable().setCurrentPage(pageNav.getPageable().getCurrentPage() + 1);
                    target.add(historyDataViewContainer, pageNav, pageNav2);
                }
            }
        };
        final AbstractDefaultAjaxBehavior historyPrevPageNav = new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                System.out.println("Previous Page");
                if (pageNav.getPageable().getCurrentPage() > 0) {
                    pageNav.getPageable().setCurrentPage(pageNav.getPageable().getCurrentPage() - 1);
                    target.add(historyDataViewContainer, pageNav, pageNav2);
                }
            }
        };
        final AbstractDefaultAjaxBehavior historyFirstPageNav = new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                System.out.println("First Page");
                if (pageNav.getPageable().getCurrentPage() > 1) {
                    pageNav.getPageable().setCurrentPage(0);
                    target.add(historyDataViewContainer, pageNav, pageNav2);
                }
            }
        };
        final AbstractDefaultAjaxBehavior historyLastPageNav = new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                System.out.println("Last Page");
                if (pageNav.getPageable().getCurrentPage() < pageNav.getPageable().getPageCount() - 1) {
                    pageNav.getPageable().setCurrentPage(pageNav.getPageable().getPageCount() - 1);
                    target.add(historyDataViewContainer, pageNav, pageNav2);
                }
            }
        };

        add(historyPrevPageNav);
        add(historyNextPageNav);
        add(historyFirstPageNav);
        add(historyLastPageNav);
        mousetrap().addBind(new KeyBinding().addKeyCombo(KeyBinding.LEFT), historyPrevPageNav);
        mousetrap().addBind(new KeyBinding().addKeyCombo(KeyBinding.RIGHT), historyNextPageNav);
        mousetrap().addBind(new KeyBinding().addKeyCombo(KeyBinding.SHIFT, KeyBinding.LEFT), historyFirstPageNav);
        mousetrap().addBind(new KeyBinding().addKeyCombo(KeyBinding.SHIFT, KeyBinding.RIGHT), historyLastPageNav);

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
