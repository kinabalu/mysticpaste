package com.mysticcoders.wicket.mousetrap;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Binding for mousetrap.js
 *
 * from: http://craig.is/killing/mice
 *
 * @author Andrew Lombardi
 */
public class Mousetrap extends Behavior {
    private static final long serialVersionUID = 1L;

    private final Map<KeyBinding, CharSequence> bindings = new LinkedHashMap<>();
    private final Map<KeyBinding, CharSequence> globalBindings = new LinkedHashMap<>();
    private final Map<KeyBinding, CharSequence> defaultBindings = new LinkedHashMap<>();
    private final Map<KeyBinding, CharSequence> defaultGlobalBindings = new LinkedHashMap<>();

    private static final int BIND = 0;
    private static final int BIND_GLOBAL = 1;
    private static final int BIND_DEFAULT = 2;
    private static final int BIND_DEFAULT_GLOBAL = 3;

    /**
     * Convenience method for returning a mousetrap binding call
     *
     * @param type are we global, a default, or a regular bind
     * @param bindings list of bindings
     * @return Mousetrap bindings
     */
    private CharSequence getMousetrapBinds(int type, Map<KeyBinding, CharSequence> bindings) {
        StringBuilder mousetrapBinds = new StringBuilder();
        for (Map.Entry<KeyBinding, CharSequence> entry : bindings.entrySet()) {
            mousetrapBinds.append("Mousetrap.")
                    .append(type == BIND_GLOBAL || type == BIND_DEFAULT_GLOBAL ? "bindGlobal" : "bind")
                    .append("(")
                    .append(entry.getKey())
                    .append(", function(e) { ");
            if (type == BIND_DEFAULT || type == BIND_DEFAULT_GLOBAL) {
                mousetrapBinds.append("if (e.preventDefault) {e.preventDefault();} else {e.returnValue = false;}");
            }
            mousetrapBinds.append(entry.getValue())
                    .append(" }");
            if (entry.getKey().getEventType() != null) {
                mousetrapBinds.append(", '")
                        .append(entry.getKey().getEventType())
                        .append("'");
            }
            mousetrapBinds.append(");\n");
        }

        return mousetrapBinds;
    }

    /**
     * Render to the web response whatever the component wants to contribute to the head section.
     *
     * @param component component this behavior is attached to
     * @param response Response object
     */
    @Override
    public void renderHead(final Component component, IHeaderResponse response) {
        super.renderHead(component, response);

        if (!bindings.isEmpty()) {
            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Mousetrap.class, "mousetrap.min.js")));
            response.render(OnDomReadyHeaderItem.forScript(getMousetrapBinds(BIND, bindings)));
        }

        if (!globalBindings.isEmpty()) {
            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Mousetrap.class, "mousetrap-global.min.js")));
            response.render(OnDomReadyHeaderItem.forScript(getMousetrapBinds(BIND_GLOBAL, globalBindings)));
        }

        if (!defaultBindings.isEmpty()) {
            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Mousetrap.class, "mousetrap.min.js")));
            response.render(OnDomReadyHeaderItem.forScript(getMousetrapBinds(BIND_DEFAULT, defaultBindings)));
        }

        if (!defaultGlobalBindings.isEmpty()) {
            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Mousetrap.class, "mousetrap-global.min.js")));
            response.render(OnDomReadyHeaderItem.forScript(getMousetrapBinds(BIND_DEFAULT_GLOBAL, defaultGlobalBindings)));
        }
    }

    /**
     * Adds a key binding to Mousetrap for given behavior
     *
     * @param keyBinding keys to bind
     * @param behavior behavior to execute upon binding being fired
     */
    public void addBind(KeyBinding keyBinding, AbstractDefaultAjaxBehavior behavior) {
        bindings.put(keyBinding, behavior.getCallbackScript());
    }

    /**
     * Adds a key binding to Mousetrap which executes a raw JavaScript snippet
     *
     * @param keyBinding keys to bind
     * @param javaScript JavaScript to execute upon binding being fired
     */
    public void addBindJs(KeyBinding keyBinding, CharSequence javaScript) {
        bindings.put(keyBinding, javaScript);
    }

    /**
     * Adds a global key binding to Mousetrap for given behavior
     *
     * - this will fire wherever your focus is, including text fields, any form element
     *
     * @param keyBinding keys to bind
     * @param behavior behavior to execute upon binding being fired
     */
    public void addGlobalBind(KeyBinding keyBinding, AbstractDefaultAjaxBehavior behavior) {
        globalBindings.put(keyBinding, behavior.getCallbackScript());
    }

    /**
     * Adds a global key binding to Mousetrap which executes a raw JavaScript snippet
     *
     * @param keyBinding keys to bind
     * @param javaScript JavaScript to execute upon binding being fired
     */
    public void addGlobalBindJs(KeyBinding keyBinding, CharSequence javaScript) {
        globalBindings.put(keyBinding, javaScript);
    }

    /**
     * Adds a default key binding to Mousetrap for given behavior
     *
     * - this will fire and override any default in the browser
     *
     * @param keyBinding keys to bind
     * @param behavior behavior to execute upon binding being fired
     */
    public void addDefaultBind(KeyBinding keyBinding, AbstractDefaultAjaxBehavior behavior) {
        defaultBindings.put(keyBinding, behavior.getCallbackScript());
    }

    /**
     * Adds a default key binding to Mousetrap which executes a raw JavaScript snippet
     *
     * @param keyBinding keys to bind
     * @param javaScript JavaScript to execute upon binding being fired
     */
    public void addDefaultBindJs(KeyBinding keyBinding, CharSequence javaScript) {
        defaultBindings.put(keyBinding, javaScript);
    }

    /**
     * Adds a default global key binding to Mousetrap for given behavior
     *
     * - this will fire wherever your focus is, including text fields, any form element
     * - and it will fire and override any default in the browser
     *
     * @param keyBinding keys to bind
     * @param behavior behavior to execute upon binding being fired
     */
    public void addDefaultGlobalBind(KeyBinding keyBinding, AbstractDefaultAjaxBehavior behavior) {
        defaultGlobalBindings.put(keyBinding, behavior.getCallbackScript());
    }

    /**
     * Adds a default global key binding to Mousetrap which executes a raw JavaScript snippet
     *
     * @param keyBinding keys to bind
     * @param javaScript JavaScript to execute upon binding being fired
     */
    public void addDefaultGlobalBindJs(KeyBinding keyBinding, CharSequence javaScript) {
        defaultGlobalBindings.put(keyBinding, javaScript);
    }

}
