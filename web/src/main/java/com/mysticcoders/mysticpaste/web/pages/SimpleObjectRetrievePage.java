package com.mysticcoders.mysticpaste.web.pages;

import com.mysticcoders.mysticpaste.model.gae.SimpleObject;
import com.mysticcoders.mysticpaste.services.PasteService;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Test for the Google App Engine code
 *
 * @author Andrew Lombardi <andrew@mysticcoders.com>
 */
public class SimpleObjectRetrievePage extends BasePage {
    @SpringBean
    private PasteService pasteService;

    public SimpleObjectRetrievePage(PageParameters params) {

        if(params.get("id") == null) {
            throw new RestartResponseException(SimpleObjectPage.class);
        }

        final Long id = params.get("id").toLong();

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
