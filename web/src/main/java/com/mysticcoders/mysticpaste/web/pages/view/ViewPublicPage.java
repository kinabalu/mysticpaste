package com.mysticcoders.mysticpaste.web.pages.view;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.PasteService;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ViewPublicPage(PageParameters params) {
        super(params);

        logger.info("Client ["+ getClientIpAddress() +"] viewing paste with ID["+getPasteItem().getItemId()+"]");
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
            return pasteService.getItem("web", id);
        }
    }

}
