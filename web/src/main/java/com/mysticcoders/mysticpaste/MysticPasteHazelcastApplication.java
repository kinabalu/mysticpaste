/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Oct 28, 2009
 * Time: 9:56:49 AM
 */
package com.mysticcoders.mysticpaste;

import com.hazelcast.core.IMap;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.EntryEvent;
import com.mysticcoders.mysticpaste.model.PasteItem;

import java.util.Set;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MysticPasteHazelcastApplication extends MysticPasteApplication {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String namespace = "default";

    private List<Long> indexList = new CopyOnWriteArrayList<Long>();

    private AtomicLong lastIdentifier = new AtomicLong(0L);
    
    public List<Long> getIndexList() {
        return indexList;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void init() {
        super.init();

        IMap map = Hazelcast.getMap(namespace);

        map.addEntryListener(new EntryListener() {

            public void entryAdded(EntryEvent entryEvent) {
                if (entryEvent.getValue() instanceof PasteItem) {
                    PasteItem pasteItem = (PasteItem) entryEvent.getValue();

                    if (!pasteItem.isPrivate()) {
                        indexList.add(pasteItem.getId());
                    }
                }
            }

            public void entryRemoved(EntryEvent entryEvent) {}

            public void entryUpdated(EntryEvent entryEvent) {}

            public void entryEvicted(EntryEvent entryEvent) {}
        }, true);


        List<Long> initialIndexList = new ArrayList<Long>();

        if (lastIdentifier.longValue() == 0L && indexList.size() == 0) {
            // rebuild our index list and lastIdentifier
            Set keys = map.keySet();
            for (Object key : keys) {
                if (key instanceof Long) {
                    Object keyValue = map.get(key);
                    if (keyValue instanceof PasteItem) {
                        PasteItem pasteItem = (PasteItem) keyValue;
                        if (lastIdentifier.longValue() < pasteItem.getId()) {
                            lastIdentifier = new AtomicLong(pasteItem.getId());
                        }
                        initialIndexList.add(pasteItem.getId());
                    }
                }
            }

            Collections.sort(initialIndexList);
            indexList = new CopyOnWriteArrayList<Long>(initialIndexList);

            logger.info("Last identifier value is: " + lastIdentifier);
            logger.info("Identifier List is: " + indexList);
        }
    }

    public long nextId() {
        return lastIdentifier.longValue() + Hazelcast.getIdGenerator("paste-item-ids").newId();
    }
}