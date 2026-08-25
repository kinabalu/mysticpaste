package com.mysticcoders.mysticpaste.web;

import com.mysticcoders.mysticpaste.web.pages.HelpPage;
import com.mysticcoders.mysticpaste.web.pages.LegalPage;
import com.mysticcoders.mysticpaste.web.pages.plugin.PluginPage;
import org.junit.jupiter.api.Test;

public class StaticPageTest extends AbstractPageTest {

    @Test
    public void helpPageRenders() {
        tester.startPage(HelpPage.class);
        tester.assertRenderedPage(HelpPage.class);
        tester.assertNoErrorMessage();
    }

    @Test
    public void legalPageRenders() {
        tester.startPage(LegalPage.class);
        tester.assertRenderedPage(LegalPage.class);
        tester.assertNoErrorMessage();
    }

    @Test
    public void pluginPageRenders() {
        tester.startPage(PluginPage.class);
        tester.assertRenderedPage(PluginPage.class);
        tester.assertNoErrorMessage();
    }
}
