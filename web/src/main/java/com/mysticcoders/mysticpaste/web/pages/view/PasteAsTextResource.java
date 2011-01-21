package com.mysticcoders.mysticpaste.web.pages.view;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.InvalidClientException;
import com.mysticcoders.mysticpaste.services.PasteService;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class PasteAsTextResource extends AbstractResource {

    @SpringBean
    private static PasteService pasteService;

    private static final long serialVersionUID = 1L;

    public PasteAsTextResource() {

        Injector.get().inject(this);
    }

    protected ContentDisposition getContentDisposition() {
        return ContentDisposition.INLINE;
    }

    protected String getFileName(PageParameters params) {
        return null;
    }

    protected PasteItem pasteItem;

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {

        ResourceResponse resourceResponse = new ResourceResponse();

        if (resourceResponse.dataNeedsToBeWritten(attributes)) {
            PageParameters params = attributes.getParameters();

            try {

                if (params.get("0").isNull()) {
                    pasteItem = pasteService.getItem("web", params.get("0").toLong());
                } else {
                    pasteItem = pasteService.findPrivateItem("web", params.get("0").toString());
                }

                resourceResponse.setContentDisposition(getContentDisposition());

                if (getFileName(params) != null) {
                    resourceResponse.setFileName(getFileName(params));
                }

                resourceResponse.setContentType("text/plain");

                resourceResponse.setWriteCallback(new WriteCallback() {
                    @Override
                    public void writeData(Attributes attributes) {
                        attributes.getResponse().write(pasteItem.getContent().getBytes());
                    }
                });

            } catch (InvalidClientException e) {
            }
        }
        return resourceResponse;
    }
}