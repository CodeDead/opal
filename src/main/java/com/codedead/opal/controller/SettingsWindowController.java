package com.codedead.opal.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public final class SettingsWindowController {

    private static final Logger logger = LogManager.getLogger(SettingsWindowController.class);

    private SettingsController settingsController;
    private ResourceBundle translationBundle;

    /**
     * Initialize a new SettingsWindowController
     */
    public SettingsWindowController() {
        logger.info("Initializing new SettingsWindowController object");
    }

    /**
     * Get the {@link com.codedead.opal.controller.SettingsController} object
     *
     * @return The {@link com.codedead.opal.controller.SettingsController} object
     */
    public final SettingsController getSettingsController() {
        return settingsController;
    }

    /**
     * Set the {@link com.codedead.opal.controller.SettingsController} object
     *
     * @param settingsController The {@link com.codedead.opal.controller.SettingsController} object
     */
    public final void setSettingsController(final SettingsController settingsController) {
        if (settingsController == null)
            throw new NullPointerException("SettingsController cannot be null!");

        this.settingsController = settingsController;

        final Properties properties = settingsController.getProperties();
        final String languageTag = properties.getProperty("locale", "en-US");

        logger.info(String.format("Attempting to load the ResourceBundle for locale %s", languageTag));
        final Locale locale = Locale.forLanguageTag(languageTag);
        translationBundle = ResourceBundle.getBundle("translations.OpalApplication", locale);
    }
}
