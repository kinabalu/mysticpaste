/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Oct 9, 2009
 * Time: 7:56:35 AM
 */
package com.mysticcoders.mysticpaste.web.components.swf;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.util.string.JavascriptUtils;

public class SwfBehavior extends AbstractBehavior {

    private static final long serialVersionUID = 1L;

    protected static final CompressedResourceReference SWFOBJECT_JS = new CompressedResourceReference(
            SwfBehavior.class, "swfobject.js");

    private String swfPath;

    public SwfBehavior(String swfPath) {
        this.swfPath = swfPath;
    }

    private String swfId;

    protected String getSwfId() { return swfId; }

    protected int getWidth() { return 500; }

    protected int getHeight() { return 400; }


    @Override
    public void onComponentTag(final Component component, final ComponentTag tag) {
        if (isEnabled(component)) {
            swfId = tag.getId();
        }
    }

    @Override
	public void onRendered(Component component) {
        StringBuilder sb = new StringBuilder();

        sb.append(JavascriptUtils.SCRIPT_OPEN_TAG);
        sb.append("var flashvars = {}; var params = { menu: \"false\" }; var attributes = {};");
        sb.append("swfobject.embedSWF(");
        sb.append("\"").append(swfPath).append("\",");
        sb.append("\"").append(getSwfId()).append("\",");
        sb.append("\"").append(getWidth()).append("\",");
        sb.append("\"").append(getHeight()).append("\",");
        sb.append("\"8.0.0\",");
        sb.append("false,");
        sb.append("flashvars, params, attributes);");

        sb.append(JavascriptUtils.SCRIPT_CLOSE_TAG);

        component.getResponse().write(sb.toString());        
	}

    @Override
    public void renderHead(IHeaderResponse response) {
        try {
            super.renderHead(response);
            response.renderJavascriptReference(SWFOBJECT_JS);
        } catch (RuntimeException exc) {
            throw exc;
        } catch (Exception exc) {
            throw new RuntimeException("wrap: " + exc.getMessage(), exc);
        }
    }
}