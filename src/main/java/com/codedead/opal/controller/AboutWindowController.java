package com.codedead.opal.controller;

import com.codedead.opal.interfaces.IRunnableHelper;
import com.codedead.opal.utils.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public final class AboutWindowController {

    @FXML
    private Label aboutLabel;
    @FXML
    private ImageView aboutImageView;

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
        final String languageTag = properties.getProperty("locale", "en-US");

        logger.info("Attempting to load the ResourceBundle for locale {}", languageTag);

        final Locale locale = Locale.forLanguageTag(languageTag);
        translationBundle = ResourceBundle.getBundle("translations.OpalApplication", locale);
    }

    /**
     * Method that is invoked to initialize the FXML window
     */
    @FXML
    private void initialize() {
        logger.info("Initializing AboutWindow");

        aboutImageView.setFitHeight(96);
        aboutImageView.setFitWidth(96);

        final InputStream inputStream = getClass().getResourceAsStream("/images/opal.png");
        if (inputStream != null) {
            aboutImageView.setImage(new Image(inputStream));
        } else {
            aboutImageView.setImage(null);
        }
    }

    /**
     * Method that is called when the close button is selected
     */
    @FXML
    private void closeAction() {
        logger.info("Closing AboutWindow");

        final Stage stage = (Stage) aboutLabel.getScene().getWindow();
        stage.close();
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
