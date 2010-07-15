package com.mysticcoders.mysticpaste.web.pages;

import com.mysticcoders.mysticpaste.model.gae.SimpleObject;
import com.mysticcoders.mysticpaste.services.PasteService;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: May 13, 2010
 * Time: 2:12:32 PM
 */
public class SimpleObjectPage extends BasePage {

    @SpringBean
    private PasteService pasteService;

    public SimpleObjectPage() {
        Form form = new Form<SimpleObject>("form", new CompoundPropertyModel<SimpleObject>(new SimpleObject())) {
            public void onSubmit() {
                SimpleObject simpleObject = getModelObject();
                Long id = pasteService.createSimpleObject(simpleObject.getContent());

                info("ID saved is:" + id);
                System.out.println("id:" + id);

                pasteService.retrieveSimpleObject(id);
            }
        };

        add(form);
        form.add(new TextArea("content"));
        form.add(new FeedbackPanel("feedback"));
    }
}
