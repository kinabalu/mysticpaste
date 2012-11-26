package com.mysticcoders.mysticpaste.web.components.mousetrap;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Binding to mousetrap.js
 * from: http://craig.is/killing/mice
 *
 * @author Andrew Lombardi
 */
public class Mousetrap extends Behavior {
    private static final long serialVersionUID = 1L;

    private Map<KeyBinding, AbstractDefaultAjaxBehavior> behaviors = new HashMap<KeyBinding, AbstractDefaultAjaxBehavior>();

    public void renderHead(final Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Mousetrap.class, "mousetrap.min.js")));

        StringBuffer mousetrapBinds = new StringBuffer();
        for (Map.Entry<KeyBinding, AbstractDefaultAjaxBehavior> entry : behaviors.entrySet()) {
            mousetrapBinds.append("Mousetrap.bind(")
                    .append(entry.getKey())
                    .append(", function(e) { Wicket.Ajax.get({'u': '")
                    .append(entry.getValue().getCallbackUrl())
                    .append("'}) });\n");

        }
        response.render(JavaScriptHeaderItem.forScript(mousetrapBinds, null));

    }

    public void addBind(KeyBinding keyBinding, AbstractDefaultAjaxBehavior behavior) {
        behaviors.put(keyBinding, behavior);
    }


}