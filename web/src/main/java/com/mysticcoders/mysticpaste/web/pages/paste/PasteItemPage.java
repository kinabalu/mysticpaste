package com.mysticcoders.mysticpaste.web.pages.paste;

import com.mysticcoders.mysticpaste.MysticPasteApplication;
import com.mysticcoders.mysticpaste.model.LanguageSyntax;
import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.PasteService;
import com.mysticcoders.mysticpaste.utils.StringUtils;
import com.mysticcoders.mysticpaste.web.components.DefaultFocusBehavior;
import com.mysticcoders.mysticpaste.web.components.highlighter.HighlighterPanel;
import com.mysticcoders.mysticpaste.web.components.spin.Spin;
import com.mysticcoders.mysticpaste.web.pages.BasePage;
import com.mysticcoders.mysticpaste.web.pages.view.ViewPrivatePage;
import com.mysticcoders.mysticpaste.web.pages.view.ViewPublicPage;
import com.mysticcoders.wicket.mousetrap.KeyBinding;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Paste Item page.
 *
 * @author Steve Forsyth
 *         Date: Mar 8, 2009
 */
public class PasteItemPage extends BasePage {

    @SpringBean
    PasteService pasteService;

    private static final Logger logger = LoggerFactory.getLogger(PasteItemPage.class);

    public PasteItemPage() {
        super(PasteItemPage.class);

        add(new PasteForm("pasteForm", new CompoundPropertyModel<PasteItem>(new PasteItem())));
        add(new Spin());
    }

    protected String getTitle() {
        return "New - Mystic Paste";
    }

    private String spamEmail;

    public String getSpamEmail() {
        return spamEmail;
    }

    public void setSpamEmail(String spamEmail) {
        this.spamEmail = spamEmail;
    }


    public class PasteForm extends StatelessForm<PasteItem> {

        private LanguageSyntax languageType = HighlighterPanel.getLanguageSyntax("plain");

        public LanguageSyntax getLanguageType() {
            return languageType;
        }

        public void setLanguageType(LanguageSyntax languageType) {
            this.languageType = languageType;
        }

        protected void onPaste(boolean isPrivate) {

            PasteItem pasteItem = PasteForm.this.getModelObject();
            if (pasteItem.getContent() == null || pasteItem.getContent().equals("")) {
                error("Paste content is required!");
                feedbackContainer.setVisible(true);
                return;
            }

            if (getSpamEmail() != null || StringUtils.hasSpamKeywords(pasteItem.getContent())) {
                error("Spam Spam Spam Spam");
                feedbackContainer.setVisible(true);
                return;
            }

            pasteItem.setPrivate(isPrivate);
            pasteItem.setType(getLanguageType() != null ? getLanguageType().getLanguage() : "text");
            pasteItem.setClientIp(getClientIpAddress());

            logger.info("New " + pasteItem.getContent() + " line " + (isPrivate ? "private" : "public") + " paste created with IP:" + getClientIpAddress() + " language set at:" + pasteItem.getType());

            pasteService.createItem("web", pasteItem);
            PageParameters params = new PageParameters();
            if (pasteItem.isPrivate()) {
//                    this.setRedirect(true);
                params.add("0", pasteItem.getItemId());
                setResponsePage(ViewPrivatePage.class, params);
            } else {
//                    this.setRedirect(true);
                params.add("0", pasteItem.getItemId());
                setResponsePage(ViewPublicPage.class, params);
            }
        }

/*
        private FileUploadField fileUploadField;

        private FileUpload imageFile;
*/

        private WebMarkupContainer feedbackContainer;

        public PasteForm(String id, IModel<PasteItem> model) {
            super(id, model);

            FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
            feedbackContainer = new WebMarkupContainer("feedbackContainer");
            feedbackContainer.add(feedbackPanel);
            feedbackContainer.setVisible(false);
            add(feedbackContainer);

            languageType = HighlighterPanel.getLanguageSyntax("text");          // default to text per AMcBain

/*
            final SubmitLink pasteButton = new SubmitLink("paste") {
                @Override
                public void onSubmit() {
                    onPaste(false);
                }
            };
*/
            Button pasteButton = new Button("paste") {
                @Override
                public void onSubmit() {
                    onPaste(false);
                }
            };

            Button privatePasteButton = new Button("privatePaste") {
                @Override
                public void onSubmit() {
                    onPaste(true);
                }
            };

            add(pasteButton, privatePasteButton);

            DropDownChoice languageDDC = new DropDownChoice<LanguageSyntax>("type",
                    new PropertyModel<LanguageSyntax>(PasteForm.this, "languageType"),
                    HighlighterPanel.getLanguageSyntaxList(), new IChoiceRenderer<LanguageSyntax>() {
                public String getDisplayValue(LanguageSyntax syntax) {
                    return syntax.getName();
                }

                public String getIdValue(LanguageSyntax languageSyntax, int index) {
                    return languageSyntax.getLanguage();
                }

            });
            add(languageDDC);

            TextArea<String> contentTextArea = new TextArea<String>("content");
            contentTextArea.add(new DefaultFocusBehavior());
            contentTextArea.setMarkupId("content");

            add(contentTextArea);

            HiddenField imageLocationField = new HiddenField("imageLocation");
            imageLocationField.setMarkupId("imageLocation");
            add(imageLocationField);
            add(new TextField<String>("email", new PropertyModel<String>(PasteItemPage.this, "spamEmail")));

            final AjaxFormSubmitBehavior doneWithPaste = new AjaxFormSubmitBehavior(this, "domready") {

                public void renderHead(final Component component, final IHeaderResponse response) { }

                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    onPaste(true);
                }
            };
            add(doneWithPaste);

            getMousetrap().addGlobalBind(new KeyBinding()
                    .addKeyCombo(KeyBinding.COMMAND, KeyBinding.ENTER)
                    .addKeyCombo(KeyBinding.ALT, KeyBinding.ENTER),
                    doneWithPaste);
        }


        /**
         * Check whether the file already exists, and if so, try to delete it.
         *
         * @param newFile the file to check
         */
        private void checkFileExists(File newFile) {
            if (newFile.exists()) {
                // Try to delete the file
                if (!Files.remove(newFile)) {
                    throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
                }
            }
        }

        private Folder getUploadFolder() {
            return ((MysticPasteApplication) Application.get()).getUploadFolder();
        }


    }
}