package com.mysticcoders.webapp;

import com.mysticcoders.integrations.AbstractIntegrationTest;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import com.mysticcoders.mysticpaste.services.PasteService;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBeanByType;

/**
 * Simple test using the WicketTester
 */

public class TestHomePage extends AbstractIntegrationTest {

    @SpringBeanByType
    private PasteService svc;

    @SpringBeanByType
    private PasteItemDao dao;

    protected WicketTester tester;

    @Before
    public void setup() {
        ApplicationContextMock appctx = new
                ApplicationContextMock();
        appctx.putBean("pasteDao", dao);
        appctx.putBean("pasteService", svc);

        // if we inject spring here, its more of an integration test, and that is not fun
//        tester = new WicketTester(new MysticPasteApplication());
        tester = new WicketTester();
        WebApplication app = tester.getApplication();

//        app.addComponentInstantiationListener(new SpringComponentInjector(app, appctx));


    }

    @Test
    public void testRenderMyPage() {
        //start and render the test page
//        tester.startPage(HomePage.class);

        //assert rendered page class
//        tester.assertRenderedPage(HomePage.class);

        //assert rendered label component
        tester.assertLabel("message", "If you see this message wicket is properly configured and running");
    }
}
