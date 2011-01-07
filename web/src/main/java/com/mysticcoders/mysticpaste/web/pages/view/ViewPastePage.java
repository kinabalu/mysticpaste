/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Jul 4, 2009
 * Time: 11:13:37 PM
 */
package com.mysticcoders.mysticpaste.web.pages.view;

import com.mysticcoders.mysticpaste.model.PasteComment;
import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.persistence.PasteCommentDao;
import com.mysticcoders.mysticpaste.services.PasteService;
import com.mysticcoders.mysticpaste.web.components.highlighter.HighlighterPanel;
import com.mysticcoders.mysticpaste.web.components.GravatarImage;
import com.mysticcoders.mysticpaste.web.pages.BasePage;
import com.mysticcoders.mysticpaste.web.pages.error.PasteNotFound;
import com.mysticcoders.mysticpaste.web.pages.error.PasteSpam;
import org.apache.wicket.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.*;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.resource.SharedResourceRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import java.io.Serializable;
import java.util.*;

public abstract class ViewPastePage extends BasePage {

    @SpringBean
    PasteService pasteService;

    @SpringBean
    PasteCommentDao pasteCommentDao;

    String justNumberPattern = "(\\d)+";
    String numbersWithRange = "\\d+\\s?-\\s?\\d+";

    protected String getTitle() {
        PageParameters params = RequestCycle.get().getPageParameters();
        return "#" + params.getString("0") + " - Mystic Paste";
    }

    public ViewPastePage(final PageParameters params) {
        if (params.getString("0") == null) {
            throw new RestartResponseException(PasteNotFound.class);
        }

        String highlightLines = null;
        if (!Strings.isEmpty(params.getString("1"))) {
            if(params.getString("1").equals("text")) {
                RequestParameters rps = new RequestParameters();
                rps.setResourceKey(new ResourceReference("textPasteResource").getSharedResourceKey());
                getRequestCycle().setRequestTarget(new SharedResourceRequestTarget(rps));
                return;
            } else if(params.getString("1").equals("download")) {
                RequestParameters rps = new RequestParameters();
                rps.setResourceKey(new ResourceReference("downloadAsTextPasteResource").getSharedResourceKey());
                getRequestCycle().setRequestTarget(new SharedResourceRequestTarget(rps));
                return;
            }


            String[] lineNumbers = params.getString("1").split(",");

            List<String> numbers = new ArrayList<String>();
            for (String lineNumber : lineNumbers) {

                if (lineNumber.matches(justNumberPattern)) {
                    numbers.add(lineNumber);
                } else if (lineNumber.matches(numbersWithRange)) {
                    String[] numberRange = lineNumber.split("-");
                    int startRange = Integer.parseInt(numberRange[0].trim());
                    int endRange = Integer.parseInt(numberRange[1].trim());
                    if (startRange < endRange) {
                        for (int i = startRange; i <= endRange; i++) {
                            numbers.add("" + i);
                        }
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            for (String number : numbers) {
                sb.append(number).append(",");
            }
            highlightLines = sb.substring(0, sb.length() - 1);
        }

        final IModel<PasteItem> pasteModel = getPasteModel(params.getString("0"));
        if (pasteModel.getObject() == null) {
            throw new RestartResponseException(PasteNotFound.class);
        }
        if ((pasteModel.getObject()).isAbuseFlag()) {
            throw new RestartResponseException(PasteSpam.class);
        }

        this.setDefaultModel(new CompoundPropertyModel(pasteModel));
        add(new Label("type"));

        String language = pasteModel.getObject().getType();
        add(new HighlighterPanel("highlightedContent",
                new PropertyModel(pasteModel, "content"),
                language, false,
                highlightLines));

        add(new Label("content").setEscapeModelStrings(true));

        PageParameters pp = new PageParameters();
        pp.put("0", params.getString("0"));
        pp.put("1", "text");
        add(new BookmarkablePageLink<Void>("rawLink", ViewPublicPage.class, pp));

        PageParameters pp2 = new PageParameters();
        pp2.put("0", params.getString("0"));
        pp2.put("1", "download");
        add(new BookmarkablePageLink<Void>("downloadLink", ViewPublicPage.class, pp2));

        final Label markAbuseLabel = new Label("markAbuseLabel", "Report Abuse");
        markAbuseLabel.setOutputMarkupId(true);
        AjaxLink markAbuseLink = new AjaxLink("markAbuseLink") {

            public void onClick(AjaxRequestTarget target) {
                PasteItem pasteItem = pasteModel.getObject();

                pasteService.markAbuse(pasteItem);

                markAbuseLabel.setDefaultModel(new Model<String>("Marked As Spam"));
                markAbuseLabel.add(new SimpleAttributeModifier("style", "color: red; font-weight: bold;"));

                target.addComponent(markAbuseLabel);
            }
        };
        add(markAbuseLink);
        markAbuseLink.add(markAbuseLabel);

    }

    protected abstract IModel<PasteItem> getPasteModel(String id);

}
