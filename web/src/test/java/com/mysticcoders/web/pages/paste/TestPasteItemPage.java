/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Oct 9, 2009
 * Time: 12:35:25 PM
 */
package com.mysticcoders.web.pages.paste;

import com.mysticcoders.mysticpaste.web.pages.paste.PasteItemPage;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

public class TestPasteItemPage {

    private WicketTester wicket;


    @Before
    public void setUp() {
        wicket = new WicketTester();
    }

    @Test
    public void testSubmitPaste() {
        //start and render the test page
        wicket.startPage(PasteItemPage.class);

        //assert rendered page class
        wicket.assertRenderedPage(PasteItemPage.class);

        FormTester form = wicket.newFormTester("pasteForm", true);
        form.setValue("content", "Here is some test code");
        form.setValue("email", "spammer@spam.com");
        form.submit("paste");

        wicket.assertErrorMessages(new String[] { "Spam Spam Spam Spam" });
    }

    @Test
    public void testContainsFormComponents() {
        wicket.startPage(PasteItemPage.class);
        wicket.assertRenderedPage(PasteItemPage.class);

        wicket.assertComponent("pasteForm", Form.class);
        wicket.assertComponent("pasteForm:private", CheckBox.class);
        wicket.assertComponent("pasteForm:twitter", CheckBox.class);
        wicket.assertComponent("pasteForm:type", DropDownChoice.class);
        wicket.assertComponent("pasteForm:content", TextArea.class);
        wicket.assertComponent("pasteForm:email", TextField.class);
    }

}