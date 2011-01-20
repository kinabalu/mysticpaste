package com.mysticcoders.mysticpaste.web.pages.view;

import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: 1/2/11
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class DownloadPasteAsTextResource extends PasteAsTextResource {

    public DownloadPasteAsTextResource() {
        super();

        //header('Content-Disposition: attachment; filename="downloaded.pdf"');


    }

    /**
     * Set the Content-Disposition header so we can initiate a download
     *
     * TODO would be great to find out what kind of highlighting they asked for so we could append the proper extension
     *
     * @param response
     */
    @Override
	protected ResourceResponse newResourceResponse(Attributes arg0) {
		ResourceResponse resourceResponse = super.newResourceResponse(arg0);

		PageParameters parameters = arg0.getParameters();

        ((WebResponse)arg0.getResponse()).setHeader("Content-Disposition", "attachment; filename=\""+parameters.get("0")+".txt\"");
        
        return resourceResponse;
    }
}
