package com.codedead.opal.controller;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageControllerTest {

    @Test
    void mapsJapaneseLanguageTagsToTheSameIndex() {
        assertEquals(5, LanguageController.getLanguageIndexFromLocale("ja-JP"));
        assertEquals(5, LanguageController.getLanguageIndexFromLocale("jp-JP"));
    }

    @Test
    void returnsStandardJapaneseTagFromIndex() {
        assertEquals("ja-JP", LanguageController.getLocaleFromLanguageIndex(5));
    }

    @Test
    void migratesLegacyJapaneseTagToStandardLocale() {
        assertEquals(Locale.forLanguageTag("ja-JP"), LanguageController.getLocaleFromLanguageTag("ja-JP"));
        assertEquals(Locale.forLanguageTag("ja-JP"), LanguageController.getLocaleFromLanguageTag("jp-JP"));
    }
}
