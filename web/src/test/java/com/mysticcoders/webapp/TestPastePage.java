package com.mysticcoders.webapp;

import com.mysticcoders.integrations.AbstractIntegrationTest;
import com.mysticcoders.mysticpaste.HomePage;
import com.mysticcoders.mysticpaste.MysticPasteApplication;
import com.mysticcoders.mysticpaste.web.pages.paste.PasteItemPage;
import com.mysticcoders.mysticpaste.web.pages.view.ViewPublicPage;
import com.mysticcoders.mysticpaste.web.pages.history.HistoryPage;
import com.mysticcoders.mysticpaste.web.pages.BasePage;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import com.mysticcoders.mysticpaste.persistence.PasteCommentDao;
import com.mysticcoders.mysticpaste.services.PasteService;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.injection.annot.test.AnnotApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.Application;
import org.junit.Before;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBeanByType;

/**
 * Simple test using the WicketTester
 */

public class TestPastePage extends AbstractIntegrationTest {

    @SpringBeanByType
    private PasteService svc;

    @SpringBeanByType
    private PasteItemDao dao;

    @SpringBeanByType
    private PasteCommentDao commentDao;

    protected WicketTester tester;

    @Before
    public void setup() {
        final AnnotApplicationContextMock appctx = new
                AnnotApplicationContextMock();
        appctx.putBean("pasteDao", dao);
        appctx.putBean("pasteService", svc);
        appctx.putBean("pasteCommentDao", commentDao);

        tester = new WicketTester(new MysticPasteApplication() {
            @Override
            protected IComponentInstantiationListener getSpringComponentInjector(WebApplication application) {
                return new SpringComponentInjector(application, appctx, true);
            }
        });
    }

    @Test
    public void testRenderMyPage() {
        tester.startPage(PasteItemPage.class);
        tester.assertRenderedPage(PasteItemPage.class);
        FormTester ft = tester.newFormTester("pasteForm");
        ft.select("type", 0);
        ft.setValue("content", "blahblahblah");
        ft.submit();
        tester.assertRenderedPage(ViewPublicPage.class);
        tester.assertContains("blahblahblah");
        tester.assertLabel("type", "as3");
    }

    @Test
    public void testHistoryMenuClick() {
        tester.startPage(PasteItemPage.class);
        tester.assertRenderedPage(PasteItemPage.class);
        tester.clickLink("historyLinkContainer:historyLink");
        tester.assertRenderedPage(HistoryPage.class);
    }
}