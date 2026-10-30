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
import java.io.Serial;
import java.security.GeneralSecurityException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Properties;
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

        final SettingsDraft draft;
        final ResourceBundle newTranslationBundle;
        try {
            draft = readDraft();
            final Locale locale = LanguageController.getLocaleFromLanguageTag(draft.localeTag());
            newTranslationBundle = ResourceBundle.getBundle("translations.OpalApplication", locale);
        } catch (final SettingsValidationException ex) {
            logger.warn("Settings validation failed: {}", ex.getMessageKey());
            restoreSavedTheme();
            showSaveError(translationBundle.getString(ex.getMessageKey()), ex.getCause());
            return;
        } catch (final MissingResourceException ex) {
            restoreSavedTheme();
            showSaveError(ex.toString(), ex);
            return;
        }

        try {
            settingsController.saveProperties(draft.toProperties(settingsController.getProperties()));
        } catch (final IOException ex) {
            restoreSavedTheme();
            showSaveError(ex.toString(), ex);
            return;
        }

        translationBundle = newTranslationBundle;
        applySettings(draft, newTranslationBundle);

        final Stage stage = (Stage) sldAudioBalance.getScene().getWindow();
        stage.close();
    }

    /**
     * Read and validate all settings without changing any application state
     *
     * @return The validated settings
     * @throws SettingsValidationException When one of the settings is invalid
     */
    private SettingsDraft readDraft() throws SettingsValidationException {
        final boolean updateProxyEnabled = chbUpdateProxy.isSelected();
        final String updateProxyHost = valueOrEmpty(txtProxyHost.getText()).trim();
        final String updateProxyPortValue = valueOrEmpty(numProxyPort.getText()).trim();
        final boolean updateProxyAuthEnabled = chbProxyAuthentication.isSelected();
        final String updateProxyUsername = valueOrEmpty(txtProxyUsername.getText()).trim();
        final String updateProxyPassword = valueOrEmpty(pfProxyPassword.getText());

        final int updateProxyPort = parseProxyPort(updateProxyPortValue, updateProxyEnabled);

        if (updateProxyEnabled && updateProxyHost.isBlank()) {
            throw new SettingsValidationException("InvalidProxySettings");
        }

        if (updateProxyEnabled && updateProxyAuthEnabled
                && (updateProxyUsername.isBlank() || updateProxyPassword.isEmpty())) {
            throw new SettingsValidationException("InvalidProxyAuthentication");
        }

        final long delay = parseTimerDelay(numDelay.getText());
        final int delayType = cboDelayType.getSelectionModel().getSelectedIndex();

        if (delayType < 0 || delayType > 2) {
            throw new SettingsValidationException("InvalidTimerDelay");
        }

        final long timerDelay = switch (delayType) {
            case 0 -> TimeUnit.SECONDS.toMillis(delay);
            case 1 -> TimeUnit.MINUTES.toMillis(delay);
            case 2 -> TimeUnit.HOURS.toMillis(delay);
            default -> throw new SettingsValidationException("InvalidTimerDelay");
        };

        final String languageTag = LanguageController.getLocaleFromLanguageIndex(cboLanguage.getSelectionModel().getSelectedIndex());
        final String theme = cboTheme.getSelectionModel().getSelectedItem();
        final String logLevel = cboLogLevel.getValue();

        if (theme == null || logLevel == null) {
            throw new SettingsValidationException("SaveSettingsError");
        }

        final double audioBalance = sldAudioBalance.getValue();
        if (!Double.isFinite(audioBalance) || audioBalance < -1.0 || audioBalance > 1.0) {
            throw new SettingsValidationException("SaveSettingsError");
        }

        final String storedProxyPassword;
        try {
            storedProxyPassword = passwordCipher.encrypt(updateProxyPassword);
        } catch (final IOException | GeneralSecurityException ex) {
            throw new SettingsValidationException("SaveSettingsError", ex);
        }

        return new SettingsDraft(
                chbAutoUpdate.isSelected(),
                updateProxyEnabled,
                updateProxyHost,
                updateProxyPort,
                updateProxyAuthEnabled,
                updateProxyUsername,
                storedProxyPassword,
                chbMediaButtons.isSelected(),
                chbDragDrop.isSelected(),
                chbTrayIcon.isSelected(),
                languageTag,
                theme,
                logLevel,
                timerDelay,
                delayType,
                chbTimerApplicationShutdown.isSelected(),
                chbTimerComputerShutdown.isSelected(),
                audioBalance
        );
    }

    /**
     * Parse a proxy port
     *
     * @param value        The value of the proxy port field
     * @param proxyEnabled True if the proxy is enabled
     * @return The proxy port, or the default port if the value is empty
     * @throws SettingsValidationException When the port is invalid and the proxy is enabled
     */
    private int parseProxyPort(final String value, final boolean proxyEnabled) throws SettingsValidationException {
        if (value.isBlank())
            return 8080;

        try {
            final int port = Integer.parseInt(value);

            if (proxyEnabled && (port < 1 || port > 65535)) {
                throw new SettingsValidationException("InvalidProxySettings");
            }

            return port;
        } catch (final NumberFormatException ex) {
            if (proxyEnabled) {
                throw new SettingsValidationException("InvalidProxySettings", ex);
            }

            return 8080;
        }
    }

    /**
     * Parse the timer delay
     *
     * @param value The value of the timer delay field
     * @return The timer delay
     * @throws SettingsValidationException When the delay is missing, invalid or smaller than 1
     */
    private long parseTimerDelay(final String value) throws SettingsValidationException {
        if (value == null || value.isBlank()) {
            throw new SettingsValidationException("InvalidTimerDelay");
        }

        try {
            final long delay = Long.parseLong(value.trim());

            if (delay < 1) {
                throw new SettingsValidationException("TimerDelayTooSmall");
            }

            return delay;
        } catch (final NumberFormatException ex) {
            throw new SettingsValidationException("InvalidTimerDelay", ex);
        }
    }

    /**
     * Display an error that occurred while saving settings
     *
     * @param message The message that should be displayed
     * @param cause   The cause of the error, if any
     */
    private void showSaveError(final String message, final Throwable cause) {
        if (cause != null) {
            logger.error("Unable to save settings", cause);
        }

        FxUtils.showErrorAlert(translationBundle.getString("SaveSettingsError"), message, getClass().getResourceAsStream(SharedVariables.ICON_URL));
    }

    /**
     * Restore the theme that is currently stored in the settings
     */
    private void restoreSavedTheme() {
        final String savedTheme = settingsController.getProperties().getProperty("theme", "light");
        final int savedThemeIndex = ThemeController.getThemeIndex(savedTheme);

        if (cboTheme.getSelectionModel().getSelectedIndex() != savedThemeIndex) {
            cboTheme.getSelectionModel().select(savedThemeIndex);
        }

        ThemeController.setTheme(savedTheme);
    }

    /**
     * Apply the validated settings to the running application
     *
     * @param draft                The validated settings
     * @param newTranslationBundle The translation bundle for the selected language
     */
    private void applySettings(final SettingsDraft draft, final ResourceBundle newTranslationBundle) {
        mainWindowController.updateResourceBundle(newTranslationBundle);
        mainWindowController.loadMediaButtonVisibility(draft.mediaButtons());
        mainWindowController.setAudioBalance(draft.audioBalance());

        ThemeController.setTheme(draft.theme());
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), toLogLevel(draft.logLevel()));

        try {
            mainWindowController.setTrayIconEnabled(draft.trayIcon());
        } catch (final IOException ex) {
            logger.error("Unable to create tray icon", ex);
            FxUtils.showErrorAlert(newTranslationBundle.getString("TrayIconError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Convert a log level name to a {@link Level} object
     *
     * @param logLevel The name of the log level
     * @return The {@link Level} object
     */
    private static Level toLogLevel(final String logLevel) {
        return switch (logLevel) {
            case "OFF" -> Level.OFF;
            case "FATAL" -> Level.FATAL;
            case "ERROR" -> Level.ERROR;
            case "WARN" -> Level.WARN;
            case "DEBUG" -> Level.DEBUG;
            case "TRACE" -> Level.TRACE;
            case "ALL" -> Level.ALL;
            default -> Level.INFO;
        };
    }

    /**
     * Get a string value or an empty string when the value is null
     *
     * @param value The value
     * @return The value or an empty string
     */
    private static String valueOrEmpty(final String value) {
        return value == null ? "" : value;
    }

    /**
     * A validated snapshot of all settings
     */
    private record SettingsDraft(
            boolean autoUpdate,
            boolean updateProxyEnabled,
            String updateProxyHost,
            int updateProxyPort,
            boolean updateProxyAuthEnabled,
            String updateProxyUsername,
            String encryptedProxyPassword,
            boolean mediaButtons,
            boolean dragDrop,
            boolean trayIcon,
            String localeTag,
            String theme,
            String logLevel,
            long timerDelay,
            int timerDelayType,
            boolean timerApplicationShutdown,
            boolean timerComputerShutdown,
            double audioBalance
    ) {

        /**
         * Create a new {@link Properties} object based on the current settings and this draft
         *
         * @param base The current properties
         * @return The new properties
         */
        private Properties toProperties(final Properties base) {
            final Properties properties = new Properties();
            properties.putAll(base);

            properties.setProperty("autoUpdate", Boolean.toString(autoUpdate));
            properties.setProperty("updateProxyEnabled", Boolean.toString(updateProxyEnabled));
            properties.setProperty("updateProxyHost", updateProxyHost);
            properties.setProperty("updateProxyPort", Integer.toString(updateProxyPort));
            properties.setProperty("updateProxyAuthEnabled", Boolean.toString(updateProxyAuthEnabled));
            properties.setProperty("updateProxyUsername", updateProxyUsername);
            properties.setProperty("updateProxyPassword", encryptedProxyPassword);
            properties.setProperty("mediaButtons", Boolean.toString(mediaButtons));
            properties.setProperty("dragDrop", Boolean.toString(dragDrop));
            properties.setProperty("trayIcon", Boolean.toString(trayIcon));
            properties.setProperty("locale", localeTag);
            properties.setProperty("theme", theme);
            properties.setProperty("loglevel", logLevel);
            properties.setProperty("timerDelay", Long.toString(timerDelay));
            properties.setProperty("timerDelayType", Integer.toString(timerDelayType));
            properties.setProperty("timerApplicationShutdown", Boolean.toString(timerApplicationShutdown));
            properties.setProperty("timerComputerShutdown", Boolean.toString(timerComputerShutdown));
            properties.setProperty("audioBalance", Double.toString(audioBalance));

            return properties;
        }
    }

    /**
     * Exception that is thrown when a setting is invalid
     */
    private static final class SettingsValidationException extends Exception {

        @Serial
        private static final long serialVersionUID = 1L;

        private final String messageKey;

        /**
         * Initialize a new SettingsValidationException
         *
         * @param messageKey The translation key of the error message
         */
        private SettingsValidationException(final String messageKey) {
            super(messageKey);
            this.messageKey = messageKey;
        }

        /**
         * Initialize a new SettingsValidationException
         *
         * @param messageKey The translation key of the error message
         * @param cause      The cause of the exception
         */
        private SettingsValidationException(final String messageKey, final Throwable cause) {
            super(messageKey, cause);
            this.messageKey = messageKey;
        }

        /**
         * Get the translation key of the error message
         *
         * @return The translation key
         */
        private String getMessageKey() {
            return messageKey;
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
