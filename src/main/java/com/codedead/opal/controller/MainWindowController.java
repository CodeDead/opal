package com.codedead.opal.controller;

import com.codedead.opal.domain.InvalidHttpResponseCodeException;
import com.codedead.opal.domain.OsCheck;
import com.codedead.opal.domain.PlatformUpdate;
import com.codedead.opal.domain.SoundPane;
import com.codedead.opal.interfaces.IAudioTimer;
import com.codedead.opal.interfaces.IRunnableHelper;
import com.codedead.opal.utils.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static com.codedead.opal.utils.SharedVariables.DEFAULT_LOCALE;

public final class MainWindowController implements IAudioTimer {

    @FXML
    private SoundPane snpGong;
    @FXML
    private SoundPane snpDrumTribal;
    @FXML
    private SoundPane snpSleepy;
    @FXML
    private SoundPane snpRugbyFootball;
    @FXML
    private SoundPane snpTribal;
    @FXML
    private SoundPane snpNetworkingEvent;
    @FXML
    private SoundPane snpZoo;
    @FXML
    private SoundPane snpCoffee;
    @FXML
    private SoundPane snpZen;
    @FXML
    private SoundPane snpFrogs;
    @FXML
    private GridPane grpMain;
    @FXML
    private SoundPane snpCave;
    @FXML
    private SoundPane snpFan;
    @FXML
    private SoundPane snpFantasy;
    @FXML
    private CheckMenuItem mniTimerEnabled;
    @FXML
    private SoundPane snpStatic;
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
    private Menu mnuTimer;
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

    private final Logger logger;

    /**
     * Initialize a new MainWindowController
     *
     * @throws URISyntaxException When the URI could not be formed
     */
    public MainWindowController() throws URISyntaxException {
        logger = LogManager.getLogger(MainWindowController.class);
        logger.info("Initializing new MainWindowController object");

        platformName = OsCheck.getOperatingSystemType().name();
        audioController = new AudioController(this);
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

        final String languageTag = properties.getProperty("locale", DEFAULT_LOCALE);
        final boolean shouldUpdate = Boolean.parseBoolean(properties.getProperty("autoUpdate", "true"));

        logger.info("Attempting to load the ResourceBundle for locale {}", languageTag);

        final Locale locale = Locale.forLanguageTag(languageTag);
        translationBundle = ResourceBundle.getBundle("translations.OpalApplication", locale);

        final boolean mediaButtons = Boolean.parseBoolean(properties.getProperty("mediaButtons", "true"));

        if (!mediaButtons) {
            loadMediaButtonVisibility(false);
        }

        if (shouldUpdate) {
            checkForUpdates(false);
        }
    }

    /**
     * Load the media button visibility for all {@link SoundPane} objects
     *
     * @param visible True if the media button should be visible, otherwise false
     */
    public void loadMediaButtonVisibility(final boolean visible) {
        snpRain.setMediaButton(visible);
        snpWind.setMediaButton(visible);
        snpThunder.setMediaButton(visible);
        snpBird.setMediaButton(visible);
        snpRiver.setMediaButton(visible);
        snpTyping.setMediaButton(visible);
        snpTelephone.setMediaButton(visible);
        snpChatter.setMediaButton(visible);
        snpTraffic.setMediaButton(visible);
        snpClock.setMediaButton(visible);
        snpFireplace.setMediaButton(visible);
        snpStatic.setMediaButton(visible);
        snpFantasy.setMediaButton(visible);
        snpFan.setMediaButton(visible);
        snpCave.setMediaButton(visible);
        snpFrogs.setMediaButton(visible);
        snpZen.setMediaButton(visible);
        snpCoffee.setMediaButton(visible);
        snpZoo.setMediaButton(visible);
        snpSleepy.setMediaButton(visible);
        snpGong.setMediaButton(visible);
        snpNetworkingEvent.setMediaButton(visible);
        snpTribal.setMediaButton(visible);
        snpDrumTribal.setMediaButton(visible);
        snpRugbyFootball.setMediaButton(visible);
    }

    /**
     * Check for application updates
     *
     * @param showNoUpdates Show an {@link Alert} object when no updates are available
     */
    private void checkForUpdates(final boolean showNoUpdates) {
        logger.info("Attempting to check for updates");

        try {
            final Optional<PlatformUpdate> platformUpdate = updateController.checkForUpdates(platformName, SharedVariables.PORTABLE);
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
                    FxUtils.showInformationAlert(translationBundle.getString("NoUpdateAvailable"), null);
                }
            }
        } catch (final InterruptedException ex) {
            logger.error("Unable to check for updates", ex);
            FxUtils.showErrorAlert(translationBundle.getString("UpdateError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            Thread.currentThread().interrupt();
        } catch (final IOException | InvalidHttpResponseCodeException ex) {
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

        mniOpenSoundPreset.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/open.png")))));
        mniSaveSoundPreset.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/save.png")))));
        mniReset.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/refresh.png")))));
        mniExit.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/remove.png")))));
        mniSettings.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/settings.png")))));
        mniAbout.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/about.png")))));
        mniDonate.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/donate.png")))));
        mniLicense.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/license.png")))));
        mniHomepage.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/home.png")))));
        mniUpdate.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/update.png")))));
        mniHelp.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/help.png")))));
        mnuTimer.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/timer.png")))));

        snpRain.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpRain.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("rain", newVolume);
        });
        snpRain.getBtnPlayPause().setOnAction(event -> {
            if (snpRain.isPlaying()) {
                audioController.stopMedia("rain");
            } else {
                audioController.playMedia("rain");
            }
            snpRain.setPlaying(!snpRain.isPlaying());
        });

        snpWind.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpWind.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("wind", newVolume);
        });
        snpWind.getBtnPlayPause().setOnAction(event -> {
            if (snpWind.isPlaying()) {
                audioController.stopMedia("wind");
            } else {
                audioController.playMedia("wind");
            }
            snpWind.setPlaying(!snpWind.isPlaying());
        });

        snpThunder.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpThunder.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("thunder", newVolume);
        });
        snpThunder.getBtnPlayPause().setOnAction(event -> {
            if (snpThunder.isPlaying()) {
                audioController.stopMedia("thunder");
            } else {
                audioController.playMedia("thunder");
            }
            snpThunder.setPlaying(!snpThunder.isPlaying());
        });

        snpBird.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpBird.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("birds", newVolume);
        });
        snpBird.getBtnPlayPause().setOnAction(event -> {
            if (snpBird.isPlaying()) {
                audioController.stopMedia("birds");
            } else {
                audioController.playMedia("birds");
            }
            snpBird.setPlaying(!snpBird.isPlaying());
        });

        snpRiver.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpRiver.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("river", newVolume);
        });
        snpRiver.getBtnPlayPause().setOnAction(event -> {
            if (snpRiver.isPlaying()) {
                audioController.stopMedia("river");
            } else {
                audioController.playMedia("river");
            }
            snpRiver.setPlaying(!snpRiver.isPlaying());
        });

        snpTyping.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpTyping.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("keyboard", newVolume);
        });
        snpTyping.getBtnPlayPause().setOnAction(event -> {
            if (snpTyping.isPlaying()) {
                audioController.stopMedia("keyboard");
            } else {
                audioController.playMedia("keyboard");
            }
            snpTyping.setPlaying(!snpTyping.isPlaying());
        });

        snpTelephone.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpTelephone.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("telephone", newVolume);
        });
        snpTelephone.getBtnPlayPause().setOnAction(event -> {
            if (snpTelephone.isPlaying()) {
                audioController.stopMedia("telephone");
            } else {
                audioController.playMedia("telephone");
            }
            snpTelephone.setPlaying(!snpTelephone.isPlaying());
        });

        snpChatter.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpChatter.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("officeChatter", newVolume);
        });
        snpChatter.getBtnPlayPause().setOnAction(event -> {
            if (snpChatter.isPlaying()) {
                audioController.stopMedia("officeChatter");
            } else {
                audioController.playMedia("officeChatter");
            }
            snpChatter.setPlaying(!snpChatter.isPlaying());
        });

        snpTraffic.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpTraffic.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("traffic", newVolume);
        });
        snpTraffic.getBtnPlayPause().setOnAction(event -> {
            if (snpTraffic.isPlaying()) {
                audioController.stopMedia("traffic");
            } else {
                audioController.playMedia("traffic");
            }
            snpTraffic.setPlaying(!snpTraffic.isPlaying());
        });

        snpClock.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpClock.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("clock", newVolume);
        });
        snpClock.getBtnPlayPause().setOnAction(event -> {
            if (snpClock.isPlaying()) {
                audioController.stopMedia("clock");
            } else {
                audioController.playMedia("clock");
            }
            snpClock.setPlaying(!snpClock.isPlaying());
        });

        snpFireplace.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpFireplace.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("fireplace", newVolume);
        });
        snpFireplace.getBtnPlayPause().setOnAction(event -> {
            if (snpFireplace.isPlaying()) {
                audioController.stopMedia("fireplace");
            } else {
                audioController.playMedia("fireplace");
            }
            snpFireplace.setPlaying(!snpFireplace.isPlaying());
        });

        snpStatic.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpStatic.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("static", newVolume);
        });
        snpStatic.getBtnPlayPause().setOnAction(event -> {
            if (snpStatic.isPlaying()) {
                audioController.stopMedia("static");
            } else {
                audioController.playMedia("static");
            }
            snpStatic.setPlaying(!snpStatic.isPlaying());
        });

        snpFantasy.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpFantasy.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("fantasy", newVolume);
        });
        snpFantasy.getBtnPlayPause().setOnAction(event -> {
            if (snpFantasy.isPlaying()) {
                audioController.stopMedia("fantasy");
            } else {
                audioController.playMedia("fantasy");
            }
            snpFantasy.setPlaying(!snpFantasy.isPlaying());
        });

        snpFan.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpFan.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("fan", newVolume);
        });
        snpFan.getBtnPlayPause().setOnAction(event -> {
            if (snpFan.isPlaying()) {
                audioController.stopMedia("fan");
            } else {
                audioController.playMedia("fan");
            }
            snpFan.setPlaying(!snpFan.isPlaying());
        });

        snpCave.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpCave.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("cave", newVolume);
        });
        snpCave.getBtnPlayPause().setOnAction(event -> {
            if (snpCave.isPlaying()) {
                audioController.stopMedia("cave");
            } else {
                audioController.playMedia("cave");
            }
            snpCave.setPlaying(!snpCave.isPlaying());
        });

        snpFrogs.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpFrogs.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("frogs", newVolume);
        });
        snpFrogs.getBtnPlayPause().setOnAction(event -> {
            if (snpFrogs.isPlaying()) {
                audioController.stopMedia("frogs");
            } else {
                audioController.playMedia("frogs");
            }
            snpFrogs.setPlaying(!snpFrogs.isPlaying());
        });

        snpZen.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpZen.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("zen", newVolume);
        });
        snpZen.getBtnPlayPause().setOnAction(event -> {
            if (snpZen.isPlaying()) {
                audioController.stopMedia("zen");
            } else {
                audioController.playMedia("zen");
            }
            snpZen.setPlaying(!snpZen.isPlaying());
        });

        snpCoffee.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpCoffee.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("coffee", newVolume);
        });
        snpCoffee.getBtnPlayPause().setOnAction(event -> {
            if (snpCoffee.isPlaying()) {
                audioController.stopMedia("coffee");
            } else {
                audioController.playMedia("coffee");
            }
            snpCoffee.setPlaying(!snpCoffee.isPlaying());
        });

        snpZoo.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpZoo.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("zoo", newVolume);
        });
        snpZoo.getBtnPlayPause().setOnAction(event -> {
            if (snpZoo.isPlaying()) {
                audioController.stopMedia("zoo");
            } else {
                audioController.playMedia("zoo");
            }
            snpZoo.setPlaying(!snpZoo.isPlaying());
        });

        snpSleepy.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpSleepy.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("sleepy", newVolume);
        });
        snpSleepy.getBtnPlayPause().setOnAction(event -> {
            if (snpSleepy.isPlaying()) {
                audioController.stopMedia("sleepy");
            } else {
                audioController.playMedia("sleepy");
            }
            snpSleepy.setPlaying(!snpSleepy.isPlaying());
        });

        snpGong.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpGong.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("gong", newVolume);
        });
        snpGong.getBtnPlayPause().setOnAction(event -> {
            if (snpGong.isPlaying()) {
                audioController.stopMedia("gong");
            } else {
                audioController.playMedia("gong");
            }
            snpGong.setPlaying(!snpGong.isPlaying());
        });

        // Audiences
        snpNetworkingEvent.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpNetworkingEvent.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("networking", newVolume);
        });
        snpNetworkingEvent.getBtnPlayPause().setOnAction(event -> {
            if (snpNetworkingEvent.isPlaying()) {
                audioController.stopMedia("networking");
            } else {
                audioController.playMedia("networking");
            }
            snpNetworkingEvent.setPlaying(!snpNetworkingEvent.isPlaying());
        });

        snpTribal.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpTribal.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("tribal", newVolume);
        });
        snpTribal.getBtnPlayPause().setOnAction(event -> {
            if (snpTribal.isPlaying()) {
                audioController.stopMedia("tribal");
            } else {
                audioController.playMedia("tribal");
            }
            snpTribal.setPlaying(!snpTribal.isPlaying());
        });

        snpDrumTribal.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpDrumTribal.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("drumtribal", newVolume);
        });
        snpDrumTribal.getBtnPlayPause().setOnAction(event -> {
            if (snpDrumTribal.isPlaying()) {
                audioController.stopMedia("drumtribal");
            } else {
                audioController.playMedia("drumtribal");
            }
            snpDrumTribal.setPlaying(!snpDrumTribal.isPlaying());
        });

        snpRugbyFootball.getSlider().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            final double newVolume = newValue.doubleValue() / 100;
            snpRugbyFootball.setPlaying(newVolume != 0);
            audioController.setPlayerVolume("football", newVolume);
        });
        snpRugbyFootball.getBtnPlayPause().setOnAction(event -> {
            if (snpRugbyFootball.isPlaying()) {
                audioController.stopMedia("football");
            } else {
                audioController.playMedia("football");
            }
            snpRugbyFootball.setPlaying(!snpRugbyFootball.isPlaying());
        });

        mniTimerEnabled.setOnAction(e ->
        {
            if (mniTimerEnabled.isSelected()) {
                final Properties properties = settingsController.getProperties();
                final long timerDelay = Long.parseLong(properties.getProperty("timerDelay", "3600000"));

                audioController.scheduleTimer(timerDelay);
            } else {
                audioController.cancelTimer();
            }
        });
    }

    /**
     * Open a sound preset
     */
    @FXML
    private void openSoundPresetAction() {
        logger.info("Opening a sound preset");
        final FileChooser chooser = new FileChooser();

        final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON (*.json)", "*.json");
        chooser.getExtensionFilters().add(extFilter);

        final File file = chooser.showOpenDialog(new Stage());

        if (file != null && file.exists()) {
            openSoundPreset(file.getAbsolutePath());
        }
    }

    /**
     * Open a sound preset
     *
     * @param filePath The absolute path of the sound preset file
     */
    private void openSoundPreset(final String filePath) {
        try {
            audioController.loadSoundPreset(filePath);
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
                    case "static" -> snpStatic.getSlider().setValue(entry.getValue() * 100);
                    case "fantasy" -> snpFantasy.getSlider().setValue(entry.getValue() * 100);
                    case "fan" -> snpFan.getSlider().setValue(entry.getValue() * 100);
                    case "clock" -> snpClock.getSlider().setValue(entry.getValue() * 100);
                    case "cave" -> snpCave.getSlider().setValue(entry.getValue() * 100);
                    case "frogs" -> snpFrogs.getSlider().setValue(entry.getValue() * 100);
                    case "zen" -> snpZen.getSlider().setValue(entry.getValue() * 100);
                    case "coffee" -> snpCoffee.getSlider().setValue(entry.getValue() * 100);
                    case "zoo" -> snpZoo.getSlider().setValue(entry.getValue() * 100);
                    case "networking" -> snpNetworkingEvent.getSlider().setValue(entry.getValue() * 100);
                    case "tribal" -> snpTribal.getSlider().setValue(entry.getValue() * 100);
                    case "football" -> snpRugbyFootball.getSlider().setValue(entry.getValue() * 100);
                    case "sleepy" -> snpSleepy.getSlider().setValue(entry.getValue() * 100);
                    case "drumtribal" -> snpDrumTribal.getSlider().setValue(entry.getValue() * 100);
                    case "gong" -> snpGong.getSlider().setValue(entry.getValue() * 100);
                    default -> logger.info("Unknown key found: {}", entry.getKey());
                }
            }
        } catch (final IOException ex) {
            logger.error("Unable to open the sound preset from {}", filePath, ex);
            FxUtils.showErrorAlert(translationBundle.getString("OpenSoundPresetError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
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
            } catch (final IOException ex) {
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
        snpStatic.getSlider().setValue(0);
        snpFantasy.getSlider().setValue(0);
        snpFan.getSlider().setValue(0);
        snpCave.getSlider().setValue(0);
        snpFrogs.getSlider().setValue(0);
        snpZen.getSlider().setValue(0);
        snpCoffee.getSlider().setValue(0);
        snpZoo.getSlider().setValue(0);
        snpNetworkingEvent.getSlider().setValue(0);
        snpTribal.getSlider().setValue(0);
        snpRugbyFootball.getSlider().setValue(0);
        snpSleepy.getSlider().setValue(0);
        snpDrumTribal.getSlider().setValue(0);
        snpGong.getSlider().setValue(0);
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
            settingsWindowController.setMainWindowController(this);

            final Stage primaryStage = new Stage();

            primaryStage.setTitle(translationBundle.getString("SettingsWindowTitle"));
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(SharedVariables.ICON_URL))));
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
            helpUtils.openFileFromResources(new RunnableFileOpener(SharedVariables.HELP_DOCUMENTATION_FILE_LOCATION, new IRunnableHelper() {
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
            }), SharedVariables.HELP_DOCUMENTATION_RESOURCE_LOCATION);
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
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/opal.png"))));
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

    /**
     * Method that is called when the {@link AudioController} object's {@link Timer} object has fired
     */
    @Override
    public void fired() {
        resetAction();
        mniTimerEnabled.setSelected(false);

        if (Boolean.parseBoolean(settingsController.getProperties().getProperty("timerApplicationShutdown", "false"))) {
            exitAction();
        }
    }

    /**
     * Method that is invoked when the {@link AudioController} object's {@link Timer} object has cancelled
     */
    @Override
    public void cancelled() {
        mniTimerEnabled.setSelected(false);
    }

    /**
     * Method that is invoked when a drag-over event is occurring
     *
     * @param dragEvent The {@link DragEvent} object
     */
    @FXML
    private void onDragOver(final DragEvent dragEvent) {
        if (dragEvent.getGestureSource() != grpMain && dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        dragEvent.consume();
    }

    /**
     * Method that is invoked when a drag-drop event occurred
     *
     * @param dragEvent The {@link DragEvent} object
     */
    @FXML
    private void onDragDropped(final DragEvent dragEvent) {
        final Dragboard db = dragEvent.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            openSoundPreset(db.getFiles().get(0).getAbsolutePath());
            success = true;
        }

        dragEvent.setDropCompleted(success);
        dragEvent.consume();
    }
}
