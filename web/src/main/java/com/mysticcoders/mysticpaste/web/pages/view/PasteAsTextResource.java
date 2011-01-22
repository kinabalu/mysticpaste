package com.mysticcoders.mysticpaste.web.pages.view;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.InvalidClientException;
import com.mysticcoders.mysticpaste.services.PasteService;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.string.StringValue;

import java.util.Locale;


public class PasteAsTextResource extends ResourceReference {

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
                            try {

                                if(params.get("0").isNull()) {
                                    // handle this with a big fat error page?
                                    return;
                                }
                                try {
                                    long pasteItemId = params.get("0").toLong();

                                    pasteItem = pasteService.getItem("web", pasteItemId);
                                } catch(NumberFormatException e) {
                                    pasteItem = pasteService.findPrivateItem("web", params.get("0").toString());
                                }

                                attributes.getResponse().write(pasteItem.getContent().getBytes());
                            } catch (InvalidClientException e) {
                            }

                        }
                    });

                }

                return resourceResponse;
            }
        };
    }
}