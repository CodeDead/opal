package com.codedead.opal.controller;

import atlantafx.base.theme.*;
import com.codedead.opal.domain.NumberTextField;
import com.codedead.opal.domain.OSType;
import com.codedead.opal.domain.OsCheck;
import com.codedead.opal.utils.FxUtils;
import com.codedead.opal.utils.PasswordCipher;
import com.codedead.opal.utils.SharedVariables;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static com.codedead.opal.utils.SharedVariables.DEFAULT_LOCALE;

public final class SettingsWindowController {

    @FXML
    private Slider sldAudioBalance;
    @FXML
    private CheckBox chbTimerComputerShutdown;
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
    @FXML
    private CheckBox chbUpdateProxy;
    @FXML
    private TextField txtProxyHost;
    @FXML
    private NumberTextField numProxyPort;
    @FXML
    private CheckBox chbProxyAuthentication;
    @FXML
    private TextField txtProxyUsername;
    @FXML
    private PasswordField pfProxyPassword;

    private MainWindowController mainWindowController;
    private SettingsController settingsController;
    private ResourceBundle translationBundle;

    private final PasswordCipher passwordCipher;
    private final Logger logger;

    /**
     * Initialize a new SettingsWindowController
     */
    public SettingsWindowController() {
        logger = LogManager.getLogger(SettingsWindowController.class);
        logger.info("Initializing new SettingsWindowController object");
        passwordCipher = new PasswordCipher();
    }

    /**
     * FXML initialize method
     */
    @FXML
    private void initialize() {
        cboTheme.getSelectionModel()
                .selectedItemProperty()
                .addListener((options, oldValue, newValue) -> ThemeController.setTheme(cboTheme.getValue()));

        if (Objects.requireNonNull(OsCheck.getOperatingSystemType()) == OSType.OTHER) {
            chbTimerComputerShutdown.setDisable(true);
        }

        chbUpdateProxy.selectedProperty()
                .addListener((options, oldValue, newValue) -> updateProxyControls());
        chbProxyAuthentication.selectedProperty()
                .addListener((options, oldValue, newValue) -> updateProxyAuthenticationControls());
        updateProxyControls();
    }

    /**
     * Enable or disable the proxy controls based on the checkbox state
     */
    private void updateProxyControls() {
        final boolean disabled = !chbUpdateProxy.isSelected();
        txtProxyHost.setDisable(disabled);
        numProxyPort.setDisable(disabled);
        chbProxyAuthentication.setDisable(disabled);
        updateProxyAuthenticationControls();
    }

    /**
     * Enable or disable the proxy authentication controls based on the checkbox states
     */
    private void updateProxyAuthenticationControls() {
        final boolean disabled = !chbUpdateProxy.isSelected() || !chbProxyAuthentication.isSelected();
        txtProxyUsername.setDisable(disabled);
        pfProxyPassword.setDisable(disabled);
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

        final Locale locale = LanguageController.getLocaleFromLanguageTag(languageTag);
        translationBundle = ResourceBundle.getBundle("translations.OpalApplication", locale);

        final ObservableList<String> options = FXCollections.observableArrayList(
                translationBundle.getString("Seconds"),
                translationBundle.getString("Minutes"),
                translationBundle.getString("Hours")
        );
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

        cboLanguage.getSelectionModel().select(LanguageController.getLanguageIndexFromLocale(settingsController.getProperties().getProperty("locale", DEFAULT_LOCALE)));

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

        final long correctDelay = switch (delayType) {
            case 0 -> TimeUnit.MILLISECONDS.toSeconds(timerDelay);
            case 1 -> TimeUnit.MILLISECONDS.toMinutes(timerDelay);
            case 2 -> TimeUnit.MILLISECONDS.toHours(timerDelay);
            default -> throw new IllegalStateException("Unexpected value: " + delayType);
        };

        chbAutoUpdate.setSelected(Boolean.parseBoolean(settingsController.getProperties().getProperty("autoUpdate", "true")));
        chbUpdateProxy.setSelected(Boolean.parseBoolean(settingsController.getProperties().getProperty("updateProxyEnabled", "false")));
        txtProxyHost.setText(settingsController.getProperties().getProperty("updateProxyHost", ""));
        numProxyPort.setText(settingsController.getProperties().getProperty("updateProxyPort", "8080"));
        chbProxyAuthentication.setSelected(Boolean.parseBoolean(settingsController.getProperties().getProperty("updateProxyAuthEnabled", "false")));
        txtProxyUsername.setText(settingsController.getProperties().getProperty("updateProxyUsername", ""));
        pfProxyPassword.setText(decryptProxyPassword(settingsController.getProperties().getProperty("updateProxyPassword", "")));
        chbMediaButtons.setSelected(Boolean.parseBoolean(settingsController.getProperties().getProperty("mediaButtons", "true")));
        chbDragDrop.setSelected(Boolean.parseBoolean(settingsController.getProperties().getProperty("dragDrop", "true")));
        chbTrayIcon.setSelected(Boolean.parseBoolean(settingsController.getProperties().getProperty("trayIcon", "false")));
        chbTimerApplicationShutdown.setSelected(Boolean.parseBoolean(settingsController.getProperties().getProperty("timerApplicationShutdown", "false")));
        cboDelayType.getSelectionModel().select(delayType);
        cboTheme.getSelectionModel().select(ThemeController.getThemeIndex(settingsController.getProperties().getProperty("theme", "light")));
        numDelay.setText(String.valueOf(correctDelay));
        chbTimerComputerShutdown.setSelected(Boolean.parseBoolean(settingsController.getProperties().getProperty("timerComputerShutdown", "false")));

        sldAudioBalance.setValue(Double.parseDouble(settingsController.getProperties().getProperty("audioBalance", "0.0")));
    }

    /**
     * Decrypt the stored proxy password
     *
     * @param storedPassword The stored password
     * @return The decrypted password, or an empty {@link String} if it could not be decrypted
     */
    private String decryptProxyPassword(final String storedPassword) {
        try {
            return passwordCipher.decrypt(storedPassword);
        } catch (final IOException | GeneralSecurityException ex) {
            logger.error("Unable to decrypt the proxy password", ex);
            return "";
        }
    }

    /**
     * Reset all settings to their default values after user confirmation
     */
    @FXML
    private void resetSettingsAction() {
        logger.info("Attempting to reset all settings");
        if (FxUtils.showConfirmationAlert(translationBundle.getString("ConfirmReset"), getClass().getResourceAsStream(SharedVariables.ICON_URL))) {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            try {
                settingsController.createDefaultProperties();
                settingsController.setProperties(settingsController.readPropertiesFile());

                final Locale locale = LanguageController.getLocaleFromLanguageTag(DEFAULT_LOCALE);
                final ResourceBundle newTranslationBundle = ResourceBundle.getBundle("translations.OpalApplication", locale);

                this.translationBundle = newTranslationBundle;
                mainWindowController.updateResourceBundle(newTranslationBundle);

                mainWindowController.loadMediaButtonVisibility(Boolean.parseBoolean(settingsController.getProperties().getProperty("mediaButtons", "true")));
                mainWindowController.setAudioBalance(Double.parseDouble(settingsController.getProperties().getProperty("audioBalance", "0.0")));

                mainWindowController.setTrayIconEnabled(false);

                Stage stage = (Stage) sldAudioBalance.getScene().getWindow();
                stage.close();
            } catch (final IOException ex) {
                logger.error("Unable to reset all settings", ex);
                FxUtils.showErrorAlert(translationBundle.getString("ResetSettingsError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            }
        }
    }

    /**
     * Method that is called when all settings should be saved
     */
    @FXML
    private void saveSettingsAction() {
        logger.info("Attempting to save all settings");

        final boolean updateProxyEnabled = chbUpdateProxy.isSelected();
        final String updateProxyHost = txtProxyHost.getText() == null ? "" : txtProxyHost.getText().trim();
        final String updateProxyPortValue = numProxyPort.getText() == null ? "" : numProxyPort.getText().trim();
        final boolean updateProxyAuthEnabled = chbProxyAuthentication.isSelected();
        final String updateProxyUsername = txtProxyUsername.getText() == null ? "" : txtProxyUsername.getText().trim();
        final String updateProxyPassword = pfProxyPassword.getText() == null ? "" : pfProxyPassword.getText();
        int updateProxyPort = 8080;

        if (!updateProxyPortValue.isBlank()) {
            try {
                updateProxyPort = Integer.parseInt(updateProxyPortValue);
            } catch (final NumberFormatException ex) {
                if (updateProxyEnabled) {
                    logger.warn("Invalid proxy port: {}", updateProxyPortValue);
                    FxUtils.showErrorAlert(translationBundle.getString("SaveSettingsError"), translationBundle.getString("InvalidProxySettings"), getClass().getResourceAsStream(SharedVariables.ICON_URL));
                    return;
                }
            }
        }

        if (updateProxyEnabled && (updateProxyHost.isBlank() || updateProxyPort < 1 || updateProxyPort > 65535)) {
            logger.warn("Invalid proxy settings: {}:{}", updateProxyHost, updateProxyPort);
            FxUtils.showErrorAlert(translationBundle.getString("SaveSettingsError"), translationBundle.getString("InvalidProxySettings"), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            return;
        }

        if (updateProxyEnabled && updateProxyAuthEnabled && (updateProxyUsername.isBlank() || updateProxyPassword.isEmpty())) {
            logger.warn("Invalid proxy authentication settings");
            FxUtils.showErrorAlert(translationBundle.getString("SaveSettingsError"), translationBundle.getString("InvalidProxyAuthentication"), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            return;
        }

        final String storedProxyPassword;
        try {
            storedProxyPassword = passwordCipher.encrypt(updateProxyPassword);
        } catch (final IOException | GeneralSecurityException ex) {
            logger.error("Unable to encrypt the proxy password", ex);
            FxUtils.showErrorAlert(translationBundle.getString("SaveSettingsError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            return;
        }

        settingsController.getProperties().setProperty("autoUpdate", Boolean.toString(chbAutoUpdate.isSelected()));
        settingsController.getProperties().setProperty("updateProxyEnabled", Boolean.toString(updateProxyEnabled));
        settingsController.getProperties().setProperty("updateProxyHost", updateProxyHost);
        settingsController.getProperties().setProperty("updateProxyPort", String.valueOf(updateProxyPort));
        settingsController.getProperties().setProperty("updateProxyAuthEnabled", Boolean.toString(updateProxyAuthEnabled));
        settingsController.getProperties().setProperty("updateProxyUsername", updateProxyUsername);
        settingsController.getProperties().setProperty("updateProxyPassword", storedProxyPassword);
        settingsController.getProperties().setProperty("mediaButtons", Boolean.toString(chbMediaButtons.isSelected()));
        settingsController.getProperties().setProperty("dragDrop", Boolean.toString(chbDragDrop.isSelected()));
        settingsController.getProperties().setProperty("trayIcon", Boolean.toString(chbTrayIcon.isSelected()));

        final String languageTag = LanguageController.getLocaleFromLanguageIndex(cboLanguage.getSelectionModel().getSelectedIndex());
        final Locale locale = LanguageController.getLocaleFromLanguageTag(languageTag);
        final ResourceBundle newTranslationBundle = ResourceBundle.getBundle("translations.OpalApplication", locale);

        this.translationBundle = newTranslationBundle;
        mainWindowController.updateResourceBundle(newTranslationBundle);

        settingsController.getProperties().setProperty("locale", languageTag);

        mainWindowController.loadMediaButtonVisibility(chbMediaButtons.isSelected());
        try {
            mainWindowController.setTrayIconEnabled(chbTrayIcon.isSelected());
        } catch (final IOException ex) {
            logger.error("Unable to create tray icon", ex);
            FxUtils.showErrorAlert(newTranslationBundle.getString("TrayIconError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }

        settingsController.getProperties().setProperty("theme", cboTheme.getSelectionModel().getSelectedItem());
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
        settingsController.getProperties().setProperty("timerComputerShutdown", String.valueOf(chbTimerComputerShutdown.isSelected()));

        settingsController.getProperties().setProperty("audioBalance", String.valueOf(sldAudioBalance.getValue()));
        mainWindowController.setAudioBalance(sldAudioBalance.getValue());

        Configurator.setAllLevels(LogManager.getRootLogger().getName(), level);
        try {
            settingsController.saveProperties();

            Stage stage = (Stage) sldAudioBalance.getScene().getWindow();
            stage.close();
        } catch (final IOException ex) {
            logger.error("Unable to save all settings", ex);
            FxUtils.showErrorAlert(translationBundle.getString("SaveSettingsError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
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
