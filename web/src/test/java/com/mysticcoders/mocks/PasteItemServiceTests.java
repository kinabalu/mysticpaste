package com.mysticcoders.mocks;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import com.mysticcoders.mysticpaste.services.PasteService;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2006 Mystic Coders, LLC
 *
 * @author: joed
 * Date  : Mar 6, 2009
 */
public class PasteItemServiceTests extends AbstractMockTestCase {

    private PasteItemDao dao;
    private PasteService svc;

/*
    @Test
    public void testMocks() {
        dao = getPasteItemDao();
        assertNotNull(getPasteItemDao());
        svc = getPasteService();
        assertNotNull(getPasteService());
    }

    @Test
    public void testGetLatestItems() throws Exception {
        svc = getPasteService();
        assertNotNull(getPasteService());
        expect(svc.getLatestItems("CLIENT", 10, 0, false)).andReturn(getPastes(10, LanguageEnum.PASCAL, false));
        replay(svc);
        List<PasteItem> returnedList = svc.getLatestItems("CLIENT", 10, 0, false);
        verify(svc);
        assertEquals(10, returnedList.size());
    }

    @Test
    public void testGetLatestItemsWithReplies() throws Exception {
        svc = getPasteService();
        assertNotNull(getPasteService());
        expect(svc.getLatestItems("CLIENT", 5, 0, true)).andReturn(getPastes(10, LanguageEnum.PASCAL, true));
        replay(svc);
        List<PasteItem> returnedList = svc.getLatestItems("CLIENT", 5, 0, true);
        verify(svc);
        assertEquals(5, returnedList.size());
        for (PasteItem item : returnedList) {
            assertFalse(item.getChildren().isEmpty());
        }
    }

    @Test
    public void testGetPasteItem() throws Exception {
        svc = getPasteService();
        assertNotNull(svc);
        expect(svc.getItem("USER0", 0)).andReturn(getPastes(1, LanguageEnum.PHP, false).get(0));
        replay(svc);
        PasteItem paste = svc.getItem("USER0", 0);
        verify(svc);
        assertTrue(paste.getUserToken().equals("USER0"));
        assertEquals(paste.getId(), 0);

    }

    @Test
    public void testFindItemsByLanguage() throws Exception {
        svc = getPasteService();
        assertNotNull(svc);
        expect(svc.findItemsByLanguage("CLIENT", LanguageEnum.ACTIONSCRIPT.name(), 100, 0, false)).andReturn(getPastes(100, LanguageEnum.ACTIONSCRIPT, false));
        replay(svc);
        List<PasteItem> actionScriptItems = svc.findItemsByLanguage("CLIENT", LanguageEnum.ACTIONSCRIPT.name(), 100, 0, false);
        verify(svc);
        assertEquals(100, actionScriptItems.size());
    }

    public void testCreatePaste() throws Exception {
        svc = getPasteService();
        assertNotNull(svc);

        PasteItem paste = new PasteItem();
        paste.setId(1L);
        paste.setContent("TEST-DATA");
        paste.setType(LanguageEnum.PERL.name());
        paste.setUserToken("WALL");
        expect(svc.createItem("WALL-BANGER", paste)).andReturn(42l);

        replay(svc);
        long huh = svc.createItem("WALL-BANGER", paste);

        assertEquals(42l, huh);


    }

    public void testReplyItem() throws Exception {
        svc = getPasteService();
        assertNotNull(svc);

        PasteItem paste = new PasteItem();
        paste.setId(1L);
        paste.setContent("TEST-DATA-REPLY");
        paste.setType(LanguageEnum.PERL.name());
        paste.setUserToken("WALL");
        expect(svc.createReplyItem("WALL-BANGER", paste, 42)).andReturn(43l);

        replay(svc);
        long huh = svc.createReplyItem("WALL-BANGER", paste, 42);

        assertEquals(43l, huh);


    }

    public void testGetItemsForUser() throws Exception {
        svc = getPasteService();
        assertNotNull(svc);

        expect(svc.getItemsForUser("CLIENT", "USER")).andReturn(getPastes(100, LanguageEnum.ACTIONSCRIPT, false));
        replay(svc);
        List<PasteItem> actionScriptItems = svc.getItemsForUser("CLIENT", "USER");
        verify(svc);
        assertEquals(100, actionScriptItems.size());


    }

    private List<PasteItem> getPastes(Integer count, LanguageEnum lang, boolean threaded) {
        List<PasteItem> pastes = new ArrayList<PasteItem>();

        for (int i = 0; i < count; i++) {
            PasteItem item = new PasteItem();
            item.setId(i);
            item.setUserToken("USER" + i);
            item.setContent("CONTENT" + i);
            item.setType(lang.name());
            if (threaded) {
                if ((i+1) % 2 == 0) {
                    PasteItem parent = pastes.get((i-1)/2);
                    item.setParent(parent);
                    parent.addChild(item);
                } else {
                    pastes.add(item);
                }
            } else {
                pastes.add(item);                
            }
        }

        return pastes;
    }
*/

}
