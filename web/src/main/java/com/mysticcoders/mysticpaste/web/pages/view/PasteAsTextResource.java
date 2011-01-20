package com.mysticcoders.mysticpaste.web.pages.view;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.InvalidClientException;
import com.mysticcoders.mysticpaste.services.PasteService;

public class PasteAsTextResource extends AbstractResource {

    @SpringBean
    private static PasteService pasteService;

    private static final long serialVersionUID = 1L;

    public PasteAsTextResource() {
        super();

        Injector.get().inject(this);
    }

    protected PasteItem pasteItem;

	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
		ResourceResponse resourceResponse = newResourceResponse(attributes);

		if (resourceResponse.dataNeedsToBeWritten(attributes)) {
			PageParameters params = attributes.getParameters();
					
	        try {
	            if (!params.get("0").isNull()){
	                pasteItem = pasteService.getItem("web", params.get("0").toLong());
	            } else {
	                pasteItem = pasteService.findPrivateItem("web", params.get("0").toString());
	            }
	
	            resourceResponse.setContentType("text/plain");
	            resourceResponse.setWriteCallback(new WriteCallback() {
					
					@Override
					public void writeData(Attributes arg0) {
						arg0.getResponse().write(pasteItem.getContent().getBytes());
					}
				});
	            
	        } catch (InvalidClientException e) {
	        }
		}
		
		return resourceResponse;
	}


}
