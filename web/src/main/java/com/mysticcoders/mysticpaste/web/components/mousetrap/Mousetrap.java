package com.mysticcoders.mysticpaste.web.components.mousetrap;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * Created with IntelliJ IDEA.
 * User: kinabalu
 * Date: 11/25/12
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class Mousetrap extends Behavior {
    private static final long serialVersionUID = 1L;

    public void renderHead(final Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Mousetrap.class, "mousetrap.min.js")));
    }
}
