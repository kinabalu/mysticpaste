package com.mysticcoders.mysticpaste.web.pages.view;

import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.value.ValueMap;

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
    protected void setHeaders(WebResponse response) {
        ValueMap params = getParameters();

        response.setHeader("Content-Disposition", "attachment; filename=\""+params.getString("0")+".txt\"");
    }
}
