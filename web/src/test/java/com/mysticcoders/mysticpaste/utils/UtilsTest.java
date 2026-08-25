package com.mysticcoders.mysticpaste.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilsTest {

    @Test
    public void generatedTokenHasRequestedLength() {
        assertEquals(10, TokenGenerator.generateToken(10).length());
        assertEquals(1, TokenGenerator.generateToken(1).length());
    }

    @Test
    public void generatedTokenIsAlphanumeric() {
        assertTrue(TokenGenerator.generateToken(64).matches("[A-Za-z0-9]+"));
    }

    @Test
    public void spamKeywordsAreDetectedCaseInsensitively() {
        assertTrue(StringUtils.hasSpamKeywords("buy cheap VIAGRA now"));
        assertTrue(StringUtils.hasSpamKeywords("online Casino"));
    }

    @Test
    public void ordinaryContentIsNotFlaggedAsSpam() {
        assertFalse(StringUtils.hasSpamKeywords("public static void main(String[] args) {}"));
    }
}
