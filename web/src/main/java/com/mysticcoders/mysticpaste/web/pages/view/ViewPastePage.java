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

    @SpringBean
    PasteCommentDao pasteCommentDao;

    String justNumberPattern = "(\\d)+";
    String numbersWithRange = "\\d+\\s?-\\s?\\d+";

    public ViewPastePage(final PageParameters params) {
        // TODO might have to change this to be getnamedParameter

        if (params.get("0") == null) {
            throw new RestartResponseException(PasteNotFound.class);
        }

        String highlightLines = null;

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

        final IModel<PasteItem> pasteModel = getPasteModel(params.get("0").toString());
        if (pasteModel.getObject() == null) {
            throw new RestartResponseException(PasteNotFound.class);
        }
        if ((pasteModel.getObject()).isAbuseFlag()) {
            throw new RestartResponseException(PasteSpam.class);
        }

        this.setDefaultModel(new CompoundPropertyModel<PasteItem>(pasteModel));
        add(new Label("type"));

        /* COMMENTS COUNT */
        final Label commentsCountLabel = new Label("commentsCount", new Model() {
            public Integer getObject() {
                return pasteCommentDao.countComments((pasteModel.getObject()).getId());
            }
        });
        commentsCountLabel.setOutputMarkupId(true);
        add(commentsCountLabel);

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

        final WebMarkupContainer commentListContainer = new WebMarkupContainer("commentListContainer");
        commentListContainer.setOutputMarkupId(true);

        CommentListModel commentListModel = new CommentListModel(pasteModel.getObject().getId());
        final ListView<PasteComment> commentList = new ListView<PasteComment>("commentList", commentListModel) {
            @Override
            protected void populateItem(final ListItem<PasteComment> item) {
                item.add(new Label("name", new PropertyModel(item.getModel(), "name")));
                item.add(new Label("comment", new PropertyModel(item.getModel(), "comment")));
                item.add(new GravatarImage("image", new PropertyModel<String>(item.getModel(), "email")));
            }
        };
        commentListContainer.add(commentList);
        add(commentListContainer);

        final Label noComments = new Label("noComments", "No Comments") {
            public boolean isVisible() {
                return commentList.size() == 0;
            }
        };
        noComments.setOutputMarkupId(true);
        add(noComments);

        StatelessForm<CommentBean> form = new StatelessForm<CommentBean>("commentForm", new CompoundPropertyModel<CommentBean>(new CommentBean())) {

            @Override
            public void onSubmit() {
                CommentBean commentBean = getModelObject();

                if (commentBean.getConfirmEmail() != null) {
                    error("SPAM SPAM SPAM");
                    return;
                }
                PasteItem pasteItem = pasteModel.getObject();
                PasteComment comment = new PasteComment();
                comment.setItem(pasteItem);
                comment.setName(commentBean.getName());
                comment.setEmail(commentBean.getEmail());
                comment.setComment(commentBean.getComment());
                comment.setTimestamp(new Date());

                String ipAddress = ((ServletWebRequest) getRequest()).getHttpServletRequest().getRemoteAddr();

                comment.setIpAddress(ipAddress);

                pasteCommentDao.save(comment);

                commentBean.clear();

/*
                target.add(commentListContainer);
                target.add(commentsCountLabel);
                target.add(commentFeedbackPanel);
                target.add(noComments);

                form.visitFormComponents(new IVisitor<FormComponent, Void>() {
                    public void component(FormComponent formComponent, IVisit<Void> visit) {
                        target.add(formComponent);
                        visit.dontGoDeeper();
                    }
                });
*/
            }

/*
            @Override
            protected void onError(final AjaxRequestTarget target, final Form form) {
                // or update the feedback panel
                target.add(commentFeedbackPanel);
            }
*/

        };
        add(form);

        final FeedbackPanel commentFeedbackPanel = new FeedbackPanel("feedback");
        commentFeedbackPanel.setOutputMarkupId(true);
        form.add(commentFeedbackPanel);

        RequiredTextField<String> commentName = new RequiredTextField<String>("name");
        commentName.setOutputMarkupId(true);
        form.add(commentName);

        RequiredTextField<String> commentEmail = new RequiredTextField<String>("email");
        commentEmail.add(EmailAddressValidator.getInstance());
        commentEmail.setOutputMarkupId(true);
        form.add(commentEmail);

        TextField<String> commentConfirmEmail = new TextField<String>("confirmEmail");
        commentConfirmEmail.setOutputMarkupId(true);
        form.add(commentConfirmEmail);

        TextArea<String> commentComment = new TextArea<String>("comment");
        commentComment.setRequired(true);
        commentComment.setOutputMarkupId(true);
        form.add(commentComment);

        form.add(new Button("addComment") {

        });
    }

    protected abstract IModel<PasteItem> getPasteModel(String id);

    private class CommentListModel extends LoadableDetachableModel<List<PasteComment>> {

        private Long pasteItemId;

        public CommentListModel(Long pasteItemId) {
            this.pasteItemId = pasteItemId;
        }

        @Override
        protected List<PasteComment> load() {
            return pasteCommentDao.findByPasteId(pasteItemId);
        }
    }

    private class CommentBean implements Serializable {
        private String name;
        private String email;
        private String confirmEmail;
        private String comment;

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getConfirmEmail() {
            return confirmEmail;
        }

        public String getComment() {
            return comment;
        }

        public void clear() {
            this.name = null;
            this.email = null;
            this.confirmEmail = null;
            this.comment = null;
        }
    }
}
