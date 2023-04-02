package com.codedead.opal.controller;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.codedead.opal.domain.NumberTextField;
import com.codedead.opal.utils.FxUtils;
import com.codedead.opal.utils.SharedVariables;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static com.codedead.opal.utils.SharedVariables.DEFAULT_LOCALE;

public final class SettingsWindowController {

    @FXML
    private CheckBox chbTrayIcon;
    @FXML
    private ComboBox<String> cboTheme;
    @FXML
    private CheckBox chbDragDrop;
    @FXML
    private CheckBox chbMediaButtons;
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

    private MainWindowController mainWindowController;
    private SettingsController settingsController;
    private ResourceBundle translationBundle;

    private final Logger logger;

    /**
     * Initialize a new SettingsWindowController
     */
    public SettingsWindowController() {
        logger = LogManager.getLogger(SettingsWindowController.class);
        logger.info("Initializing new SettingsWindowController object");
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
        final String languageTag = settingsController.getProperties().getProperty("locale", DEFAULT_LOCALE);

        logger.info("Attempting to load the ResourceBundle for locale {}", languageTag);

        final Locale locale = Locale.forLanguageTag(languageTag);
        translationBundle = ResourceBundle.getBundle("translations.OpalApplication", locale);

        final ObservableList<String> options = FXCollections.observableArrayList(
                translationBundle.getString("Seconds"),
                translationBundle.getString("Minutes"),
                translationBundle.getString("Hours")
        );

        final ObservableList<String> themes = FXCollections.observableArrayList(
                translationBundle.getString("Light"),
                translationBundle.getString("Dark"),
                translationBundle.getString("NordLight"),
                translationBundle.getString("NordDark")
        );

        cboTheme.setItems(themes);
        cboDelayType.setItems(options);

        loadSettings();
    }

    /**
     * Set the {@link MainWindowController} object
     *
     * @param mainWindowController The {@link MainWindowController} object
     */
    public void setMainWindowController(final MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    /**
     * Load all the settings into the UI
     */
    private void loadSettings() {
        logger.info("Attempting to load all settings");
        long timerDelay = Long.parseLong(settingsController.getProperties().getProperty("timerDelay", "3600000"));
        final int delayType = Integer.parseInt(settingsController.getProperties().getProperty("timerDelayType", "0"));

        if (timerDelay < 1) {
            timerDelay = 1;
        }

        switch (settingsController.getProperties().getProperty("locale", DEFAULT_LOCALE).toLowerCase()) {
            case "de-de" -> cboLanguage.getSelectionModel().select(1);
            case "es-es" -> cboLanguage.getSelectionModel().select(2);
            case "fr-fr" -> cboLanguage.getSelectionModel().select(3);
            case "jp-jp" -> cboLanguage.getSelectionModel().select(4);
            case "nl-nl" -> cboLanguage.getSelectionModel().select(5);
            case "ru-ru" -> cboLanguage.getSelectionModel().select(6);
            default -> cboLanguage.getSelectionModel().select(0);
        }

        switch (settingsController.getProperties().getProperty("loglevel", "INFO").toUpperCase()) {
            case "OFF" -> cboLogLevel.getSelectionModel().select(0);
            case "FATAL" -> cboLogLevel.getSelectionModel().select(1);
            case "ERROR" -> cboLogLevel.getSelectionModel().select(2);
            case "WARN" -> cboLogLevel.getSelectionModel().select(3);
            case "DEBUG" -> cboLogLevel.getSelectionModel().select(5);
            case "TRACE" -> cboLogLevel.getSelectionModel().select(6);
            case "ALL" -> cboLogLevel.getSelectionModel().select(7);
            default -> cboLogLevel.getSelectionModel().select(4);
        }

        final int themeIndex = switch (settingsController.getProperties().getProperty("theme", "light").toLowerCase()) {
            case "dark" -> 1;
            case "nordlight" -> 2;
            case "norddark" -> 3;
            default -> 0;
        };

        final long correctDelay = switch (delayType) {
            case 0 -> TimeUnit.MILLISECONDS.toSeconds(timerDelay);
            case 1 -> TimeUnit.MILLISECONDS.toMinutes(timerDelay);
            case 2 -> TimeUnit.MILLISECONDS.toHours(timerDelay);
            default -> throw new IllegalStateException("Unexpected value: " + delayType);
        };

        chbAutoUpdate.setSelected(Boolean.parseBoolean(settingsController.getProperties().getProperty("autoUpdate", "true")));
        chbMediaButtons.setSelected(Boolean.parseBoolean(settingsController.getProperties().getProperty("mediaButtons", "true")));
        chbDragDrop.setSelected(Boolean.parseBoolean(settingsController.getProperties().getProperty("dragDrop", "true")));
        chbTrayIcon.setSelected(Boolean.parseBoolean(settingsController.getProperties().getProperty("trayIcon", "false")));
        chbTimerApplicationShutdown.setSelected(Boolean.parseBoolean(settingsController.getProperties().getProperty("timerApplicationShutdown", "false")));
        cboDelayType.getSelectionModel().select(delayType);
        cboTheme.getSelectionModel().select(themeIndex);
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

            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            try {
                settingsController.createDefaultProperties();
                settingsController.setProperties(settingsController.readPropertiesFile());
                mainWindowController.loadMediaButtonVisibility(Boolean.parseBoolean(settingsController.getProperties().getProperty("mediaButtons", "true")));
                mainWindowController.hideTrayIcon();

                loadSettings();
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

        settingsController.getProperties().setProperty("autoUpdate", Boolean.toString(chbAutoUpdate.isSelected()));
        settingsController.getProperties().setProperty("mediaButtons", Boolean.toString(chbMediaButtons.isSelected()));
        settingsController.getProperties().setProperty("dragDrop", Boolean.toString(chbDragDrop.isSelected()));
        settingsController.getProperties().setProperty("trayIcon", Boolean.toString(chbTrayIcon.isSelected()));

        mainWindowController.loadMediaButtonVisibility(chbMediaButtons.isSelected());
        if (chbTrayIcon.isSelected()) {
            try {
                mainWindowController.showTrayIcon();
            } catch (final IOException ex) {
                logger.error("Unable to create tray icon", ex);
                FxUtils.showErrorAlert(translationBundle.getString("TrayIconError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            }
        } else {
            mainWindowController.hideTrayIcon();
        }

        showAlertIfLanguageMismatch(settingsController.getProperties().getProperty("locale", DEFAULT_LOCALE));

        switch (cboLanguage.getSelectionModel().getSelectedIndex()) {
            case 1 -> settingsController.getProperties().setProperty("locale", "de-DE");
            case 2 -> settingsController.getProperties().setProperty("locale", "es-es");
            case 3 -> settingsController.getProperties().setProperty("locale", "fr-FR");
            case 4 -> settingsController.getProperties().setProperty("locale", "jp-JP");
            case 5 -> settingsController.getProperties().setProperty("locale", "nl-NL");
            case 6 -> settingsController.getProperties().setProperty("locale", "ru-RU");
            default -> settingsController.getProperties().setProperty("locale", DEFAULT_LOCALE);
        }

        switch (cboTheme.getSelectionModel().getSelectedIndex()) {
            case 1 -> {
                settingsController.getProperties().setProperty("theme", "dark");
                Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
            }
            case 2 -> {
                settingsController.getProperties().setProperty("theme", "nordlight");
                Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
            }
            case 3 -> {
                settingsController.getProperties().setProperty("theme", "norddark");
                Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
            }
            default -> {
                settingsController.getProperties().setProperty("theme", "light");
                Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            }
        }

        settingsController.getProperties().setProperty("loglevel", cboLogLevel.getValue());

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

        settingsController.getProperties().setProperty("timerDelay", String.valueOf(correctDelay));
        settingsController.getProperties().setProperty("timerDelayType", String.valueOf(delayType));
        settingsController.getProperties().setProperty("timerApplicationShutdown", String.valueOf(chbTimerApplicationShutdown.isSelected()));

        Configurator.setAllLevels(LogManager.getRootLogger().getName(), level);
        try {
            settingsController.saveProperties();
        } catch (final IOException ex) {
            logger.error("Unable to save all settings", ex);
            FxUtils.showErrorAlert(translationBundle.getString("SaveSettingsError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Show an information alert if a restart is required
     *
     * @param languageToMatch The language that needs to be matched to the combobox
     */
    private void showAlertIfLanguageMismatch(final String languageToMatch) {
        final String newLanguage = switch (cboLanguage.getSelectionModel().getSelectedIndex()) {
            case 1 -> "de-DE";
            case 2 -> "es-es";
            case 3 -> "fr-FR";
            case 4 -> "jp-JP";
            case 5 -> "nl-NL";
            case 6 -> "ru-RU";
            default -> DEFAULT_LOCALE;
        };

        if (!languageToMatch.equals(newLanguage)) {
            FxUtils.showInformationAlert(translationBundle.getString("RestartRequired"), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Method that is invoked when the window should be closed
     *
     * @param event The {@link ActionEvent} object
     */
    @FXML
    private void cancelAction(final ActionEvent event) {
        logger.info("Closing SettingsWindow");
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }
}
