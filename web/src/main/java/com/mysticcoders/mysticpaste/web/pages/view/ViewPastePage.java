/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Jul 4, 2009
 * Time: 11:13:37 PM
 */
package com.mysticcoders.mysticpaste.web.pages.view;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.InvalidClientException;
import com.mysticcoders.mysticpaste.services.PasteService;
import com.mysticcoders.mysticpaste.web.components.highlighter.HighlighterPanel;
import com.mysticcoders.mysticpaste.web.pages.BasePage;
import com.mysticcoders.mysticpaste.web.pages.error.PasteNotFound;
import com.mysticcoders.mysticpaste.web.pages.error.PasteSpam;
import com.mysticcoders.mysticpaste.web.pages.history.HistoryPage;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.resource.SharedResourceRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public abstract class ViewPastePage extends BasePage {

    @SpringBean
    PasteService pasteService;

    String justNumberPattern = "(\\d)+";
    String numbersWithRange = "\\d+\\s?-\\s?\\d+";

    protected String getTitle() {
        PageParameters params = RequestCycle.get().getPageParameters();
        return "#" + params.getString("0") + " - Mystic Paste";
    }

    protected abstract boolean isPublic();

    public ViewPastePage(final PageParameters params) {
        if (params.getString("0") == null) {
            throw new RestartResponseException(PasteNotFound.class);
        }

        String highlightLines = null;
        if (!Strings.isEmpty(params.getString("1"))) {
            if (params.getString("1").equals("text")) {
                RequestParameters rps = new RequestParameters();
                rps.setResourceKey(new ResourceReference("textPasteResource").getSharedResourceKey());
                getRequestCycle().setRequestTarget(new SharedResourceRequestTarget(rps));
                return;
            } else if (params.getString("1").equals("download")) {
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

        // User must have copied just the funny private string rather than the whole bit
        if (params.getAsInteger("0") == null && isPublic()) {
            PageParameters pp = new PageParameters();
            pp.put("0", params.getString("0"));
            throw new RestartResponseException(ViewPrivatePage.class, pp);
        }

        final IModel<PasteItem> pasteModel = getPasteModel(params.getString("0"));
        if (pasteModel.getObject() == null || (pasteModel.getObject().isPrivate() && params.getAsInteger("0") != null)) {
            throw new RestartResponseException(PasteNotFound.class);
        }
        if (pasteModel.getObject().isAbuseFlag()) {
            throw new RestartResponseException(PasteSpam.class);
        }

        this.setDefaultModel(new CompoundPropertyModel(pasteModel));
        add(new Label("type"));

        WebMarkupContainer diffView = new WebMarkupContainer("diffView") {
            @Override
            public boolean isVisible() {
                return pasteModel.getObject().getParent() != null;
            }
        };

        if (pasteModel.getObject().getParent() != null) {
            PasteItem parentPaste = pasteModel.getObject().getParent();
            PageParameters pp = new PageParameters();
            pp.put("0", parentPaste.getId());
            diffView.add(new BookmarkablePageLink<Void>("originalPasteLink", (parentPaste.isPrivate() ? ViewPrivatePage.class : ViewPublicPage.class), pp));


            Object[] diffOutput = PasteItem.diffPastes(parentPaste.getContent(), pasteModel.getObject().getContent());

            List<Integer> changedLines= (List<Integer>)diffOutput[0];       // TODO horrible horrible hackish thing, where do you get these things
            String diffText = (String)diffOutput[1];

            diffView.add(new HighlighterPanel("highlightedContent",
                new Model<String>(diffText),
                parentPaste.getType(),
                false,
                highlightLines,
                changedLines));
        }
        add(diffView);




/*
        final AbstractReadOnlyModel<List<PasteItem>> childPastes = new AbstractReadOnlyModel<List<PasteItem>>() {
            public List<PasteItem> getObject() {
                return pasteService.hasChildren(pasteModel.getObject().getId());
            }
        };
*/

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
                pp.put("0", pasteItem.getId());
                BookmarkablePageLink<Void> viewPaste = new BookmarkablePageLink<Void>("viewChildPaste", (pasteItem.isPrivate() ? ViewPrivatePage.class : ViewPublicPage.class), pp);

                viewPaste.add(new Label("pasteId", new PropertyModel<String>(item.getModel(), "id")));

                item.add(viewPaste);

                item.add(new Label("posted", HistoryPage.getElapsedTimeSincePost(pasteItem)));      // TODO refactor this into it's own class
            }
        });

        String language = pasteModel.getObject().getType();
        add(new HighlighterPanel("highlightedContent",
                new PropertyModel(pasteModel, "content"),
                language, false,
                highlightLines));

        add(createRawLink("rawLink", params));
        add(createDownloadLink("downloadLink", params));

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


        Form<PasteItem> replyForm = new Form<PasteItem>("replyForm", pasteModel) {

            @Override
            protected void onSubmit() {
                onPaste(getModel());
            }
        };
        add(replyForm);
        replyForm.add(new TextArea<String>("content"));

    }

    private String replyPaste;

    private void onPaste(IModel<PasteItem> pasteModel) {
        PasteItem pI = pasteModel.getObject();

/*
        if (pasteItem.getContent() == null || pasteItem.getContent().equals("")) {
            error("Paste content is required!");
            return;
        }

        if (getSpamEmail() != null || hasSpamKeywords(pasteItem.getContent())) {
            error("Spam Spam Spam Spam");
            return;
        }
*/
        PasteItem pasteItem = new PasteItem();
        pasteItem.setContent(pI.getContent());
        pasteItem.setPrivate(pI.isPrivate());
        pasteItem.setType(pI.getType());
        pasteItem.setParent(pI);
        pasteItem.setClientIp(getClientIpAddress());

        try {
            pasteService.detachItem(pI);       // TODO this is a horrible hack, we should instead clone pasteItem in the Model
            pasteService.createItem("web", pasteItem);
            PageParameters params = new PageParameters();
            if (pasteItem.isPrivate()) {
                this.setRedirect(true);
                params.put("0", pasteItem.getPrivateToken());
                setResponsePage(ViewPrivatePage.class, params);
            } else {
                this.setRedirect(true);
                params.put("0", Long.toString(pasteItem.getId()));
                setResponsePage(ViewPublicPage.class, params);
            }
        } catch (InvalidClientException e) {
            // Do nothing at this point as we haven't defined what an "Invalid Client" is.
            e.printStackTrace();
        }

    }

    private BookmarkablePageLink<Void> createExportLink(String id, PageParameters params, String type) {
        PageParameters pp = new PageParameters();
        pp.put("0", params.getString("0"));
        pp.put("1", type);
        return new BookmarkablePageLink<Void>(id, ViewPublicPage.class, pp);
    }

    private BookmarkablePageLink<Void> createRawLink(String id, PageParameters params) {
        return createExportLink(id, params, "text");
    }

    private BookmarkablePageLink<Void> createDownloadLink(String id, PageParameters params) {
        return createExportLink(id, params, "download");
    }


    protected abstract IModel<PasteItem> getPasteModel(String id);

}
