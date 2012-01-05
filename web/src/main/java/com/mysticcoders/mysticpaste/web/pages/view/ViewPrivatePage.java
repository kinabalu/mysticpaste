package com.mysticcoders.mysticpaste.web.pages.view;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.InvalidClientException;
import com.mysticcoders.mysticpaste.services.PasteService;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View Paste.
 *
 * @author: Steve Forsyth
 * Date: Mar 8, 2009
 */
public class ViewPrivatePage extends ViewPastePage {

    @SpringBean
    PasteService pasteService;
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ViewPrivatePage(PageParameters params) {
        super(params);
        
        logger.info("Client ["+ getClientIpAddress() +"] viewing paste with ID["+getPasteItem().getId()+"]");
    }

    protected IModel<PasteItem> getPasteModel(String id) {
        return new PasteModel(id);
    }


    @Override
    protected boolean isPublic() {
        return false;
    }

    private class PasteModel extends LoadableDetachableModel<PasteItem> {

        String id;

        public PasteModel(String id) {
            this.id = id;
        }

        protected PasteItem load() {
            try {
                return pasteService.findPrivateItem("web", id);
            } catch (InvalidClientException e) {
                // Do nothing as the validation is not yet implemented
                e.printStackTrace();
            }
            return null;
        }
    }

}