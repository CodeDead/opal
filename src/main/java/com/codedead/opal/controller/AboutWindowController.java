package com.codedead.opal.controller;

import com.codedead.opal.interfaces.IRunnableHelper;
import com.codedead.opal.utils.FxUtils;
import com.codedead.opal.utils.HelpUtils;
import com.codedead.opal.utils.RunnableFileOpener;
import com.codedead.opal.utils.RunnableSiteOpener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public final class AboutWindowController {

    private static final Logger logger = LogManager.getLogger(AboutWindowController.class);

    @FXML
    private Label aboutLabel;
    @FXML
    private ImageView aboutImageView;

    private final HelpUtils helpUtils;
    private SettingsController settingsController;
    private ResourceBundle translationBundle;

    /**
     * Initialize a new AboutWindowController
     */
    public AboutWindowController() {
        logger.info("Initializing new AboutWindowController object");

        helpUtils = new HelpUtils();
    }

    /**
     * Get the SettingsController
     *
     * @return The SettingsController
     */
    public final SettingsController getSettingsController() {
        return settingsController;
    }

    /**
     * Set the SettingsController
     *
     * @param settingsController The SettingsController
     */
    public final void setSettingsController(final SettingsController settingsController) {
        if (settingsController == null)
            throw new NullPointerException("SettingsController cannot be null!");

        this.settingsController = settingsController;

        final Properties properties = settingsController.getProperties();
        final String languageTag = properties.getProperty("locale", "en-US");

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Attempting to load the ResourceBundle for locale %s", languageTag));
        }
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
        aboutImageView.setImage(new Image(getClass().getResourceAsStream("/images/opal.png")));
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
            helpUtils.openFileFromResources(new RunnableFileOpener("license.pdf", new IRunnableHelper() {
                @Override
                public final void executed() {
                    Platform.runLater(() -> logger.info("Successfully opened the license file"));
                }

                @Override
                public final void exceptionOccurred(final Exception ex) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public final void run() {
                            logger.error("Error opening the license file", ex);
                            FxUtils.showErrorAlert(translationBundle.getString("LicenseFileError"), ex.getMessage(), getClass().getResourceAsStream("/images/opal.png"));
                        }
                    });

                }
            }), "/documents/license.pdf");
        } catch (final IOException ex) {
            logger.error("Error opening the license file", ex);
            FxUtils.showErrorAlert(translationBundle.getString("LicenseFileError"), ex.getMessage(), getClass().getResourceAsStream("/images/opal.png"));
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
            public final void executed() {
                Platform.runLater(() -> logger.info("Successfully opened website"));
            }

            @Override
            public final void exceptionOccurred(final Exception ex) {
                Platform.runLater(new Runnable() {
                    @Override
                    public final void run() {
                        logger.error("Error opening the CodeDead website", ex);
                        FxUtils.showErrorAlert(translationBundle.getString("WebsiteError"), ex.getMessage(), getClass().getResourceAsStream("/images/opal.png"));
                    }
                });
            }
        });

        new Thread(runnableSiteOpener).start();
    }
}
