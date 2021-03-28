package com.codedead.opal.controller;

import com.codedead.opal.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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
     * Load all the settings into the UI
     */
    private void loadSettings() {
        logger.info("Attempting to load all settings");
        final boolean autoUpdate = Boolean.parseBoolean(properties.getProperty("autoUpdate", "true"));
        final String locale = properties.getProperty("locale", "en-US");

        chbAutoUpdate.setSelected(autoUpdate);
        switch (locale.toLowerCase()) {
            case "fr-fr" -> cboLanguage.getSelectionModel().select(1);
            case "nl-nl" -> cboLanguage.getSelectionModel().select(2);
            default -> cboLanguage.getSelectionModel().select(0);
        }
    }

    /**
     * Reset all settings to their default values after user confirmation
     */
    @FXML
    private void resetSettingsAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, translationBundle.getString("ConfirmReset"), ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                settingsController.createDefaultProperties();
                setSettingsController(settingsController);
            } catch (final IOException ex) {
                logger.error("Unable to reset all settings", ex);
                FxUtils.showErrorAlert(translationBundle.getString("ResetSettingsError"), ex.getMessage(), getClass().getResourceAsStream("/images/opal.png"));
            }
        }
    }

    /**
     * Method that is called when all settings should be saved
     */
    @FXML
    private void saveSettingsAction() {
        properties.setProperty("autoUpdate", Boolean.toString(chbAutoUpdate.isSelected()));
        switch (cboLanguage.getSelectionModel().getSelectedIndex()) {
            case 1 -> properties.setProperty("locale", "fr-FR");
            case 2 -> properties.setProperty("locale", "nl-NL");
            default -> properties.setProperty("locale", "en-US");
        }

        settingsController.setProperties(properties);
        try {
            settingsController.saveProperties();
        } catch (final IOException ex) {
            logger.error("Unable to save all settings", ex);
            FxUtils.showErrorAlert(translationBundle.getString("SaveSettingsError"), ex.getMessage(), getClass().getResourceAsStream("/images/opal.png"));
        }

        setSettingsController(settingsController);
    }
}
