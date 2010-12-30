package com.mysticcoders.mysticpaste;

import com.mysticcoders.mysticpaste.web.pages.history.HistoryPage;
import com.mysticcoders.mysticpaste.web.pages.paste.PasteItemPage;
import com.mysticcoders.mysticpaste.web.pages.plugin.PluginPage;
import com.mysticcoders.mysticpaste.web.pages.view.ViewPastePage;
import com.mysticcoders.mysticpaste.web.pages.view.ViewPrivatePage;
import com.mysticcoders.mysticpaste.web.pages.view.ViewPublicPage;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.devutils.stateless.StatelessChecker;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.MountedMapper;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.file.Folder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 */
public class MysticPasteApplication extends WebApplication {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Folder uploadFolder = null;
    
    /**
     * Constructor
     */
    public MysticPasteApplication() {
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void init() {
        super.init();

        uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "wicket-uploads");
/*
        // Ensure folder exists
        if(!uploadFolder.mkdirs()) {
            throw new RuntimeException("Fatal error: Can't create temp directory for uploads at: " + System.getProperty("java.io.tmpdir"));
        }
*/

        getComponentInstantiationListeners().add(getSpringComponentInjector(this));
        getComponentPreOnBeforeRenderListeners().add(new StatelessChecker());

        getMarkupSettings().setStripWicketTags(true);


        mountPage("/home", HomePage.class);
        mountPage("/new", PasteItemPage.class);
        mountPage("/history", HistoryPage.class);
        mountPage("/plugin", PluginPage.class);



//        mount(new MountedMapper("/view/${pasteId}", ViewPublicPage.class));
        mountPage("/view/${0}/${1}", ViewPublicPage.class);
        mountPage("/private/${0}/${1}", ViewPrivatePage.class);

//        getRootRequestMapperAsCompound().add(new MountedMapper("/view/${pasteId}/${highlightLines}", ViewPublicPage.class));
//        getRootRequestMapperAsCompound().add(new MountedMapper("/private/${pasteId}", ViewPrivatePage.class));
//        getRootRequestMapperAsCompound().add(new MountedMapper("/private/${pasteId}/${highlightLines}", ViewPrivatePage.class));

        ServletContext servletContext = super.getServletContext();
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }

    /**
     * @return the folder for uploads
     */
    public Folder getUploadFolder() {
        return uploadFolder;
    }

    protected IComponentInstantiationListener getSpringComponentInjector(WebApplication application) {
        return new SpringComponentInjector(application);
    }

    public static MysticPasteApplication getInstance() {
        return ((MysticPasteApplication) WebApplication.get());
    }

    public Class<PasteItemPage> getHomePage() {
        return PasteItemPage.class;
    }

    private ApplicationContext applicationContext;


    public Object getBean(String name) {
        if (name == null) return null;

        return applicationContext.getBean(name);
    }

}