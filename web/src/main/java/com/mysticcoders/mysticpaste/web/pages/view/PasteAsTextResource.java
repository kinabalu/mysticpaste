package com.mysticcoders.mysticpaste.web.pages.view;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.PasteService;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;


public class PasteAsTextResource extends ResourceReference {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SpringBean
    private static PasteService pasteService;

    private static final long serialVersionUID = 1L;

    public PasteAsTextResource(String scope, String name) {
        super(new Key(scope, name, Locale.ENGLISH, null, null));
        Injector.get().inject(this);
    }

    public PasteAsTextResource() {
        this(PasteAsTextResource.class.getName(), "pasteAsTextResource");
    }

    protected ContentDisposition getContentDisposition() {
        return ContentDisposition.INLINE;
    }

    protected String getFileName(PageParameters params) {
        return null;
    }

    protected PasteItem pasteItem;

    @Override
    public IResource getResource() {

        return new AbstractResource() {
            private static final long serialVersionUID = 1L;

            @Override
            protected ResourceResponse newResourceResponse(Attributes attributes) {
                ResourceResponse resourceResponse = new ResourceResponse();

                if (resourceResponse.dataNeedsToBeWritten(attributes)) {
                    PageParameters params = attributes.getParameters();

                    resourceResponse.setContentDisposition(getContentDisposition());

                    if (getFileName(params) != null) {
                        resourceResponse.setFileName(getFileName(params));
                    }

                    resourceResponse.setContentType("text/plain");

                    resourceResponse.setWriteCallback(new WriteCallback() {
                        @Override
                        public void writeData(Attributes attributes) {
                            PageParameters params = attributes.getParameters();
                            if (params.get("0").isNull()) {
                                // handle this with a big fat error page?
                                return;
                            }

                            String pasteItemId = params.get("0").toString();
                            pasteItem = pasteService.getItem("web", pasteItemId);

                            logger.info("Paste ID[" + pasteItem.getItemId() + "] requested text / download");
                            if (pasteItem.isPrivate()) {
                                //X-Robots-Tag: noindex
                                ((WebResponse) attributes.getResponse()).addHeader("X-Robots-Tag", "noindex");
                            }
                            if (pasteItem.getAbuseCount() <= 2) {
                                attributes.getResponse().write(pasteItem.getContent().getBytes());
                            } else {
                                logger.info("Abuse Paste accessed id[" + pasteItem.getItemId() + "]");
                                attributes.getResponse().write("Marked for abuse.".getBytes());
                            }
                        }
                    });

                }

                return resourceResponse;
            }
        };
    }
}