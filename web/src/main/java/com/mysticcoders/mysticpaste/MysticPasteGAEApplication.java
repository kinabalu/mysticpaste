package com.mysticcoders.mysticpaste;

import com.mysticcoders.mysticpaste.web.pages.SimpleObjectPage;
import com.mysticcoders.mysticpaste.web.pages.SimpleObjectRetrievePage;

/**
 * Custom Wicket application class for the GAE port
 *
 * @author Andrew Lombardi <andrew@mysticcoders.com>
 */
public class MysticPasteGAEApplication extends MysticPasteApplication {

    protected void init() {
        super.init();
        mountPage("/simple", SimpleObjectPage.class);
        mountPage("/simple.get", SimpleObjectRetrievePage.class);
    }

    /**
     * Google App Engine doesn't like DiskPageStore at all
     *
     * @return
     */
/*
    @Override
    public HttpSessionStore newSessionStore() {
        return new HttpSessionStore();
    }
*/
}
