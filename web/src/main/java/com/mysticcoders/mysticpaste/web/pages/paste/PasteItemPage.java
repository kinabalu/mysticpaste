package com.mysticcoders.mysticpaste.web.pages.paste;

import com.mysticcoders.mysticpaste.MysticPasteApplication;
import com.mysticcoders.mysticpaste.model.LanguageSyntax;
import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.InvalidClientException;
import com.mysticcoders.mysticpaste.services.PasteService;
import com.mysticcoders.mysticpaste.web.components.DefaultFocusBehavior;
import com.mysticcoders.mysticpaste.web.components.highlighter.HighlighterPanel;
import com.mysticcoders.mysticpaste.web.pages.BasePage;
import com.mysticcoders.mysticpaste.web.pages.view.ViewPrivatePage;
import com.mysticcoders.mysticpaste.web.pages.view.ViewPublicPage;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;

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

    public PasteItemPage() {
        super(PasteItemPage.class);

        add(new PasteForm("pasteForm", new CompoundPropertyModel<PasteItem>(new PasteItem())));
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

        private void onPaste(boolean isPrivate) {


/*
            final FileUpload upload = fileUploadField.getFileUpload();
            if (upload != null)
            {
                // Create a new file
                File newFile = new File(getUploadFolder(), upload.getClientFileName());

                // Check new file, delete if it allready existed
                checkFileExists(newFile);
                try
                {
                    // Save to new file
                    newFile.createNewFile();
                    upload.writeTo(newFile);

                    System.out.println("upload.clientFileName:"+upload.getClientFileName());
                    //UploadPage.this.info("saved file: " + upload.getClientFileName());
                }
                catch (Exception e)
                {
                    throw new IllegalStateException("Unable to write file");
                }
            }
*/

            PasteItem pasteItem = PasteForm.this.getModelObject();
            if (pasteItem.getContent() == null || pasteItem.getContent().equals("")) {
                error("Paste content is required!");
                return;
            }

            if (getSpamEmail() != null || hasSpamKeywords(pasteItem.getContent())) {
                error("Spam Spam Spam Spam");
                return;
            }

            pasteItem.setPrivate(isPrivate);
            pasteItem.setType(getLanguageType() != null ? getLanguageType().getLanguage() : "text");
            pasteItem.setClientIp(getClientIpAddress());

            try {
                pasteService.createItem("web", pasteItem);
                PageParameters params = new PageParameters();
                if (pasteItem.isPrivate()) {
//                    this.setRedirect(true);
                    params.add("0", pasteItem.getPrivateToken());
                    setResponsePage(ViewPrivatePage.class, params);
                } else {
//                    this.setRedirect(true);
                    params.add("0", Long.toString(pasteItem.getId()));
                    setResponsePage(ViewPublicPage.class, params);
                }
            } catch (InvalidClientException e) {
                // Do nothing at this point as we haven't defined what an "Invalid Client" is.
                e.printStackTrace();
            }
        }

        public boolean hasSpamKeywords(String content) {
            String lowercasedContent = content.toLowerCase();

            for (String badWord : badWords) {
                if (lowercasedContent.indexOf(badWord) != -1) return true;
            }

            return false;
        }

/*
        private FileUploadField fileUploadField;

        private FileUpload imageFile;
*/

        public PasteForm(String id, IModel<PasteItem> model) {
            super(id, model);

            add(new FeedbackPanel("feedback"));

            languageType = HighlighterPanel.getLanguageSyntax("text");          // default to text per AMcBain

/*
            setMultiPart(true);
            setMaxSize(Bytes.kilobytes(1024));

            add(fileUploadField = new FileUploadField("imageFile", new PropertyModel(PasteForm.this,  "imageFile")));
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

            add(contentTextArea);

            add(new TextField<String>("email", new PropertyModel<String>(PasteItemPage.this, "spamEmail")));
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

        private String[] badWords = new String[]{
                "[/URL]",
                "[/url]",
                "adipex",
                "adultfriendfinder",
                "adult-dvd",
                "adult-friend-finder",
                "adult-personal",
                "adult personal",
                "adult-stories",
                "adult friends",
                "boob",
                "casino",
                "cheap hotel",
                "cialis",
                "classified ad",
                "diazepam",
                "diazepan",
                "fiksa.org",
                "forexcurrency",
                "free ringtones",
                "fuck",
                "gay porn",
                "geo.ya",
                "httpgeo",
                "hot sex",
                "hydroconone",
                "incest",
                "inderal",
                "insulin",
                "jewish dating",
                "keflex",
                "klonopin",
                "lamictal",
                "lasix",
                "levaquin",
                "levitra",
                "lipitor",
                "male porn",
                "malenhancement",
                "masya",
                "mature porn",
                "milf",
                "murphy bed",
                "nude celebrity",
                "oxycodone",
                "paxil",
                "payday",
                "phenergan",
                "phentermine",
                "poker",
                "porn link",
                "porn video",
                "porno portal",
                "pornmaster",
                "premarin",
                "prozac",
                "rape",
                "strattera",
                "tramadol",
                "tussionex",
                "valium",
                "viagra",
                "vicodin",
                "web gratis",
                "without prescription",
                "xanax",
                "xxx ",
                " xxx",
                "xxxvideo",
                "youradult",
                "zelnorm",
                "zenegra",
                "megaupload.com",
                "members.lycos.co.uk",
                "lix.in"
        };
    }
}