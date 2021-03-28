package com.codedead.opal.controller;

import com.codedead.opal.domain.SoundPane;
import com.codedead.opal.domain.SoundPreset;
import com.codedead.opal.interfaces.IRunnableHelper;
import com.codedead.opal.utils.FxUtils;
import com.codedead.opal.utils.HelpUtils;
import com.codedead.opal.utils.RunnableFileOpener;
import com.codedead.opal.utils.RunnableSiteOpener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
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
    private SoundPane snpBird;
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
    @FXML
    private MenuItem mniSaveSoundPreset;
    @FXML
    private MenuItem mniOpenSoundPreset;

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
        logger.info("Initializing MainWindow");

        mniOpenSoundPreset.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/open.png"))));
        mniSaveSoundPreset.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/save.png"))));
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

        snpBird.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.doubleValue() == 0) {
                audioController.stopBirds();
            } else {
                audioController.setBirdsVolume(newValue.doubleValue() / 100);
                audioController.playBirds();
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
     * Open a {@link com.codedead.opal.domain.SoundPreset} object
     */
    @FXML
    private void openSoundPresetAction() {
        logger.info("Attempting to open a SoundPreset");
        final FileChooser chooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON (*.json)", "*.json");
        chooser.getExtensionFilters().add(extFilter);

        final File file = chooser.showOpenDialog(new Stage());

        if (file != null && file.exists()) {
            try {
                final SoundPreset soundPreset = audioController.loadSoundPreset(file.getAbsolutePath());

                if (soundPreset != null) {
                    snpRain.getSlider().setValue(soundPreset.getRainVolume() * 100);
                    snpWind.getSlider().setValue(soundPreset.getWindVolume() * 100);
                    snpThunder.getSlider().setValue(soundPreset.getThunderVolume() * 100);
                    snpBird.getSlider().setValue(soundPreset.getBirdsVolume() * 100);
                    snpTyping.getSlider().setValue(soundPreset.getKeyboardVolume() * 100);
                    snpTelephone.getSlider().setValue(soundPreset.getPhoneVolume() * 100);
                    snpChatter.getSlider().setValue(soundPreset.getChatterVolume() * 100);
                    snpTraffic.getSlider().setValue(soundPreset.getTrafficVolume() * 100);
                    snpFireplace.getSlider().setValue(soundPreset.getFirePlaceVolume() * 100);

                    audioController.setSoundPreset(soundPreset);
                }
            } catch (final IOException ex) {
                logger.error(String.format("Unable to open the SoundPreset from %s", file.getAbsolutePath()), ex);
                FxUtils.showErrorAlert(translationBundle.getString("OpenSoundPresetError"), ex.getMessage(), getClass().getResourceAsStream("/images/opal.png"));
            }
        } else {
            logger.info("Cancelled opening a SoundPreset");
        }
    }

    /**
     * Save the {@link com.codedead.opal.domain.SoundPreset} object to disk
     */
    @FXML
    private void saveSoundPresetAction() {
        logger.info("Attempting to save a SoundPreset");
        final FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        final File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            String filePath = file.getAbsolutePath();
            try {
                if (!filePath.toLowerCase().contains(".json")) {
                    filePath += ".json";
                }
                audioController.saveSoundPreset(audioController.getSoundPreset(), filePath);
            } catch (IOException ex) {
                logger.error(String.format("Unable to save the SoundPreset to %s", filePath), ex);
                FxUtils.showErrorAlert(translationBundle.getString("SaveSoundPresetError"), ex.getMessage(), getClass().getResourceAsStream("/images/opal.png"));
            }
        } else {
            logger.info("Cancelled saving a SoundPreset");
        }
    }

    /**
     * Method that is called when all players should be reset
     */
    @FXML
    private void resetAction() {
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
    private void exitAction() {
        logger.info("Exiting the application");

        System.exit(0);
    }

    /**
     * Method that is called when the SettingsWindow should be opened
     */
    @FXML
    private void settingsAction() {
        logger.info("Attempting to open the SettingsWindow");

        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/windows/SettingsWindow.fxml"), translationBundle);
            final Parent root = loader.load();

            final SettingsWindowController settingsWindowController = loader.getController();
            settingsWindowController.setSettingsController(getSettingsController());

            final Stage primaryStage = new Stage();

            primaryStage.setTitle(translationBundle.getString("SettingsWindowTitle"));
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/opal.png")));
            primaryStage.setScene(new Scene(root));

            logger.info("Showing the SettingsWindow");
            primaryStage.show();
        } catch (final IOException | NumberFormatException ex) {
            logger.error("Unable to open the SettingsWindow", ex);
            FxUtils.showErrorAlert(translationBundle.getString("SettingsWindowError"), ex.getMessage(), getClass().getResourceAsStream("/images/opal.png"));
        }
    }

    /**
     * Method that is called when the help file should be opened
     */
    @FXML
    private void helpAction() {
        logger.info("Attempting to open the help file");

        try {
            helpUtils.openFileFromResources(new RunnableFileOpener("help.pdf", new IRunnableHelper() {
                @Override
                public final void executed() {
                    Platform.runLater(() -> logger.info("Successfully opened the help file"));
                }

                @Override
                public final void exceptionOccurred(final Exception ex) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public final void run() {
                            logger.error("Error opening the help file", ex);
                            FxUtils.showErrorAlert(translationBundle.getString("HelpFileError"), ex.getMessage(), getClass().getResourceAsStream("/images/opal.png"));
                        }
                    });
                }
            }), "/documents/help.pdf");
        } catch (final IOException ex) {
            logger.error("Error opening the help file", ex);
            FxUtils.showErrorAlert(translationBundle.getString("HelpFileError"), ex.getMessage(), getClass().getResourceAsStream("/images/opal.png"));
        }
    }

    /**
     * Method that is called when the homepage should be opened
     */
    @FXML
    private void homepageAction() {
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

    /**
     * Method that is called when the license should be opened
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
     * Method that is called when the donate website should be opened
     */
    @FXML
    private void donateAction() {
        logger.info("Opening the CodeDead donation website");

        final RunnableSiteOpener runnableSiteOpener = new RunnableSiteOpener("https://codedead.com/donate", new IRunnableHelper() {
            @Override
            public final void executed() {
                Platform.runLater(() -> logger.info("Successfully opened website"));
            }

            @Override
            public final void exceptionOccurred(final Exception ex) {
                Platform.runLater(new Runnable() {
                    @Override
                    public final void run() {
                        logger.error("Error opening the CodeDead donation website", ex);
                        FxUtils.showErrorAlert(translationBundle.getString("WebsiteError"), ex.getMessage(), getClass().getResourceAsStream("/images/opal.png"));
                    }
                });
            }
        });

        new Thread(runnableSiteOpener).start();
    }

    /**
     * Method that is called when the AboutWindow should be opened
     */
    @FXML
    private void aboutAction() {
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
