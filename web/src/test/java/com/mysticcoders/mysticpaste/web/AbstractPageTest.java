package com.mysticcoders.mysticpaste.web;

import com.mysticcoders.mysticpaste.MysticPasteApplication;
import com.mysticcoders.mysticpaste.services.PasteService;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;

/**
 * Boots the real application against a mocked Spring context so pages can be rendered
 * without a live MongoDB or Redis.
 */
public abstract class AbstractPageTest {

    protected WicketTester tester;

    protected PasteService pasteService;

    @BeforeEach
    public void setUp() {
        pasteService = mock(PasteService.class);

        final ApplicationContextMock context = new ApplicationContextMock();
        context.putBean("pasteService", pasteService);

        tester = new WicketTester(new MysticPasteApplication() {
            @Override
            protected IComponentInstantiationListener getSpringComponentInjector(WebApplication application) {
                return new SpringComponentInjector(application, context);
            }
        });
    }

    @AfterEach
    public void tearDown() {
        if (tester != null) {
            tester.destroy();
        }
    }
}
