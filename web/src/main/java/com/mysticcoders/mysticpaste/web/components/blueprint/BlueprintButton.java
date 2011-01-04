package com.mysticcoders.mysticpaste.web.components.blueprint;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: 1/3/11
 * Time: 11:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlueprintButton extends Panel {


    public BlueprintButton(String id, IModel<String> model) {
        super(id, model);
    }

    public BlueprintButton(String id, IModel<String> model, ResourceReference image) {
        super(id, model);
    }
}
