package com.mysticcoders.mysticpaste.web.pages;

import com.mysticcoders.mysticpaste.web.components.google.TagExternalLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: kinabalu
 * Date: 11/11/12
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Footer extends Panel {

    /**
     * Construct.
     *
     * @param markupId The components markup id.
     */
    public Footer(String markupId) {
        super(markupId);

        AbstractReadOnlyModel<String> dateModel = new AbstractReadOnlyModel<String>() {
            public String getObject() {
                Calendar cal = Calendar.getInstance();
                return "" + cal.get(Calendar.YEAR);
            }
        };

        add(new Label("latestYear", dateModel));

        add(new ExternalLink("sourceLink", "http://github.com/kinabalu/mysticpaste/"));

        add(new TagExternalLink("blogLink", "http://www.mysticcoders.com/blog"));

        add(new BookmarkablePageLink<Void>("legalLink", LegalPage.class));
    }
}
