/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Oct 9, 2009
 * Time: 9:37:56 AM
 */
package com.mysticcoders.mysticpaste.web.pages.view;

import com.mysticcoders.mysticpaste.web.components.swf.SwfBehavior;
import com.mysticcoders.mysticpaste.web.pages.BasePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;

public class SwfExample extends BasePage {
    private static final long serialVersionUID = 1L;

    public SwfExample() {
        add(new Label("youtube").add(new SwfBehavior("http://www.youtube.com/v/2LTLEVC-sfQ&hl=en&fs=1&")));
    }

}