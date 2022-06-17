package com.codedead.opal.controller;

import com.codedead.opal.domain.NumberTextField;
import com.codedead.opal.utils.FxUtils;
import com.codedead.opal.utils.SharedVariables;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.concurrent.TimeUnit;

import static com.codedead.opal.utils.SharedVariables.DEFAULT_LOCALE;

public final class SettingsWindowController {

    @FXML
    private CheckBox chbTimerApplicationShutdown;
    @FXML
    private ComboBox<String> cboDelayType;
    @FXML
    private NumberTextField numDelay;
    @FXML
    private CheckBox chbAutoUpdate;
    @FXML
    private ComboBox<String> cboLanguage;
    @FXML
    private ComboBox<String> cboLogLevel;

    private SettingsController settingsController;
    private ResourceBundle translationBundle;
    private Properties properties;

    private final Logger logger;

    /**
     * Initialize a new SettingsWindowController
     */
    public SettingsWindowController() {
        logger = LogManager.getLogger(SettingsWindowController.class);
        logger.info("Initializing new SettingsWindowController object");
    }

    /**
     * Method that is invoked to initialize the FXML window
     */
    @FXML
    private void initialize() {
        logger.info("Initializing SettingsWindow");
    }

    /**
     * Set the {@link SettingsController} object
     *
     * @param settingsController The {@link SettingsController} object
     */
    public void setSettingsController(final SettingsController settingsController) {
        if (settingsController == null)
            throw new NullPointerException("SettingsController cannot be null!");

        this.settingsController = settingsController;

        properties = settingsController.getProperties();
        final String languageTag = properties.getProperty("locale", DEFAULT_LOCALE);

        logger.info("Attempting to load the ResourceBundle for locale {}", languageTag);

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
        final String locale = properties.getProperty("locale", DEFAULT_LOCALE);
        final String logLevel = properties.getProperty("loglevel", "INFO");
        long timerDelay = Long.parseLong(properties.getProperty("timerDelay", "3600000"));
        final int delayType = Integer.parseInt(properties.getProperty("timerDelayType", "0"));

        if (timerDelay < 1) {
            timerDelay = 1;
        }

        chbAutoUpdate.setSelected(autoUpdate);
        chbTimerApplicationShutdown.setSelected(Boolean.parseBoolean(properties.getProperty("timerApplicationShutdown", "false")));

        switch (locale.toLowerCase()) {
            case "de-de" -> cboLanguage.getSelectionModel().select(1);
            case "es-es" -> cboLanguage.getSelectionModel().select(2);
            case "fr-fr" -> cboLanguage.getSelectionModel().select(3);
            case "nl-nl" -> cboLanguage.getSelectionModel().select(4);
            case "ru-ru" -> cboLanguage.getSelectionModel().select(5);
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

        final ObservableList<String> options = FXCollections.observableArrayList(
                translationBundle.getString("Seconds"),
                translationBundle.getString("Minutes"),
                translationBundle.getString("Hours")
        );

        cboDelayType.setItems(options);
        cboDelayType.getSelectionModel().select(delayType);

        final long correctDelay = switch (delayType) {
            case 0 -> TimeUnit.MILLISECONDS.toSeconds(timerDelay);
            case 1 -> TimeUnit.MILLISECONDS.toMinutes(timerDelay);
            case 2 -> TimeUnit.MILLISECONDS.toHours(timerDelay);
            default -> throw new IllegalStateException("Unexpected value: " + delayType);
        };

        numDelay.setText(String.valueOf(correctDelay));
    }

    /**
     * Reset all settings to their default values after user confirmation
     */
    @FXML
    private void resetSettingsAction() {
        logger.info("Attempting to reset all settings");
        if (FxUtils.showConfirmationAlert(translationBundle.getString("ConfirmReset"), getClass().getResourceAsStream(SharedVariables.ICON_URL))) {
            showAlertIfLanguageMismatch(DEFAULT_LOCALE);

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
        properties.setProperty("trayIcon", Boolean.toString(chbAutoUpdate.isSelected()));

        showAlertIfLanguageMismatch(properties.getProperty("locale", DEFAULT_LOCALE));

        switch (cboLanguage.getSelectionModel().getSelectedIndex()) {
            case 1 -> properties.setProperty("locale", "de-DE");
            case 2 -> properties.setProperty("locale", "es-es");
            case 3 -> properties.setProperty("locale", "fr-FR");
            case 4 -> properties.setProperty("locale", "nl-NL");
            case 5 -> properties.setProperty("locale", "ru-RU");
            default -> properties.setProperty("locale", DEFAULT_LOCALE);
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

        final int delayType = cboDelayType.getSelectionModel().getSelectedIndex();
        final long delay = Long.parseLong(numDelay.getText());

        if (delay < 1) {
            FxUtils.showErrorAlert(translationBundle.getString("SaveSettingsError"), translationBundle.getString("TimerDelayTooSmall"), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            return;
        }

        final long correctDelay = switch (delayType) {
            case 0 -> TimeUnit.SECONDS.toMillis(delay);
            case 1 -> TimeUnit.MINUTES.toMillis(delay);
            case 2 -> TimeUnit.HOURS.toMillis(delay);
            default -> throw new IllegalStateException("Unexpected value: " + delayType);
        };

        properties.setProperty("timerDelay", String.valueOf(correctDelay));
        properties.setProperty("timerDelayType", String.valueOf(delayType));
        properties.setProperty("timerApplicationShutdown", String.valueOf(chbTimerApplicationShutdown.isSelected()));

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
        final String newLanguage = switch (cboLanguage.getSelectionModel().getSelectedIndex()) {
            case 1 -> "fr-FR";
            case 2 -> "nl-NL";
            default -> DEFAULT_LOCALE;
        };

        if (!languageToMatch.equals(newLanguage)) {
            FxUtils.showInformationAlert(translationBundle.getString("RestartRequired"), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }
}
