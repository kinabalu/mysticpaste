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
import com.mysticcoders.mysticpaste.web.components.GravatarImage;
import com.mysticcoders.mysticpaste.web.components.highlighter.HighlighterPanel;
import com.mysticcoders.mysticpaste.web.pages.BasePage;
import com.mysticcoders.mysticpaste.web.pages.error.PasteNotFound;
import com.mysticcoders.mysticpaste.web.pages.error.PasteSpam;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.devutils.stateless.StatelessComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.*;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@StatelessComponent
public abstract class ViewPastePage extends BasePage {

    @SpringBean
    PasteService pasteService;

    private String justNumberPattern = "(\\d)+";
    private String numbersWithRange = "\\d+\\s?-\\s?\\d+";

    private String pasteId;

    private String highlightLines;

    public ViewPastePage(final PageParameters params) {
        if (params.get("0") == null) {
            throw new RestartResponseException(PasteNotFound.class);
        }
        pasteId = params.get("0").toString();

        if (!params.get("1").isEmpty()) {
            String[] lineNumbers = params.get("1").toString().split(",");

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

        init();
    }

    protected String getTitle() {
        return "#" + pasteId + " - Mystic Paste";
    }

    private void init() {


        final IModel<PasteItem> pasteModel = getPasteModel(pasteId);
        if (pasteModel.getObject() == null) {
            throw new RestartResponseException(PasteNotFound.class);
        }
        if ((pasteModel.getObject()).isAbuseFlag()) {
            throw new RestartResponseException(PasteSpam.class);
        }

        this.setDefaultModel(new CompoundPropertyModel<PasteItem>(pasteModel));
        add(new Label("type"));

        String language = pasteModel.getObject().getType();
        add(new HighlighterPanel("highlightedContent",
                new PropertyModel(pasteModel, "content"),
                language,
                highlightLines));

        add(new Label("content").setEscapeModelStrings(true));

        final Label markAbuseLabel = new Label("markAbuseLabel", "Report Abuse");
        markAbuseLabel.setOutputMarkupId(true);

        // TODO looks like AjaxLink is stateful
        StatelessLink markAbuseLink = new StatelessLink("markAbuseLink") {

            public void onClick() {
                PasteItem pasteItem = pasteModel.getObject();

                pasteService.markAbuse(pasteItem);

                markAbuseLabel.setDefaultModel(new Model<String>("Marked As Spam"));
                markAbuseLabel.add(new SimpleAttributeModifier("style", "color: red; font-weight: bold;"));

//                target.add(markAbuseLabel);
            }
        };
        add(markAbuseLink);
        markAbuseLink.add(markAbuseLabel);
    }

    protected abstract IModel<PasteItem> getPasteModel(String id);

}
