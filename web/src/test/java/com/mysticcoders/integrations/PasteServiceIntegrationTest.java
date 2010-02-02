package com.mysticcoders.integrations;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.model.Client;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import com.mysticcoders.mysticpaste.services.InvalidClientException;
import com.mysticcoders.mysticpaste.services.PasteService;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.unitils.hibernate.HibernateUnitils;
import org.unitils.spring.annotation.SpringBeanByType;

/**
 * Copyright 2009 Mystic Coders, LLC
 *
 * @author: joed
 * Date  : Mar 7, 2009
 */
public class PasteServiceIntegrationTest extends AbstractIntegrationTest {

    @SpringBeanByType
    private PasteService svc;

    @SpringBeanByType
    private PasteItemDao dao;

    // We start with verifying that the DB is correctly setup.
    @Test
    public void testMapping() {
        HibernateUnitils.assertMappingWithDatabaseConsistent();
    }

    @Test
    public void testGetCurrentCount() {

        PasteItem paste = new PasteItem();
        paste.setContent("TEST-DATA");
        paste.setType("text");
        paste.setUserToken("USER");

        try {

            svc.createItem("CLIENT", paste);
            assertTrue(svc.getItemsForUser("CLIENT", "USER").size() == 1);
        } catch (InvalidClientException e) {
            e.printStackTrace();  //TODO
        }
    }

    @Test
    public void testCreateAndRetrieve() throws InvalidClientException {
        PasteItem paste = new PasteItem();
        paste.setContent("TEST-DATA");
        paste.setType("text");
        paste.setUserToken("USER");

        Long id = svc.createItem("CLIENT", paste);
        System.out.println(id);
        PasteItem item2 = svc.getItem("CLIENT", id);

        assertEquals(item2.getContent(), paste.getContent());
//        assertEquals(item2.getClientToken(), paste.getClientToken());

    }

    @Test
    public void testCreatePrivateItem() throws InvalidClientException {
        PasteItem paste = new PasteItem();
        paste.setContent("TEST-DATA");
        paste.setType("text");
        paste.setUserToken("USER");
        paste.setPrivate(true);

        Long id = svc.createItem("CLIENT", paste);
        System.out.println(id);
        PasteItem item2 = svc.getItem("CLIENT", id);

        assertNotNull(item2.getPrivateToken());
        assertEquals(10, item2.getPrivateToken().length());
        System.out.println(item2.getPrivateToken());
    }


}
