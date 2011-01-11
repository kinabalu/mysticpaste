package com.mysticcoders.mysticpaste.web.pages.view;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.InvalidClientException;
import com.mysticcoders.mysticpaste.services.PasteService;
import com.mysticcoders.mysticpaste.web.components.highlighter.HighlighterPanel;
import com.mysticcoders.mysticpaste.web.pages.BasePage;
import com.mysticcoders.mysticpaste.web.pages.error.PasteNotFound;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * View Paste.
 *
 * @author: Steve Forsyth
 * @author Andrew Lombardi <andrew@mysticcoders.com>
 * Date: Mar 8, 2009
 */
public class ViewPublicPage extends ViewPastePage {

    @SpringBean
    PasteService pasteService;

    public ViewPublicPage(PageParameters params) {
        super(params);
    }

    protected IModel<PasteItem> getPasteModel(String id) {
        return new PasteModel(id);
    }

    @Override
    protected boolean isPublic() {
        return true;
    }

    private class PasteModel extends LoadableDetachableModel<PasteItem> {

        String id;

        public PasteModel(String id) {
            this.id = id;
        }

        protected PasteItem load() {
            try {
                return pasteService.getItem("web", Long.parseLong(id));
            } catch (InvalidClientException e) {
                // Do nothing as the validation is not yet implemented
                e.printStackTrace();
            }
            return null;
        }
    }

}
