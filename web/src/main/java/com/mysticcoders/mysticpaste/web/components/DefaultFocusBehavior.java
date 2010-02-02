/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Oct 28, 2009
 * Time: 10:54:23 AM
 */
package com.mysticcoders.mysticpaste.web.components;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponent;

public class DefaultFocusBehavior extends AbstractBehavior {
    private static final long serialVersionUID = 1L;

    private Component component;

    @Override
    public void bind(Component component) {
        if (!(component instanceof FormComponent)) {
            throw new IllegalArgumentException("DefaultFocusBehavior: component must be instanceof FormComponent");
        }
        this.component = component;
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse iHeaderResponse) {
        super.renderHead(iHeaderResponse);
        iHeaderResponse.renderOnLoadJavascript("document.getElementById('"
                + component.getMarkupId() + "').focus();");
    }
}