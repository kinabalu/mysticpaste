/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Jul 4, 2009
 * Time: 11:13:37 PM
 */
package com.mysticcoders.mysticpaste.web.pages.view;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.PasteService;
import com.mysticcoders.mysticpaste.utils.StringUtils;
import com.mysticcoders.mysticpaste.web.components.highlighter.HighlighterPanel;
import com.mysticcoders.mysticpaste.web.pages.BasePage;
import com.mysticcoders.mysticpaste.web.pages.error.PasteNotFound;
import com.mysticcoders.mysticpaste.web.pages.error.PasteSpam;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

//@StatelessComponent
public abstract class ViewPastePage extends BasePage {

    @SpringBean
    PasteService pasteService;

    private String justNumberPattern = "(\\d)+";
    private String numbersWithRange = "\\d+\\s?-\\s?\\d+";

    protected String getTitle() {
        PageParameters params = getPage().getPageParameters();
        return "#" + params.get("0") + " - Mystic Paste";
    }

    final IModel<PasteItem> pasteModel;

    protected PasteItem getPasteItem() {
        return pasteModel.getObject();
    }

    protected abstract boolean isPublic();

    public ViewPastePage(final PageParameters params) {
        super(params);

        if (params.get("0").isNull()) {
            throw new RestartResponseException(PasteNotFound.class);
        }

        String highlightLines = null;
        if (!params.get(0).isEmpty()) {
            String[] lineNumbers = params.get(0).toString().split(",");

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

            if (sb.length() > 0)
                highlightLines = sb.substring(0, sb.length() - 1);
        }

        // User must have copied just the funny private string rather than the whole bit
        if (params.get("0").isNull() && isPublic()) {
            PageParameters pp = new PageParameters();
            pp.add("0", params.get("0").toString());
            throw new RestartResponseException(ViewPrivatePage.class, pp);
        }

        pasteModel = getPasteModel(params.get("0").toString());
        if (pasteModel.getObject() == null || (pasteModel.getObject().isPrivate() && params.get("0").isNull())) {
            throw new RestartResponseException(PasteNotFound.class);
        }
        if (pasteModel.getObject().getAbuseCount() > 2) {
            throw new RestartResponseException(PasteSpam.class);
        }
        if (pasteModel.getObject().getContent() == null) {
            pasteModel.getObject().setContent("");
        }

        int viewCount = pasteService.incViewCount(pasteModel.getObject());

        this.setDefaultModel(new CompoundPropertyModel<PasteItem>(pasteModel));
        add(new Label("type"));


        WebMarkupContainer spamAlert = new WebMarkupContainer("spamAlert") {
            @Override
            public boolean isVisible() {
                return pasteModel.getObject().getAbuseCount() > 0;
            }
        };
        add(spamAlert);

        WebMarkupContainer hasImage = new WebMarkupContainer("hasImage") {
            @Override
            public boolean isVisible() {
                return pasteModel.getObject().hasImage();

            }
        };
        ExternalLink imageLocationLink = new ExternalLink("imageLocation", new PropertyModel<String>(pasteModel, "imageLocation"));
        hasImage.add(imageLocationLink);
        add(hasImage);

        WebMarkupContainer diffView = new WebMarkupContainer("diffView") {
            @Override
            public boolean isVisible() {
                return pasteModel.getObject().getParent() != null;
            }
        };

        if (pasteModel.getObject().getParent() != null) {
            PasteItem parentPaste = pasteService.getItem("web", pasteModel.getObject().getParent());
            PageParameters pp = new PageParameters();
            pp.add("0", parentPaste.getItemId());
            diffView.add(new BookmarkablePageLink<Void>("originalPasteLink", (parentPaste.isPrivate() ? ViewPrivatePage.class : ViewPublicPage.class), pp));


            Object[] diffOutput = PasteItem.diffPastes(parentPaste.getContent(), pasteModel.getObject().getContent());

            List<Integer> changedLines = (List<Integer>) diffOutput[0];       // TODO horrible horrible hackish thing, where do you get these things
            String diffText = (String) diffOutput[1];

            diffView.add(new HighlighterPanel("highlightedContent",
                    new Model<String>(diffText),
                    parentPaste.getType(),
                    false,
                    highlightLines,
                    changedLines));
        }
        add(diffView);

        final List<PasteItem> pasteChildren = pasteService.hasChildren(pasteModel.getObject());

//        final List<PasteItem> pasteChildren = new ArrayList<PasteItem>();
        WebMarkupContainer hasChildPastes = new WebMarkupContainer("hasChildPastes") {
            @Override
            public boolean isVisible() {
                return pasteChildren != null && pasteChildren.size() > 0;
            }
        };
        add(hasChildPastes);

        hasChildPastes.add(new ListView<PasteItem>("childPastes", pasteChildren) {

            @Override
            protected void populateItem(ListItem<PasteItem> item) {
                PasteItem pasteItem = item.getModelObject();

                PageParameters pp = new PageParameters();
                pp.add("0", pasteItem.getItemId());
                BookmarkablePageLink<Void> viewPaste = new BookmarkablePageLink<Void>("viewChildPaste", (pasteItem.isPrivate() ? ViewPrivatePage.class : ViewPublicPage.class), pp);

                viewPaste.add(new Label("pasteId", new PropertyModel<String>(item.getModel(), "itemId")));

                item.add(viewPaste);

                item.add(new Label("posted", PasteItem.getElapsedTimeSincePost(pasteItem)));
            }
        });

        String language = pasteModel.getObject().getType();
        add(new HighlighterPanel("highlightedContent",
                new PropertyModel<String>(pasteModel, "content"),
                language, false,
                highlightLines));

        add(createRawLink("rawLink", params));
        add(createDownloadLink("downloadLink", params));

        final Label markAbuseLabel = new Label("markAbuseLabel", "Report Abuse");
        markAbuseLabel.setOutputMarkupId(true);
        StatelessLink markAbuseLink = new StatelessLink("markAbuseLink") {

            public void onClick() {
                PasteItem pasteItem = pasteModel.getObject();

                pasteService.markAbuse(pasteItem);

                markAbuseLabel.setDefaultModel(new Model<String>("Marked As Spam"));
                markAbuseLabel.add(new AttributeModifier("style", "color: red; font-weight: bold;"));
            }
        };
        add(markAbuseLink);
        markAbuseLink.add(markAbuseLabel);


        StatelessForm<PasteItem> replyForm = new StatelessForm<PasteItem>("replyForm", pasteModel) {

            @Override
            protected void onSubmit() {
                onPaste(getModel());
            }
        };
        add(replyForm);
        replyForm.add(new TextArea<String>("content"));

        replyForm.add(new TextField<String>("email", new PropertyModel<String>(ViewPastePage.this, "spamEmail")));


    }

    private String replyPaste;

    private String spamEmail;

    public String getSpamEmail() {
        return spamEmail;
    }

    public void setSpamEmail(String spamEmail) {
        this.spamEmail = spamEmail;
    }

    private void onPaste(IModel<PasteItem> pasteModel) {
        PasteItem pI = pasteModel.getObject();

        if (pI.getContent() == null || pI.getContent().equals("")) {
            error("Paste content is required!");
            return;
        }

        if (getSpamEmail() != null || StringUtils.hasSpamKeywords(pI.getContent())) {
            error("Spam Spam Spam Spam");
            return;
        }
        PasteItem pasteItem = new PasteItem();
        pasteItem.setContent(pI.getContent());
        pasteItem.setPrivate(pI.isPrivate());
        pasteItem.setType(pI.getType());
        pasteItem.setParent(pI.getItemId());
        pasteItem.setClientIp(getClientIpAddress());

//        pasteService.detachItem(pI);       // TODO this is a horrible hack, we should instead clone pasteItem in the Model
        pasteService.createItem("web", pasteItem);
        PageParameters params = new PageParameters();
        if (pasteItem.isPrivate()) {
//                this.setRedirect(true);
            params.add("0", pasteItem.getItemId());
            setResponsePage(ViewPrivatePage.class, params);
        } else {
//                this.setRedirect(true);
            params.add("0", pasteItem.getItemId());
            setResponsePage(ViewPublicPage.class, params);
        }

    }

    private AbstractLink createRawLink(final String id, final PageParameters params) {
        return new ExternalLink(id, "/view/" + params.get("0") + "/text");
    }

    private AbstractLink createDownloadLink(String id, PageParameters params) {
        return new ExternalLink(id, "/view/" + params.get("0") + "/download");
    }


    protected abstract IModel<PasteItem> getPasteModel(String id);

}
