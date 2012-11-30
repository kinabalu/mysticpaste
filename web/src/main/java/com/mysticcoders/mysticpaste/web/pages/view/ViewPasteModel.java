package com.mysticcoders.mysticpaste.web.pages.view;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.PasteService;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * Created with IntelliJ IDEA.
 * User: kinabalu
 * Date: 11/30/12
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ViewPasteModel extends LoadableDetachableModel<PasteItem> {

    String id;
    PasteService pasteService;

    public ViewPasteModel(String id, PasteService pasteService) {
        this.id = id;
        this.pasteService = pasteService;
    }

    protected PasteItem load() {
        return pasteService.getItem("web", id);
    }
}