package com.codedead.opal.controller;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThemeControllerTest {

    @Test
    void mapsAllAvailableThemesToTheirComboBoxIndexes() {
        assertEquals(0, ThemeController.getThemeIndex("CupertinoDark"));
        assertEquals(1, ThemeController.getThemeIndex("CupertinoLight"));
        assertEquals(2, ThemeController.getThemeIndex("Dracula"));
        assertEquals(3, ThemeController.getThemeIndex("Light"));
        assertEquals(4, ThemeController.getThemeIndex("Dark"));
        assertEquals(5, ThemeController.getThemeIndex("NordLight"));
        assertEquals(6, ThemeController.getThemeIndex("NordDark"));
        assertEquals(7, ThemeController.getThemeIndex("Modena"));
        assertEquals(8, ThemeController.getThemeIndex("Caspian"));
    }

    @Test
    void themeLookupIsIndependentOfTheDefaultLocale() {
        final Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals(1, ThemeController.getThemeIndex("CupertinoLight"));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}
