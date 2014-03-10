package com.mysticcoders.mysticpaste.web.components;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.model.Model;

/**
 * Created with IntelliJ IDEA.
 * User: kinabalu
 * Date: 12/4/12
 * Time: 5:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class HelpModal extends Modal {

    public HelpModal(final String markupId, String headerText) {
        super(markupId);

        setFooterVisible(false);
        setFadeIn(true);
//        addCloseButton();
//        setUseCloseHandler(true);
        header(Model.of(headerText));
    }

}
