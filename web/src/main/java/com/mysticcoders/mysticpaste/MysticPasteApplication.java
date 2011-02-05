package com.mysticcoders.mysticpaste;

import com.mysticcoders.mysticpaste.utils.PriorityResourceMapper;
import com.mysticcoders.mysticpaste.web.pages.history.HistoryPage;
import com.mysticcoders.mysticpaste.web.pages.paste.PasteItemPage;
import com.mysticcoders.mysticpaste.web.pages.plugin.PluginPage;
import com.mysticcoders.mysticpaste.web.pages.view.PasteAsTextResource;
import com.mysticcoders.mysticpaste.web.pages.view.DownloadPasteAsTextResource;
import com.mysticcoders.mysticpaste.web.pages.view.ViewPrivatePage;
import com.mysticcoders.mysticpaste.web.pages.view.ViewPublicPage;
import org.apache.wicket.Application;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.devutils.stateless.StatelessChecker;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.mapper.ResourceMapper;
import org.apache.wicket.request.mapper.mount.MountMapper;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
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

        mountPage("/new", PasteItemPage.class);
        mountPage("/history", HistoryPage.class);
        mountPage("/plugin", PluginPage.class);

        mountPage("/view/${0}", ViewPublicPage.class);
//        mountPage("/view", ViewPublicPage.class);             // map the highlight line URL

        getRootRequestMapperAsCompound().add(new PriorityResourceMapper("/view/${0}/text", new PasteAsTextResource()));
        getRootRequestMapperAsCompound().add(new PriorityResourceMapper("/view/${0}/download", new DownloadPasteAsTextResource()));
//        mount(new MountMapper("/view/${pasteId}/text", new ResourceReferenceRequestHandler(new PasteAsTextResource())));

        mountPage("/private/${0}", ViewPrivatePage.class);
        mountPage("/private/${0}/${1}", ViewPrivatePage.class);         // map the highlight line URL

        ResourceReference pasteAsTextResource = new PasteAsTextResource();

        if (pasteAsTextResource.canBeRegistered()) {
            getResourceReferenceRegistry().registerResourceReference(pasteAsTextResource);
        }

        ResourceReference downloadPasteAsTextResource = new DownloadPasteAsTextResource();

        if (pasteAsTextResource.canBeRegistered()) {
            getResourceReferenceRegistry().registerResourceReference(downloadPasteAsTextResource);
        }

        getRootRequestMapperAsCompound().add(new PriorityResourceMapper("/private/${0}/text", pasteAsTextResource));
        getRootRequestMapperAsCompound().add(new PriorityResourceMapper("/private/${0}/download", downloadPasteAsTextResource));

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