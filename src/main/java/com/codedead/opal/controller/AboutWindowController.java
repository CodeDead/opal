package com.codedead.opal.controller;

import com.codedead.opal.interfaces.IRunnableHelper;
import com.codedead.opal.utils.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import static com.codedead.opal.utils.SharedVariables.DEFAULT_LOCALE;

public final class AboutWindowController {

    private SettingsController settingsController;
    private ResourceBundle translationBundle;
    private final HelpUtils helpUtils;
    private final Logger logger;

    /**
     * Initialize a new AboutWindowController
     */
    public AboutWindowController() {
        logger = LogManager.getLogger(AboutWindowController.class);
        logger.info("Initializing new AboutWindowController object");

        helpUtils = new HelpUtils();
    }

    /**
     * Get the {@link SettingsController} object
     *
     * @return The {@link SettingsController} object
     */
    public SettingsController getSettingsController() {
        return settingsController;
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

        final Properties properties = settingsController.getProperties();
        final String languageTag = properties.getProperty("locale", DEFAULT_LOCALE);

        logger.info("Attempting to load the ResourceBundle for locale {}", languageTag);

        final Locale locale = Locale.forLanguageTag(languageTag);
        translationBundle = ResourceBundle.getBundle("translations.OpalApplication", locale);
    }

    /**
     * Method that is called when the close button is selected
     */
    @FXML
    private void closeAction(final ActionEvent event) {
        logger.info("Closing AboutWindow");
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }

    /**
     * Method that is called when the license button is selected
     */
    @FXML
    private void licenseAction() {
        logger.info("Attempting to open the license file");

        try {
            helpUtils.openFileFromResources(new RunnableFileOpener(SharedVariables.LICENSE_FILE_LOCATION, new IRunnableHelper() {
                @Override
                public void executed() {
                    Platform.runLater(() -> logger.info("Successfully opened the license file"));
                }

                @Override
                public void exceptionOccurred(final Exception ex) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            logger.error("Error opening the license file", ex);
                            FxUtils.showErrorAlert(translationBundle.getString("LicenseFileError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
                        }
                    });

                }
            }), SharedVariables.LICENSE_RESOURCE_LOCATION);
        } catch (final IOException ex) {
            logger.error("Error opening the license file", ex);
            FxUtils.showErrorAlert(translationBundle.getString("LicenseFileError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Method that is called when the CodeDead button is selected
     */
    @FXML
    private void codeDeadAction() {
        logger.info("Opening the CodeDead website");

        final RunnableSiteOpener runnableSiteOpener = new RunnableSiteOpener("https://codedead.com", new IRunnableHelper() {
            @Override
            public void executed() {
                Platform.runLater(() -> logger.info("Successfully opened website"));
            }

            @Override
            public void exceptionOccurred(final Exception ex) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        logger.error("Error opening the CodeDead website", ex);
                        FxUtils.showErrorAlert(translationBundle.getString("WebsiteError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
                    }
                });
            }
        });

        new Thread(runnableSiteOpener).start();
    }
}
