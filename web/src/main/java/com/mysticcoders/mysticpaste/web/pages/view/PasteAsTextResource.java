package com.mysticcoders.mysticpaste.web.pages.view;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.InvalidClientException;
import com.mysticcoders.mysticpaste.services.PasteService;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.DynamicWebResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;

public class PasteAsTextResource extends DynamicWebResource {

    @SpringBean
    private static PasteService pasteService;

    private static final long serialVersionUID = 1L;

    public PasteAsTextResource() {
        super();

        InjectorHolder.getInjector().inject(this);
    }

    protected PasteItem pasteItem;

    @Override
    protected ResourceState getResourceState() {
        ValueMap params = getParameters();

        try {
            if(params.getAsLong("0")!=null){
                pasteItem = pasteService.getItem("web", params.getAsLong("0"));
            } else {
                pasteItem = pasteService.findPrivateItem("web", params.getString("0"));
            }

            return new ResourceState() {

                @Override
                public byte[] getData() {
                    return pasteItem.getContent().getBytes();
                }

                @Override
                public String getContentType() {
                    return "text/plain";
                }
            };

        } catch (InvalidClientException e) {
        }

        return null;
    }


}
