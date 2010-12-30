/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Oct 15, 2009
 * Time: 12:08:28 PM
 */
package com.mysticcoders.mysticpaste.web.components.google;

import org.apache.wicket.Application;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.JavaScriptUtils;

public abstract class GoogleAnalyticsSnippet extends Panel {

    public GoogleAnalyticsSnippet(String id, IModel model) {
        super(id, model);

        setRenderBodyOnly(true);
    }

    public GoogleAnalyticsSnippet(String id) {
        this(id, null);
    }

    /**
     * Visibility in development mode generally should be absent
     *
     * method can be overwritten if there are other rules associated with this script
     *
     * @return visibility
     */
    public boolean isVisible() {
        return Application.get().getConfigurationType() != RuntimeConfigurationType.DEVELOPMENT;
    }

    protected abstract String getTracker();

    @Override
    protected void onComponentTag(final ComponentTag tag) {
        StringBuilder sb = new StringBuilder();

        
        sb.append(JavaScriptUtils.SCRIPT_OPEN_TAG);
        sb.append("var gaJsHost = ((\"https:\" == document.location.protocol) ? \"https://ssl.\" : \"http://www.\");\n");
        sb.append("document.write(unescape(\"%3Cscript src='\" + gaJsHost + \"google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E\"));\n");
        sb.append(JavaScriptUtils.SCRIPT_CLOSE_TAG);
        sb.append(JavaScriptUtils.SCRIPT_OPEN_TAG);
        sb.append("try {\n");
        sb.append("var pageTracker = _gat._getTracker(\"");
        sb.append(getTracker());
        sb.append("\");\n");
        sb.append("pageTracker._trackPageview();\n");
        sb.append("} catch(err) {}\n");

        sb.append(JavaScriptUtils.SCRIPT_CLOSE_TAG);

        getResponse().write(sb.toString());
    }

}