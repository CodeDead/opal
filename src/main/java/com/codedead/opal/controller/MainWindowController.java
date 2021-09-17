package com.codedead.opal.controller;

import com.codedead.opal.domain.PlatformUpdate;
import com.codedead.opal.domain.SoundPane;
import com.codedead.opal.interfaces.IRunnableHelper;
import com.codedead.opal.utils.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
import java.util.*;

public final class MainWindowController {

    private final Logger logger;

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
    private SoundPane snpClock;
    @FXML
    private SoundPane snpBird;
    @FXML
    private SoundPane snpThunder;
    @FXML
    private SoundPane snpWind;
    @FXML
    private SoundPane snpRain;
    @FXML
    private SoundPane snpRiver;
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

    private final String platformName;
    private final AudioController audioController;
    private final HelpUtils helpUtils;

    private SettingsController settingsController;
    private UpdateController updateController;
    private ResourceBundle translationBundle;
    private boolean isPortable;

    /**
     * Initialize a new MainWindowController
     *
     * @throws URISyntaxException When the URI could not be formed
     */
    public MainWindowController() throws URISyntaxException {
        logger = LogManager.getLogger(MainWindowController.class);
        logger.info("Initializing new MainWindowController object");

        platformName = System.getProperty("os.name");
        audioController = new AudioController();
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
     * Set the controllers
     *
     * @param settingsController The {@link SettingsController} object
     * @param updateController   The {@link UpdateController} object
     */
    public void setControllers(final SettingsController settingsController, final UpdateController updateController) {
        if (settingsController == null)
            throw new NullPointerException("SettingsController cannot be null!");
        if (updateController == null)
            throw new NullPointerException("UpdateController cannot be null!");

        this.settingsController = settingsController;
        this.updateController = updateController;

        final Properties properties = settingsController.getProperties();

        final String languageTag = properties.getProperty("locale", "en-US");

        final boolean shouldUpdate = Boolean.parseBoolean(properties.getProperty("autoUpdate", "true"));
        isPortable = Boolean.parseBoolean(properties.getProperty("portable", "false"));

        logger.info("Attempting to load the ResourceBundle for locale {}", languageTag);

        final Locale locale = Locale.forLanguageTag(languageTag);
        translationBundle = ResourceBundle.getBundle("translations.OpalApplication", locale);

        if (shouldUpdate) {
            checkForUpdates(false);
        }
    }

    /**
     * Check for application updates
     *
     * @param showNoUpdates Show an {@link Alert} object when no updates are available
     */
    private void checkForUpdates(final boolean showNoUpdates) {
        logger.info("Attempting to check for updates");

        try {
            final Optional<PlatformUpdate> platformUpdate = updateController.checkForUpdates(platformName, isPortable);
            if (platformUpdate.isPresent()) {
                final PlatformUpdate update = platformUpdate.get();

                logger.info("Version {}.{}.{}.{} is available", update.getMajorVersion(), update.getMinorVersion(), update.getBuildVersion(), update.getRevisionVersion());

                if (FxUtils.showConfirmationAlert(translationBundle
                                .getString("NewUpdateAvailable")
                                .replace("{v}", String.format("%1$s.%2$s.%3$s.%4$s", update.getMajorVersion(), update.getMinorVersion(), update.getBuildVersion(), update.getRevisionVersion())),
                        getClass().getResourceAsStream(SharedVariables.ICON_URL))) {

                    final String extension = update.getDownloadUrl().substring(update.getDownloadUrl().lastIndexOf('.')).toLowerCase();
                    final FileChooser fileChooser = new FileChooser();
                    final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                            String.format("%1$s (*%2$s)", extension.substring(1).toUpperCase(), extension),
                            String.format("*%s", extension));

                    fileChooser.getExtensionFilters().add(extFilter);

                    final File file = fileChooser.showSaveDialog(new Stage());

                    if (file != null) {
                        String filePath = file.getAbsolutePath();
                        if (!filePath.toLowerCase().contains(extension)) {
                            filePath += extension;
                        }

                        updateController.downloadFile(update.getDownloadUrl(), filePath);
                        openFile(filePath);
                    }
                }
            } else {
                logger.info("No updates available");
                if (showNoUpdates) {
                    final Alert alert = new Alert(Alert.AlertType.INFORMATION, translationBundle.getString("NoUpdateAvailable"), ButtonType.OK);
                    alert.showAndWait();
                }
            }
        } catch (final Exception ex) {
            logger.error("Unable to check for updates", ex);
            FxUtils.showErrorAlert(translationBundle.getString("UpdateError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Open a file on the local filesystem
     *
     * @param path The path of the file that should be opened
     */
    private void openFile(final String path) {
        if (path == null)
            throw new NullPointerException("Path cannot be null!");
        if (path.isEmpty())
            throw new IllegalArgumentException("Path cannot be empty!");

        try {
            helpUtils.openFile(new RunnableFileOpener(path, new IRunnableHelper() {
                @Override
                public void executed() {
                    Platform.runLater(() -> logger.info("Successfully opened the file"));
                }

                @Override
                public void exceptionOccurred(final Exception ex) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            logger.error("Error opening the file", ex);
                            FxUtils.showErrorAlert(translationBundle.getString("FileExecutionError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
                        }
                    });
                }
            }));
        } catch (final IOException ex) {
            logger.error("Error opening the file", ex);
            FxUtils.showErrorAlert(translationBundle.getString("FileExecutionError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Get the {@link UpdateController} object
     *
     * @return The {@link UpdateController} object
     */
    public UpdateController getUpdateController() {
        return updateController;
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

        snpRain.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> audioController.setPlayerVolume("rain", newValue.doubleValue() / 100));
        snpWind.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> audioController.setPlayerVolume("wind", newValue.doubleValue() / 100));
        snpThunder.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> audioController.setPlayerVolume("thunder", newValue.doubleValue() / 100));
        snpBird.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> audioController.setPlayerVolume("birds", newValue.doubleValue() / 100));
        snpRiver.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> audioController.setPlayerVolume("river", newValue.doubleValue() / 100));
        snpTyping.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> audioController.setPlayerVolume("keyboard", newValue.doubleValue() / 100));
        snpTelephone.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> audioController.setPlayerVolume("telephone", newValue.doubleValue() / 100));
        snpChatter.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> audioController.setPlayerVolume("officeChatter", newValue.doubleValue() / 100));
        snpTraffic.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> audioController.setPlayerVolume("traffic", newValue.doubleValue() / 100));
        snpClock.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> audioController.setPlayerVolume("clock", newValue.doubleValue() / 100));
        snpFireplace.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> audioController.setPlayerVolume("fireplace", newValue.doubleValue() / 100));
    }

    /**
     * Open a sound preset
     */
    @FXML
    private void openSoundPresetAction() {
        logger.info("Attempting to open a sound preset");
        final FileChooser chooser = new FileChooser();

        final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON (*.json)", "*.json");
        chooser.getExtensionFilters().add(extFilter);

        final File file = chooser.showOpenDialog(new Stage());

        if (file != null && file.exists()) {
            try {
                audioController.loadSoundPreset(file.getAbsolutePath());
                for (final Map.Entry<String, Double> entry : audioController.getVolumes()) {
                    switch (entry.getKey()) {
                        case "rain" -> snpRain.getSlider().setValue(entry.getValue() * 100);
                        case "wind" -> snpWind.getSlider().setValue(entry.getValue() * 100);
                        case "thunder" -> snpThunder.getSlider().setValue(entry.getValue() * 100);
                        case "birds" -> snpBird.getSlider().setValue(entry.getValue() * 100);
                        case "river" -> snpRiver.getSlider().setValue(entry.getValue() * 100);
                        case "keyboard" -> snpTyping.getSlider().setValue(entry.getValue() * 100);
                        case "telephone" -> snpTelephone.getSlider().setValue(entry.getValue() * 100);
                        case "officeChatter" -> snpChatter.getSlider().setValue(entry.getValue() * 100);
                        case "traffic" -> snpTraffic.getSlider().setValue(entry.getValue() * 100);
                        case "fireplace" -> snpFireplace.getSlider().setValue(entry.getValue() * 100);
                        default -> logger.info("Unknown key found: {}", entry.getKey());
                    }
                }
            } catch (final IOException ex) {
                logger.error("Unable to open the sound preset from {}", file.getAbsolutePath(), ex);
                FxUtils.showErrorAlert(translationBundle.getString("OpenSoundPresetError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            }
        } else {
            logger.info("Cancelled opening a sound preset");
        }
    }

    /**
     * Save the sound settings to disk
     */
    @FXML
    private void saveSoundPresetAction() {
        logger.info("Attempting to save sound settings");
        final FileChooser fileChooser = new FileChooser();

        final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        final File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            String filePath = file.getAbsolutePath();
            try {
                if (!filePath.toLowerCase().contains(".json")) {
                    filePath += ".json";
                }
                audioController.saveSoundPreset(filePath);
            } catch (IOException ex) {
                logger.error("Unable to save the sound settings to {}", filePath, ex);
                FxUtils.showErrorAlert(translationBundle.getString("SaveSoundPresetError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            }
        } else {
            logger.info("Cancelled saving a sound settings");
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
        snpBird.getSlider().setValue(0);
        snpRiver.getSlider().setValue(0);
        snpThunder.getSlider().setValue(0);
        snpTyping.getSlider().setValue(0);
        snpTelephone.getSlider().setValue(0);
        snpChatter.getSlider().setValue(0);
        snpTraffic.getSlider().setValue(0);
        snpClock.getSlider().setValue(0);
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
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(SharedVariables.ICON_URL)));
            primaryStage.setScene(new Scene(root));

            logger.info("Showing the SettingsWindow");
            primaryStage.show();
        } catch (final IOException | NumberFormatException ex) {
            logger.error("Unable to open the SettingsWindow", ex);
            FxUtils.showErrorAlert(translationBundle.getString("SettingsWindowError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
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
                public void executed() {
                    Platform.runLater(() -> logger.info("Successfully opened the help file"));
                }

                @Override
                public void exceptionOccurred(final Exception ex) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            logger.error("Error opening the help file", ex);
                            FxUtils.showErrorAlert(translationBundle.getString("HelpFileError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
                        }
                    });
                }
            }), "/documents/help.pdf");
        } catch (final IOException ex) {
            logger.error("Error opening the help file", ex);
            FxUtils.showErrorAlert(translationBundle.getString("HelpFileError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
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

    /**
     * Method that is called when the license should be opened
     */
    @FXML
    private void licenseAction() {
        logger.info("Attempting to open the license file");

        try {
            helpUtils.openFileFromResources(new RunnableFileOpener("license.pdf", new IRunnableHelper() {
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
            }), "/documents/license.pdf");
        } catch (final IOException ex) {
            logger.error("Error opening the license file", ex);
            FxUtils.showErrorAlert(translationBundle.getString("LicenseFileError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Method that is called when the donation website should be opened
     */
    @FXML
    private void donateAction() {
        logger.info("Opening the CodeDead donation website");

        final RunnableSiteOpener runnableSiteOpener = new RunnableSiteOpener("https://codedead.com/donate", new IRunnableHelper() {
            @Override
            public void executed() {
                Platform.runLater(() -> logger.info("Successfully opened website"));
            }

            @Override
            public void exceptionOccurred(final Exception ex) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        logger.error("Error opening the CodeDead donation website", ex);
                        FxUtils.showErrorAlert(translationBundle.getString("WebsiteError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
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
            FxUtils.showErrorAlert(translationBundle.getString("AboutWindowError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Method that is called when the user wants to check for updates
     */
    @FXML
    private void updateAction() {
        checkForUpdates(true);
    }
}
