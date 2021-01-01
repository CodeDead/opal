package com.codedead.opal.controller;

import com.codedead.opal.domain.SoundPane;
import com.codedead.opal.utils.FxUtils;
import com.codedead.opal.utils.HelpUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public final class MainWindowController {

    private static final Logger logger = LogManager.getLogger(MainWindowController.class);

    @FXML
    private SoundPane snpFireplace;
    @FXML
    private SoundPane snpTraffic;
    @FXML
    private SoundPane snpChatter;
    @FXML
    private SoundPane snpTelephone;
    @FXML
    private SoundPane snpTyping;
    @FXML
    private SoundPane snpThunder;
    @FXML
    private SoundPane snpWind;
    @FXML
    private SoundPane snpRain;
    @FXML
    private MenuItem mniUpdate;
    @FXML
    private MenuItem mniHomepage;
    @FXML
    private MenuItem mniLicense;
    @FXML
    private MenuItem mniDonate;
    @FXML
    private MenuItem mniAbout;
    @FXML
    private MenuItem mniHelp;
    @FXML
    private MenuItem mniSettings;
    @FXML
    private MenuItem mniExit;
    @FXML
    private MenuItem mniReset;

    private final AudioController audioController;
    private SettingsController settingsController;
    private final HelpUtils helpUtils;
    private ResourceBundle translationBundle;

    /**
     * Initialize a new MainWindowController
     *
     * @throws URISyntaxException When an URI could not be formed
     */
    public MainWindowController() throws URISyntaxException {
        logger.info("Initializing new MainWindowController object");

        helpUtils = new HelpUtils();
        audioController = new AudioController();
    }

    /**
     * Get the SettingsController
     *
     * @return The SettingsController
     */
    public SettingsController getSettingsController() {
        return settingsController;
    }

    /**
     * Set the SettingsController
     *
     * @param settingsController The SettingsController
     */
    public void setSettingsController(final SettingsController settingsController) {
        if (settingsController == null)
            throw new NullPointerException("SettingsController cannot be null!");

        this.settingsController = settingsController;

        final Properties properties = settingsController.getProperties();
        final String languageTag = properties.getProperty("locale", "en-US");

        logger.info(String.format("Attempting to load the ResourceBundle for locale %s", languageTag));
        final Locale locale = Locale.forLanguageTag(languageTag);
        translationBundle = ResourceBundle.getBundle("translations.OpalApplication", locale);
    }

    /**
     * Method that is invoked to initialize the FXML window
     */
    @FXML
    public void initialize() {
        logger.info("Initializing MainWindow");

        mniReset.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/refresh.png"))));
        mniExit.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/remove.png"))));
        mniSettings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/settings.png"))));
        mniAbout.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/about.png"))));
        mniDonate.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/donate.png"))));
        mniLicense.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/license.png"))));
        mniHomepage.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/home.png"))));
        mniUpdate.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/update.png"))));
        mniHelp.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/help.png"))));

        // Set volume before playing for better user-experience
        snpRain.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.doubleValue() == 0) {
                audioController.stopRain();
            } else {
                audioController.setRainVolume(newValue.doubleValue() / 100);
                audioController.playRain();
            }
        });

        snpWind.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.doubleValue() == 0) {
                audioController.stopWind();
            } else {
                audioController.setWindVolume(newValue.doubleValue() / 100);
                audioController.playWind();
            }
        });

        snpThunder.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.doubleValue() == 0) {
                audioController.stopThunder();
            } else {
                audioController.setThunderVolume(newValue.doubleValue() / 100);
                audioController.playThunder();
            }
        });

        snpTyping.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.doubleValue() == 0) {
                audioController.stopKeyboard();
            } else {
                audioController.setKeyboardVolume(newValue.doubleValue() / 100);
                audioController.playKeyboard();
            }
        });

        snpTelephone.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.doubleValue() == 0) {
                audioController.stopPhone();
            } else {
                audioController.setPhoneVolume(newValue.doubleValue() / 100);
                audioController.playPhone();
            }
        });

        snpChatter.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.doubleValue() == 0) {
                audioController.stopChatter();
            } else {
                audioController.setChatterVolume(newValue.doubleValue() / 100);
                audioController.playChatter();
            }
        });

        snpTraffic.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.doubleValue() == 0) {
                audioController.stopTraffic();
            } else {
                audioController.setTrafficVolume(newValue.doubleValue() / 100);
                audioController.playTraffic();
            }
        });

        snpFireplace.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.doubleValue() == 0) {
                audioController.stopFireplace();
            } else {
                audioController.setFireplaceVolume(newValue.doubleValue() / 100);
                audioController.playFireplace();
            }
        });
    }

    /**
     * Method that is called when all players should be reset
     */
    @FXML
    public void resetAction() {
        logger.info("Resetting all audio sliders");

        snpRain.getSlider().setValue(0);
        snpWind.getSlider().setValue(0);
        snpThunder.getSlider().setValue(0);
        snpTyping.getSlider().setValue(0);
        snpTelephone.getSlider().setValue(0);
        snpChatter.getSlider().setValue(0);
        snpTraffic.getSlider().setValue(0);
        snpFireplace.getSlider().setValue(0);
    }

    /**
     * Method that is called when the application should quit
     */
    @FXML
    public void exitAction() {
        logger.info("Exiting the application");

        System.exit(0);
    }

    /**
     * Method that is called when the SettingsWindow should be opened
     */
    @FXML
    public void settingsAction() {

    }

    /**
     * Method that is called when the help file should be opened
     */
    @FXML
    public void helpAction() {
        logger.info("Attempting to open the help file");

        try {
            helpUtils.openFileFromResources("help.pdf", "/documents/help.pdf");
        } catch (final IOException ex) {
            logger.error("Error opening the help file", ex);
            FxUtils.showErrorAlert(translationBundle.getString("HelpFileError"), ex.getMessage(), getClass().getResourceAsStream("/images/opal.png"));
        }
    }

    /**
     * Method that is called when the homepage should be opened
     */
    @FXML
    public void homepageAction() {
        logger.info("Opening the CodeDead website");

        helpUtils.openWebsite("https://codedead.com/");
    }

    /**
     * Method that is called when the license should be opened
     */
    @FXML
    public void licenseAction() {
        logger.info("Attempting to open the license file");

        try {
            helpUtils.openFileFromResources("license.pdf", "/documents/license.pdf");
        } catch (final IOException ex) {
            logger.error("Error opening the license file", ex);
            FxUtils.showErrorAlert(translationBundle.getString("LicenseFileError"), ex.getMessage(), getClass().getResourceAsStream("/images/opal.png"));
        }
    }

    /**
     * Method that is called when the donate website should be opened
     */
    @FXML
    public void donateAction() {
        logger.info("Opening the CodeDead donation website");

        helpUtils.openWebsite("https://codedead.com/?page_id=302");
    }

    /**
     * Method that is called when the AboutWindow should be opened
     */
    @FXML
    public void aboutAction() {
        logger.info("Attempting to open the AboutWindow");

        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/windows/AboutWindow.fxml"), translationBundle);
            final Parent root = loader.load();

            final AboutWindowController aboutWindowController = loader.getController();
            aboutWindowController.setSettingsController(getSettingsController());

            final Stage primaryStage = new Stage();

            primaryStage.setTitle(translationBundle.getString("AboutWindowTitle"));
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/opal.png")));
            primaryStage.setScene(new Scene(root));

            logger.info("Showing the AboutWindow");
            primaryStage.show();
        } catch (final IOException | NumberFormatException ex) {
            logger.error("Unable to open the AboutWindow", ex);
            FxUtils.showErrorAlert(translationBundle.getString("AboutWindowError"), ex.getMessage(), getClass().getResourceAsStream("/images/opal.png"));
        }
    }
}
