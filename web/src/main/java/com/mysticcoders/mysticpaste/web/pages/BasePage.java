package com.mysticcoders.mysticpaste.web.pages;

import com.mysticcoders.mysticpaste.web.components.google.GoogleAnalyticsSnippet;
import com.mysticcoders.mysticpaste.web.components.google.TagExternalLink;
import com.mysticcoders.mysticpaste.web.pages.history.HistoryPage;
import com.mysticcoders.mysticpaste.web.pages.paste.PasteItemPage;
import com.mysticcoders.mysticpaste.web.pages.plugin.PluginPage;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Calendar;

/**
 * Base Page for the application.
 * Extend this page to pull in the application header/footer.
 *
 * @author Steve Forsyth
 *         Date: Mar 8, 2009
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

    public BasePage(PageParameters params) {
        super(params);
        init();
    }

    protected String getTitle() {
        return "Mystic Paste";
    }

    private void init() {

        AbstractReadOnlyModel<String> dateModel = new AbstractReadOnlyModel<String>() {
            public String getObject() {
                Calendar cal = Calendar.getInstance();
                return "" + cal.get(Calendar.YEAR);
            }
        };

        add(new Label("latestYear", dateModel));

        WebMarkupContainer newLinkContainer = new WebMarkupContainer("newLinkContainer");
        if (activePage != null && activePage.equals(PasteItemPage.class)) {
            newLinkContainer.add(new SimpleAttributeModifier("class", "active"));
        }
        newLinkContainer.add(new BookmarkablePageLink<Void>("newLink", PasteItemPage.class));
        add(newLinkContainer);

        WebMarkupContainer historyLinkContainer = new WebMarkupContainer("historyLinkContainer");
        if (activePage != null && activePage.equals(HistoryPage.class)) {
            historyLinkContainer.add(new SimpleAttributeModifier("class", "active"));
        }
        historyLinkContainer.add(new BookmarkablePageLink<Void>("historyLink", HistoryPage.class));
        add(historyLinkContainer);

        WebMarkupContainer pluginLinkContainer = new WebMarkupContainer("pluginLinkContainer");
        if (activePage != null && activePage.equals(PluginPage.class)) {
            pluginLinkContainer.add(new SimpleAttributeModifier("class", "active"));
        }
        pluginLinkContainer.add(new BookmarkablePageLink<Void>("pluginLink", PluginPage.class));
        add(pluginLinkContainer);

        WebMarkupContainer helpLinkContainer = new WebMarkupContainer("helpLinkContainer");
        if (activePage != null && activePage.equals(HelpPage.class)) {
            helpLinkContainer.add(new SimpleAttributeModifier("class", "active"));
        }
        helpLinkContainer.add(new BookmarkablePageLink<Void>("helpLink", HelpPage.class));
        add(helpLinkContainer);

        add(new ExternalLink("sourceLink", "http://github.com/kinabalu/mysticpaste/"));

        add(new TagExternalLink("blogLink", "http://www.mysticcoders.com/blog"));

        add(new GoogleAnalyticsSnippet("ga-js") {
            public String getTracker() {
                return "UA-254925-6";
            }

            public boolean isVisible() {
                return true;
            }
        });
    }

    @Override
    protected void onBeforeRender() {
        addOrReplace(new Label("title", getTitle()));

        super.onBeforeRender();
    }

    /**
     * Returns the X-Forwarded-For header
     *
     * @return
     */
    protected String getClientIpAddress() {
        String xForwardedFor = ((ServletWebRequest) RequestCycle.get().getRequest()).getHeader("X-Forwarded-For");
        return xForwardedFor;
    }

}
