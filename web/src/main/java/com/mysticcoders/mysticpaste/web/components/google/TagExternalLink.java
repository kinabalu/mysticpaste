/**
 * A new type of External Link that injects the pageTracker code for tagging
 * a specific page URL
 * 
 * @author Andrew Lombardi
 * Date: Oct 15, 2009
 * Time: 12:35:51 PM
 */
package com.mysticcoders.mysticpaste.web.components.google;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;

public class TagExternalLink extends ExternalLink {

    private String prefix = "/outbound/";
    private String suffix = "";

    public TagExternalLink(String id, String href, String label) {
        super(id, href, label);
    }

    public TagExternalLink(String id, String href) {
        super(id, href);
    }

    public TagExternalLink(String id, IModel<String> href, IModel<String> label) {
        super(id, href, label);
    }

    public TagExternalLink(String id, IModel<String> href) {
        super(id, href);
    }

    /**
     * Returns the prefix to place in the pageTracker tagging url
     *
     * @return prefix of the external link
     */
    protected String getPrefix() {
        return prefix;
    }

    /**
     * Returns the suffix to place after the pageTracker tagging url
     *
     * @return suffix of the external link
     */
    protected String getSuffix() {
        return suffix;
    }

	/**
	 * Processes and adds the Google Tracking code to our external link
	 *
	 * @param tag
	 *            Tag to modify
	 * @see org.apache.wicket.Component#onComponentTag(org.apache.wicket.markup.ComponentTag)
	 */
    @Override
    protected void onComponentTag(final ComponentTag tag) {
        super.onComponentTag(tag);

        Object hrefValue = getDefaultModelObject();

        StringBuffer pageTrackerCode = new StringBuffer("pageTracker._trackPageView('");
        pageTrackerCode.append(getPrefix());
        pageTrackerCode.append(hrefValue);
        pageTrackerCode.append(getSuffix());
        pageTrackerCode.append("');");

        pageTrackerCode.append(tag.getAttribute("onclick") != null ? tag.getAttribute("onclick") : "");

        tag.put("onclick", pageTrackerCode.toString());
    }
}