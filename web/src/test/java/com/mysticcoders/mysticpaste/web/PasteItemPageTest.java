package com.mysticcoders.mysticpaste.web;

import com.mysticcoders.mysticpaste.web.pages.paste.PasteItemPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.junit.jupiter.api.Test;

public class PasteItemPageTest extends AbstractPageTest {

    @Test
    public void homePageRenders() {
        tester.startPage(PasteItemPage.class);
        tester.assertRenderedPage(PasteItemPage.class);
        tester.assertNoErrorMessage();
    }

    @Test
    public void pasteFormHasExpectedComponents() {
        tester.startPage(PasteItemPage.class);
        tester.assertRenderedPage(PasteItemPage.class);

        tester.assertComponent("pasteForm", Form.class);
        tester.assertComponent("pasteForm:type", DropDownChoice.class);
        tester.assertComponent("pasteForm:content", TextArea.class);
        tester.assertComponent("pasteForm:email", TextField.class);
        tester.assertComponent("pasteForm:paste", Button.class);
        tester.assertComponent("pasteForm:privatePaste", Button.class);
    }

    @Test
    public void spamHoneypotRejectsThePaste() {
        tester.startPage(PasteItemPage.class);
        tester.assertRenderedPage(PasteItemPage.class);

        FormTester form = tester.newFormTester("pasteForm", false);
        form.setValue("content", "Here is some test code");
        form.setValue("email", "spammer@spam.com");
        form.submit("paste");

        tester.assertErrorMessages("Spam Spam Spam Spam");
    }

    @Test
    public void emptyPasteIsRejected() {
        tester.startPage(PasteItemPage.class);
        tester.assertRenderedPage(PasteItemPage.class);

        FormTester form = tester.newFormTester("pasteForm", false);
        form.submit("paste");

        tester.assertErrorMessages("Paste content is required!");
    }
}
