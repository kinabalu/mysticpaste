package com.mysticcoders.mysticpaste.web.pages;

import com.mysticcoders.mysticpaste.model.gae.SimpleObject;
import com.mysticcoders.mysticpaste.services.PasteService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: May 13, 2010
 * Time: 2:12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleObjectRetrievePage extends BasePage {
    @SpringBean
    private PasteService pasteService;

    public SimpleObjectRetrievePage(PageParameters params) {

        if(params.getAsLong("id") == null) {
            throw new RestartResponseException(SimpleObjectPage.class);
        }

        final Long id = params.getAsLong("id");

        AbstractReadOnlyModel<String> myModel = new AbstractReadOnlyModel<String>() {
            public String getObject() {
                SimpleObject simpleObject =
                        pasteService.retrieveSimpleObject(id);
                return simpleObject.getContent();
            }
        };

        add(new Label("content", myModel));
    }
}
