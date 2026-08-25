package com.mysticcoders.mysticpaste.model;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasteItemTest {

    @Test
    public void previewContentIsCappedAtFiveLines() {
        PasteItem item = new PasteItem();
        item.setContent("1\n2\n3\n4\n5\n6\n7");

        assertEquals("1\n2\n3\n4\n5\n", item.getPreviewContent());
    }

    @Test
    public void previewContentOfAnEmptyPasteIsEmpty() {
        PasteItem item = new PasteItem();

        assertEquals("", item.getPreviewContent());
        assertEquals(0, item.getContentLineCount());
    }

    @Test
    public void contentLineCountCountsEveryLine() {
        PasteItem item = new PasteItem();
        item.setContent("one\ntwo\nthree");

        assertEquals(3, item.getContentLineCount());
    }

    @Test
    public void diffPastesMarksTheChangedLines() {
        Object[] result = PasteItem.diffPastes("alpha\nbravo\ncharlie", "alpha\nbravo-changed\ncharlie");

        @SuppressWarnings("unchecked")
        List<Integer> changedLines = (List<Integer>) result[0];
        String diffText = (String) result[1];

        assertFalse(changedLines.isEmpty());
        assertTrue(diffText.contains("- bravo"));
        assertTrue(diffText.contains("+ bravo-changed"));
    }

    @Test
    public void elapsedTimeForAFreshPaste() {
        PasteItem item = new PasteItem();
        item.setTimestamp(new Date());

        assertEquals("Posted less than a minute ago", PasteItem.getElapsedTimeSincePost(item));
    }
}
