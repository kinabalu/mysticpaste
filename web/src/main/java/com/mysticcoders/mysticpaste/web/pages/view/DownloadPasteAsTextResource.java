package com.mysticcoders.mysticpaste.web.pages.view;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContentDisposition;

/**
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: 1/2/11
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class DownloadPasteAsTextResource extends PasteAsTextResource {

    public DownloadPasteAsTextResource() {
        super(DownloadPasteAsTextResource.class.getName(), "downloadPasteAsTextResource");
    }

    /**
     * TODO would be great to find out what kind of highlighting they asked for so we could append the proper extension
     */
    public ContentDisposition getContentDisposition() {
        return ContentDisposition.ATTACHMENT;
    }

    public String getFileName(PageParameters params) {
        return "" + params.get("0") + ".txt";
    }

}
