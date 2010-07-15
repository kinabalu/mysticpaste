package com.mysticcoders.mysticpaste;

import com.mysticcoders.mysticpaste.MysticPasteApplication;
import com.mysticcoders.mysticpaste.web.pages.SimpleObjectPage;
import com.mysticcoders.mysticpaste.web.pages.SimpleObjectRetrievePage;
import org.apache.wicket.protocol.http.HttpSessionStore;

/**
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: May 13, 2010
 * Time: 2:11:10 PM
 */
public class MysticPasteGAEApplication extends MysticPasteApplication {

    protected void init() {
        super.init();
        mountBookmarkablePage("/simple", SimpleObjectPage.class);
        mountBookmarkablePage("/simple.get", SimpleObjectRetrievePage.class);
    }

    /**
     * Google App Engine doesn't like DiskPageStore at all
     *
     * @return
     */
    @Override
    public HttpSessionStore newSessionStore() {
        return new HttpSessionStore(this);
    }
}
