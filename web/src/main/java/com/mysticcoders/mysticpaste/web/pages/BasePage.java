package com.mysticcoders.mysticpaste.web.pages;

import com.mysticcoders.mysticpaste.web.components.google.GoogleAnalyticsSnippet;
import com.mysticcoders.mysticpaste.web.components.google.TagExternalLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import com.mysticcoders.mysticpaste.web.pages.paste.PasteItemPage;
import com.mysticcoders.mysticpaste.web.pages.history.HistoryPage;
import com.mysticcoders.mysticpaste.web.pages.plugin.PluginPage;

/**
 * Base Page for the application.
 * Extend this page to pull in the application header/footer.
 *
 * @author Steve Forsyth
 * Date: Mar 8, 2009
 */
public class BasePage extends WebPage {

    Class activePage;

    public BasePage() {
        init();
    }

    public BasePage(Class activePage) {
        this.activePage = activePage;
        init();
    }

    private void init() {
        WebMarkupContainer newLinkContainer = new WebMarkupContainer("newLinkContainer");
        if (activePage != null && activePage.equals(PasteItemPage.class)) {
            newLinkContainer.add(new SimpleAttributeModifier("class", "current_page_item"));
        }
        newLinkContainer.add(new BookmarkablePageLink<Void>("newLink", PasteItemPage.class));
        add(newLinkContainer);

        WebMarkupContainer historyLinkContainer = new WebMarkupContainer("historyLinkContainer");
        if (activePage != null && activePage.equals(HistoryPage.class)) {
            historyLinkContainer.add(new SimpleAttributeModifier("class", "current_page_item"));
        }
        historyLinkContainer.add(new BookmarkablePageLink<Void>("historyLink", HistoryPage.class));
        add(historyLinkContainer);

        WebMarkupContainer pluginLinkContainer = new WebMarkupContainer("pluginLinkContainer");
        if (activePage != null && activePage.equals(PluginPage.class)) {
            pluginLinkContainer.add(new SimpleAttributeModifier("class", "current_page_item"));
        }
        pluginLinkContainer.add(new BookmarkablePageLink<Void>("pluginLink", PluginPage.class));
        add(pluginLinkContainer);

        add(new TagExternalLink("homeLink", "http://www.mysticcoders.com"));
        add(new GoogleAnalyticsSnippet("ga-js") {
            public String getTracker() {
                return "UA-254925-6";
            }

            public boolean isVisible() { return true; }
        });
    }
}
