package com.mysticcoders.mysticpaste.web.pages;

import com.mysticcoders.mysticpaste.web.components.FixBootstrapStylesCssResourceReference;
import com.mysticcoders.mysticpaste.web.components.HelpModal;
import com.mysticcoders.mysticpaste.web.components.google.GoogleAnalyticsSnippet;
import com.mysticcoders.mysticpaste.web.pages.history.HistoryPage;
import com.mysticcoders.mysticpaste.web.pages.paste.PasteItemPage;
import com.mysticcoders.mysticpaste.web.pages.plugin.PluginPage;
import com.mysticcoders.wicket.mousetrap.KeyBinding;
import com.mysticcoders.wicket.mousetrap.Mousetrap;
import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.ChromeFrameMetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.HtmlTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.MetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.OptimizedMobileViewportMetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarComponents;
import de.agilecoders.wicket.core.markup.html.references.BootstrapJavaScriptReference;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import org.apache.wicket.Application;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.tester.WicketTesterHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Base Page for the application.
 * Extend this page to pull in the application header/footer.
 *
 * @author Steve Forsyth
 *         Date: Mar 8, 2009
 */
public class BasePage extends WebPage {

    Class activePage;
    Mousetrap mousetrap;

    private static final Logger logger = LoggerFactory.getLogger(BasePage.class);

    public BasePage() {
        init();
    }

    public BasePage(Class activePage) {
        this.activePage = activePage;
        init();
    }

    public BasePage(PageParameters params) {
        super(params);
        init();
    }

    protected String getTitle() {
        return "Mystic Paste";
    }

    protected Mousetrap mousetrap() {
        return mousetrap;
    }

    private void init() {

        add(new HtmlTag("html"));

        add(new OptimizedMobileViewportMetaTag("viewport"));
        add(new ChromeFrameMetaTag("chrome-frame"));
        add(new MetaTag("description", Model.of("description"), Model.of("Mystic Paste")));
        add(new MetaTag("author", Model.of("author"), Model.of("Andrew Lombardi")));

        add(newNavbar("navbar"));
        add(new Footer("footer"));

        add(mousetrap = new Mousetrap());
        add(new HelpModal("helpModal", "Help"));

        add(new GoogleAnalyticsSnippet("ga-js") {
            public String getTracker() {
                return "UA-254925-6";
            }

            public boolean isVisible() {
                return true;
            }
        });

        final AbstractDefaultAjaxBehavior newNav = new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                System.out.println("newNav");
                throw new RestartResponseException(PasteItemPage.class);
            }
        };
        add(newNav);

        final AbstractDefaultAjaxBehavior historyNav = new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                System.out.println("historyNav");
                throw new RestartResponseException(HistoryPage.class);
            }
        };
        add(historyNav);

//        mousetrap.addBind(new KeyBinding().addKeyCombo("n").addKeyCombo("N"), newNav);

        String newPasteStr = "Wicket.Ajax.get({'u': '" + newNav.getCallbackUrl() + "'})";
        mousetrap.addBindJs(new KeyBinding().addKeyCombo("n"), newPasteStr);
        mousetrap.addBind(new KeyBinding(KeyBinding.EVENT_KEYUP).addKeyCombo("h").addKeyCombo("H"),
                historyNav);
        /*
        mousetrap.addBindJs(new KeyBinding().addKeyCombo("?"),
                "$('#helpModal').modal();"
                );
        */
        add(new BootstrapBaseBehavior());
    }

    /**
     * creates a new {@link Navbar} instance
     *
     * @param markupId The components markup id.
     * @return a new {@link Navbar} instance
     */
    protected Navbar newNavbar(String markupId) {
        Navbar navbar = new Navbar(markupId);
        navbar.setPosition(Navbar.Position.TOP);
        navbar.setInverted(false);

        // show brand name and logo
        navbar.brandName(Model.of("Mystic Paste"));

        navbar.addComponents(NavbarComponents.transform(Navbar.ComponentPosition.LEFT,
                new NavbarButton<PasteItemPage>(PasteItemPage.class, Model.of("New")),
                new NavbarButton<HistoryPage>(HistoryPage.class, Model.of("History")),
//                new NavbarButton<PopularPage>(PopularPage.class, Model.of("Popular")),
                new NavbarButton<PluginPage>(PluginPage.class, Model.of("Plugins")),
                new NavbarButton<HelpPage>(HelpPage.class, Model.of("Help"))
        ));

        return navbar;
    }

    @Override
    protected void onBeforeRender() {
        addOrReplace(new Label("title", getTitle()));

        super.onBeforeRender();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(FixBootstrapStylesCssResourceReference.INSTANCE));
        response.render(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));
        response.render(JavaScriptHeaderItem.forReference(BootstrapJavaScriptReference.instance()));
    }

    /**
     * sets the theme for the current user.
     *
     * @param pageParameters current page parameters
     */
    private void configureTheme(PageParameters pageParameters) {
        StringValue theme = pageParameters.get("theme");

        if (!theme.isEmpty()) {
            IBootstrapSettings settings = Bootstrap.getSettings(getApplication());
            settings.getActiveThemeProvider().setActiveTheme(theme.toString(""));
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        configureTheme(getPageParameters());
    }

    /**
     * Returns the X-Forwarded-For header
     *
     * @return
     */
    protected String getClientIpAddress() {
        ServletWebRequest request = (ServletWebRequest) RequestCycle.get().getRequest();
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getContainerRequest().getRemoteHost();
        }
        return ipAddress;
    }

    protected String getReferrer() {
        ServletWebRequest request = (ServletWebRequest) RequestCycle.get().getRequest();

        return request.getHeader("referer");
    }

    private String serverName;

    protected String getServerName() {
        return serverName;
    }

}
