package com.mysticcoders.mysticpaste.web.pages.plugin;

import com.mysticcoders.mysticpaste.web.pages.BasePage;

/**
 * Plug-ins page for display and instructions for installing application plug-ins.
 *
 * @author Steve Forsyth
 * Date: Mar 29, 2009
 */
public class PluginPage extends BasePage {

    public PluginPage() {
        super(PluginPage.class);
    }

    protected String getTitle() {
        return "Plugins - Mystic Paste";
    }
}