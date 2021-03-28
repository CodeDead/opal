package com.codedead.opal.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public final class SettingsWindowController {

    private static final Logger logger = LogManager.getLogger(SettingsWindowController.class);

    @FXML
    private CheckBox chbAutoUpdate;
    @FXML
    private ComboBox<String> cboLanguage;

    private SettingsController settingsController;
    private ResourceBundle translationBundle;
    private Properties properties;

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

        properties = settingsController.getProperties();
        final String languageTag = properties.getProperty("locale", "en-US");

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Attempting to load the ResourceBundle for locale %s", languageTag));
        }

        final Locale locale = Locale.forLanguageTag(languageTag);
        translationBundle = ResourceBundle.getBundle("translations.OpalApplication", locale);

        loadSettings();
    }

    /**
     * Method that is invoked to initialize the FXML window
     */
    @FXML
    private void initialize() {

    }

    /**
     * Load all the settings into the UI
     */
    private void loadSettings() {
        logger.info("Attempting to load all settings");
        final boolean autoUpdate = Boolean.parseBoolean(properties.getProperty("autoUpdate", "true"));
        final String locale = properties.getProperty("locale", "en-US");

        chbAutoUpdate.setSelected(autoUpdate);
        switch (locale.toLowerCase()) {
            case "en-us" -> cboLanguage.getSelectionModel().select(0);
            case "fr-fr" -> cboLanguage.getSelectionModel().select(1);
            case "nl-nl" -> cboLanguage.getSelectionModel().select(2);
        }
    }
}
