package com.codedead.opal.controller;

import com.codedead.opal.utils.FxUtils;
import com.codedead.opal.utils.SharedVariables;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

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
    @FXML
    private ComboBox<String> cboLogLevel;

    private SettingsController settingsController;
    private ResourceBundle translationBundle;
    private Properties properties;

    /**
     * Initialize a new SettingsWindowController
     */
    public SettingsWindowController() {
        logger.info("Initializing new SettingsWindowController object");
    }

    @FXML
    private void initialize() {
        logger.info("Initializing SettingsWindow");
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
        final String logLevel = properties.getProperty("loglevel", "INFO");

        chbAutoUpdate.setSelected(autoUpdate);
        switch (locale.toLowerCase()) {
            case "fr-fr" -> cboLanguage.getSelectionModel().select(1);
            case "nl-nl" -> cboLanguage.getSelectionModel().select(2);
            default -> cboLanguage.getSelectionModel().select(0);
        }

        switch (logLevel) {
            case "OFF" -> cboLogLevel.getSelectionModel().select(0);
            case "FATAL" -> cboLogLevel.getSelectionModel().select(1);
            case "ERROR" -> cboLogLevel.getSelectionModel().select(2);
            case "WARN" -> cboLogLevel.getSelectionModel().select(3);
            case "DEBUG" -> cboLogLevel.getSelectionModel().select(5);
            case "TRACE" -> cboLogLevel.getSelectionModel().select(6);
            case "ALL" -> cboLogLevel.getSelectionModel().select(7);
            default -> cboLogLevel.getSelectionModel().select(4);
        }
    }

    /**
     * Reset all settings to their default values after user confirmation
     */
    @FXML
    private void resetSettingsAction() {
        logger.info("Attempting to reset all settings");
        if (FxUtils.showConfirmationAlert(translationBundle.getString("ConfirmReset"), getClass().getResourceAsStream(SharedVariables.ICON_URL))) {
            showAlertIfLanguageMismatch("en-US");

            try {
                settingsController.createDefaultProperties();
                settingsController.setProperties(settingsController.readPropertiesFile());

                setSettingsController(settingsController);
            } catch (final IOException ex) {
                logger.error("Unable to reset all settings", ex);
                FxUtils.showErrorAlert(translationBundle.getString("ResetSettingsError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            }
        }
    }

    /**
     * Method that is called when all settings should be saved
     */
    @FXML
    private void saveSettingsAction() {
        logger.info("Attempting to save all settings");

        properties.setProperty("autoUpdate", Boolean.toString(chbAutoUpdate.isSelected()));

        showAlertIfLanguageMismatch(properties.getProperty("locale", "en-US"));

        switch (cboLanguage.getSelectionModel().getSelectedIndex()) {
            case 1 -> properties.setProperty("locale", "fr-FR");
            case 2 -> properties.setProperty("locale", "nl-NL");
            default -> properties.setProperty("locale", "en-US");
        }

        properties.setProperty("loglevel", cboLogLevel.getValue());

        final Level level = switch (cboLogLevel.getValue()) {
            case "OFF" -> Level.OFF;
            case "FATAL" -> Level.FATAL;
            case "ERROR" -> Level.ERROR;
            case "WARN" -> Level.WARN;
            case "DEBUG" -> Level.DEBUG;
            case "TRACE" -> Level.TRACE;
            case "ALL" -> Level.ALL;
            default -> Level.INFO;
        };

        Configurator.setAllLevels(LogManager.getRootLogger().getName(), level);
        settingsController.setProperties(properties);
        try {
            settingsController.saveProperties();
        } catch (final IOException ex) {
            logger.error("Unable to save all settings", ex);
            FxUtils.showErrorAlert(translationBundle.getString("SaveSettingsError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }

        setSettingsController(settingsController);
    }

    /**
     * Show an information alert if a restart is required
     *
     * @param languageToMatch The language that needs to be matched to the combobox
     */
    private void showAlertIfLanguageMismatch(final String languageToMatch) {
        String newLanguage;
        switch (cboLanguage.getSelectionModel().getSelectedIndex()) {
            case 1 -> newLanguage = "fr-FR";
            case 2 -> newLanguage = "nl-NL";
            default -> newLanguage = "en-US";
        }

        if (!languageToMatch.equals(newLanguage)) {
            FxUtils.showInformationAlert(translationBundle.getString("RestartRequired"), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }
}
