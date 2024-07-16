package com.codedead.opal.controller;

import static com.codedead.opal.utils.SharedVariables.DEFAULT_LOCALE;

public final class LanguageController {

    /**
     * Initialize a new LanguageController
     */
    private LanguageController() {
        // Default constructor
    }

    /**
     * Get the language index from the locale
     *
     * @param locale The locale
     * @return The language index
     */
    public static int getLanguageIndexFromLocale(final String locale) {
        return switch (locale.toLowerCase()) {
            case "de-de" -> 1;
            case "es-es" -> 2;
            case "fr-fr" -> 3;
            case "hi" -> 4;
            case "jp-jp" -> 5;
            case "nl-nl" -> 6;
            case "ru-ru" -> 7;
            case "tr-tr" -> 8;
            case "zh-cn" -> 9;
            default -> 0;
        };
    }

    /**
     * Get the locale from the language index
     *
     * @param index The language index
     * @return The locale
     */
    public static String getLocaleFromLanguageIndex(final int index) {
        return switch (index) {
            case 1 -> "de-DE";
            case 2 -> "es-ES";
            case 3 -> "fr-FR";
            case 4 -> "hi";
            case 5 -> "jp-JP";
            case 6 -> "nl-NL";
            case 7 -> "ru-RU";
            case 8 -> "tr-TR";
            case 9 -> "zh-CN";
            default -> DEFAULT_LOCALE;
        };
    }
}
