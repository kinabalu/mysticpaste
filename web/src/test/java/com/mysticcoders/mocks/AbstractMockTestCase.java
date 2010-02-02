package com.mysticcoders.mocks;

import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import com.mysticcoders.mysticpaste.services.PasteService;
import static org.easymock.EasyMock.createMock;
import org.junit.Before;

/**
 * Copyright 2006 Mystic Coders, LLC
 *
 * @author: joed
 * Date  : Mar 6, 2009
 */
public abstract class AbstractMockTestCase {

    private PasteItemDao pasteItemDao;

    private PasteService pasteService;

    @Before
    public void setUp() {

        pasteItemDao = createMock(PasteItemDao.class);
        pasteService = createMock(PasteService.class);

    }

    public PasteItemDao getPasteItemDao() {
        return pasteItemDao;
    }

    public PasteService getPasteService() {
        return pasteService;
    }
}
