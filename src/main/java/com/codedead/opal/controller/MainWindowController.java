package com.codedead.opal.controller;

import com.codedead.opal.domain.*;
import com.codedead.opal.interfaces.IAudioTimer;
import com.codedead.opal.interfaces.IRunnableHelper;
import com.codedead.opal.interfaces.TrayIconListener;
import com.codedead.opal.utils.FxUtils;
import com.codedead.opal.utils.HelpUtils;
import com.codedead.opal.utils.RunnableFileOpener;
import com.codedead.opal.utils.SharedVariables;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.codedead.opal.utils.SharedVariables.DEFAULT_LOCALE;

public final class MainWindowController implements IAudioTimer, TrayIconListener {

    @FXML
    private SoundPane snpFastMetronome;
    @FXML
    private SoundPane snpSlowMetronome;
    @FXML
    private SoundPane snpTrain;
    @FXML
    private SoundPane snpSpace;
    @FXML
    private SoundPane snpBellTower;
    @FXML
    private SoundPane snpGong;
    @FXML
    private SoundPane snpRollerCoaster;
    @FXML
    private SoundPane snpSleepy;
    @FXML
    private SoundPane snpZen;
    @FXML
    private SoundPane snpFantasy;
    @FXML
    private SoundPane snpBrownNoise;
    @FXML
    private SoundPane snpPinkNoise;
    @FXML
    private SoundPane snpWhiteNoise;
    @FXML
    private SoundPane snpStatic;
    @FXML
    private SoundPane snpRugbyFootball;
    @FXML
    private SoundPane snpDrumTribalFestival;
    @FXML
    private SoundPane snpTribalFestival;
    @FXML
    private SoundPane snpRestaurant;
    @FXML
    private SoundPane snpLargeCrowd;
    @FXML
    private SoundPane snpNetworkingEvent;
    @FXML
    private SoundPane snpCoffee;
    @FXML
    private SoundPane snpFan;
    @FXML
    private SoundPane snpClock;
    @FXML
    private SoundPane snpTraffic;
    @FXML
    private SoundPane snpChatter;
    @FXML
    private SoundPane snpPhone;
    @FXML
    private SoundPane snpTyping;
    @FXML
    private SoundPane snpDolphins;
    @FXML
    private SoundPane snpForest;
    @FXML
    private SoundPane snpZoo;
    @FXML
    private SoundPane snpFrogs;
    @FXML
    private SoundPane snpCave;
    @FXML
    private SoundPane snpFireplace;
    @FXML
    private SoundPane snpRiver;
    @FXML
    private SoundPane snpOcean;
    @FXML
    private SoundPane snpSeagulls;
    @FXML
    private SoundPane snpBirds;
    @FXML
    private SoundPane snpThunder;
    @FXML
    private SoundPane snpWind;
    @FXML
    private SoundPane snpRain;
    @FXML
    private MenuItem mniAbout;
    @FXML
    private MenuItem mniDonate;
    @FXML
    private MenuItem mniLicense;
    @FXML
    private MenuItem mniHomePage;
    @FXML
    private MenuItem mniCheckForUpdates;
    @FXML
    private MenuItem mniHelp;
    @FXML
    private Menu mnuHelp;
    @FXML
    private Menu mnuTimer;
    @FXML
    private MenuItem mniSettings;
    @FXML
    private Menu mnuTools;
    @FXML
    private MenuItem mniExit;
    @FXML
    private MenuItem mniReset;
    @FXML
    private MenuItem mniPlayPause;
    @FXML
    private MenuItem mniAddCustomSound;
    @FXML
    private MenuItem mniSaveSoundPreset;
    @FXML
    private MenuItem mniOpenSoundPreset;
    @FXML
    private Menu mnuFile;
    @FXML
    private GridPane grpCustom;
    @FXML
    private TitledPane pneCustom;
    @FXML
    private TitledPane pneOther;
    @FXML
    private TitledPane pneRadioFrequencyStatic;
    @FXML
    private TitledPane pneAudiences;
    @FXML
    private TitledPane pneOffice;
    @FXML
    private TitledPane pneNature;
    @FXML
    private Button btnClearSearch;
    @FXML
    private TextField txtSearch;
    @FXML
    private GridPane grpOther;
    @FXML
    private GridPane grpRadioFrequencyStatic;
    @FXML
    private GridPane grpAudiences;
    @FXML
    private GridPane grpOffice;
    @FXML
    private GridPane grpNature;
    @FXML
    private CheckMenuItem mniTimerEnabled;
    @FXML
    private MenuItem mniCountDown;

    private TrayIconController trayIconController;
    private SettingsController settingsController;
    private UpdateController updateController;
    private TimerTask timerTask;
    private TimerTask countDownTask;
    private final String platformName;
    private final HelpUtils helpUtils;
    private final JsonMapper jsonMapper;
    private final Timer timer;
    private final Timer countDownTimer;
    private final IAudioTimer audioTimer;
    private final Logger logger;
    private final ObservableResourceFactory resourceFactory;

    /**
     * Initialize a new MainWindowController
     */
    public MainWindowController() {
        logger = LogManager.getLogger(MainWindowController.class);
        logger.info("Initializing new MainWindowController object");

        platformName = OsCheck.getOperatingSystemType().name();
        helpUtils = new HelpUtils();

        this.timer = new Timer();
        this.countDownTimer = new Timer();
        this.audioTimer = this;
        this.jsonMapper = JsonMapper.builder().build();

        resourceFactory = new ObservableResourceFactory();
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
        resourceFactory.setResources(ResourceBundle.getBundle("translations.OpalApplication", locale));

        final boolean mediaButtons = Boolean.parseBoolean(properties.getProperty("mediaButtons", "true"));

        trayIconController = new TrayIconController(resourceFactory, this);

        // Load tray icons after displaying the main stage to display the proper icon in the task bar / activities bar (linux)
        if (Boolean.parseBoolean(properties.getProperty("trayIcon", "false"))) {
            try {
                trayIconController.showTrayIcon();
            } catch (final IOException ex) {
                logger.error("Unable to create tray icon", ex);
                FxUtils.showErrorAlert(resourceFactory.getResources().getString("TrayIconError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            }
        }

        if (!mediaButtons) {
            loadMediaButtonVisibility(false);
        }

        if (shouldUpdate) {
            checkForUpdates(false, false);
        }

        setAudioBalance(Double.parseDouble(properties.getProperty("audioBalance", "0.0")));

        // Locale
        mnuFile.textProperty().bind(resourceFactory.getStringBinding("File"));
        mniOpenSoundPreset.textProperty().bind(resourceFactory.getStringBinding("OpenSoundPreset"));
        mniSaveSoundPreset.textProperty().bind(resourceFactory.getStringBinding("SaveSoundPreset"));
        mniAddCustomSound.textProperty().bind(resourceFactory.getStringBinding("AddCustomSound"));
        mniPlayPause.textProperty().bind(resourceFactory.getStringBinding("PlayPause"));
        mniReset.textProperty().bind(resourceFactory.getStringBinding("Reset"));
        mniExit.textProperty().bind(resourceFactory.getStringBinding("Exit"));
        mnuTools.textProperty().bind(resourceFactory.getStringBinding("Tools"));
        mniSettings.textProperty().bind(resourceFactory.getStringBinding("Settings"));
        mnuTimer.textProperty().bind(resourceFactory.getStringBinding("Timer"));
        mniTimerEnabled.textProperty().bind(resourceFactory.getStringBinding("Enabled"));
        mnuHelp.textProperty().bind(resourceFactory.getStringBinding("HelpMenu"));
        mniHelp.textProperty().bind(resourceFactory.getStringBinding("Help"));
        mniCheckForUpdates.textProperty().bind(resourceFactory.getStringBinding("CheckForUpdates"));
        mniHomePage.textProperty().bind(resourceFactory.getStringBinding("Homepage"));
        mniLicense.textProperty().bind(resourceFactory.getStringBinding("License"));
        mniDonate.textProperty().bind(resourceFactory.getStringBinding("Donate"));
        mniAbout.textProperty().bind(resourceFactory.getStringBinding("About"));
        txtSearch.promptTextProperty().bind(resourceFactory.getStringBinding("Search"));
        pneCustom.textProperty().bind(resourceFactory.getStringBinding("CustomSounds"));
        pneNature.textProperty().bind(resourceFactory.getStringBinding("Nature"));

        snpRain.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Rain"));
        snpWind.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Wind"));
        snpThunder.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Thunder"));
        snpBirds.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Birds"));
        snpSeagulls.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Seagulls"));
        snpOcean.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Ocean"));
        snpRiver.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("River"));
        snpFireplace.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Fireplace"));
        snpCave.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Cave"));
        snpFrogs.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Frogs"));
        snpZoo.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Zoo"));
        snpDolphins.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Dolphins"));
        snpForest.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Forest"));

        pneOffice.textProperty().bind(resourceFactory.getStringBinding("Office"));
        snpTyping.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Typing"));
        snpPhone.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Phone"));
        snpChatter.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Chatter"));
        snpTraffic.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Traffic"));
        snpClock.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Clock"));
        snpFan.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Fan"));
        snpCoffee.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Coffee"));

        pneAudiences.textProperty().bind(resourceFactory.getStringBinding("Audiences"));
        snpNetworkingEvent.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("NetworkingEvent"));
        snpLargeCrowd.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("LargeCrowd"));
        snpRestaurant.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Restaurant"));
        snpTribalFestival.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("TribalFestival"));
        snpDrumTribalFestival.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("DrumTribalFestival"));
        snpRugbyFootball.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("RugbyFootball"));

        pneRadioFrequencyStatic.textProperty().bind(resourceFactory.getStringBinding("RadioFrequencyStatic"));
        snpStatic.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Static"));
        snpWhiteNoise.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("WhiteNoise"));
        snpPinkNoise.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("PinkNoise"));
        snpBrownNoise.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("BrownNoise"));

        pneOther.textProperty().bind(resourceFactory.getStringBinding("Other"));
        snpFantasy.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Fantasy"));
        snpZen.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Zen"));
        snpSleepy.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Sleepy"));
        snpRollerCoaster.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("RollerCoaster"));
        snpGong.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Gong"));
        snpBellTower.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("BellTower"));
        snpSpace.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Space"));
        snpTrain.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("Train"));
        snpSlowMetronome.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("SlowMetronome"));
        snpFastMetronome.getNameLabel().textProperty().bind(resourceFactory.getStringBinding("FastMetronome"));
    }

    /**
     * Load the media button visibility for all {@link SoundPane} objects
     *
     * @param visible True if the media buttons should be visible, otherwise false
     */
    public void loadMediaButtonVisibility(final boolean visible) {
        getAllSoundPanes().forEach(s -> s.setMediaButton(visible));
    }

    /**
     * Check for application updates
     *
     * @param showNoUpdates Show an {@link Alert} object when no updates are available
     * @param showErrors    Show an {@link Alert} object when an error occurs
     */
    private void checkForUpdates(final boolean showNoUpdates, final boolean showErrors) {
        logger.info("Attempting to check for updates");

        try {
            final Optional<PlatformUpdate> platformUpdate = updateController.checkForUpdates(platformName, SharedVariables.PORTABLE);
            if (platformUpdate.isPresent()) {
                final PlatformUpdate update = platformUpdate.get();

                logger.info("Version {}.{}.{}.{} is available", update.getMajorVersion(), update.getMinorVersion(), update.getBuildVersion(), update.getRevisionVersion());

                if (FxUtils.showConfirmationAlert(resourceFactory
                                .getResources()
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
                        exitAction();
                    }
                }
            } else {
                logger.info("No updates available");
                if (showNoUpdates) {
                    FxUtils.showInformationAlert(resourceFactory.getResources().getString("NoUpdateAvailable"), getClass().getResourceAsStream(SharedVariables.ICON_URL));
                }
            }
        } catch (final InterruptedException | IOException | InvalidHttpResponseCodeException | URISyntaxException ex) {
            if (ex instanceof InterruptedException)
                Thread.currentThread().interrupt();

            logger.error("Unable to check for updates", ex);
            if (showErrors) {
                FxUtils.showErrorAlert(resourceFactory.getResources().getString("UpdateError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            }
        }
    }

    /**
     * Get all {@link SoundPane} objects
     *
     * @return The {@link List} of {@link SoundPane} objects
     */
    private List<SoundPane> getAllSoundPanes() {
        final List<SoundPane> elements = new ArrayList<>();

        elements.addAll(getSoundPanes(grpOther));
        elements.addAll(getSoundPanes(grpRadioFrequencyStatic));
        elements.addAll(getSoundPanes(grpAudiences));
        elements.addAll(getSoundPanes(grpOffice));
        elements.addAll(getSoundPanes(grpNature));

        return elements;
    }

    /**
     * Get all {@link SoundPane} objects from a {@link GridPane} object
     *
     * @param parent The {@link GridPane} object
     * @return The {@link List} of {@link SoundPane} objects inside the given {@link GridPane} object
     */
    private List<SoundPane> getSoundPanes(final GridPane parent) {
        if (parent == null)
            throw new NullPointerException("GridPane cannot be null!");

        final List<SoundPane> elements = new ArrayList<>();
        parent.getChildren().forEach(e -> {
            if (e instanceof GridPane p)
                elements.addAll(getSoundPanes(p));
            if (e instanceof SoundPane s)
                elements.add(s);
        });
        return elements;
    }

    /**
     * Open a file on the local filesystem
     *
     * @param path The path of the file that should be opened
     */
    private void openFile(final String path) {
        if (path == null)
            throw new NullPointerException("Path cannot be null!");
        if (path.isBlank())
            throw new IllegalArgumentException("Path cannot be blank!");

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
                            FxUtils.showErrorAlert(resourceFactory.getResources().getString("FileExecutionError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
                        }
                    });
                }
            }));
        } catch (final IOException ex) {
            logger.error("Error opening the file", ex);
            FxUtils.showErrorAlert(resourceFactory.getResources().getString("FileExecutionError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Method that is invoked to initialize the FXML object
     */
    @FXML
    private void initialize() {
        mniTimerEnabled.setOnAction(_ -> {
            if (mniTimerEnabled.isSelected()) {
                final Properties properties = settingsController.getProperties();
                final long timerDelay = Long.parseLong(properties.getProperty("timerDelay", "3600000"));

                scheduleTimer(timerDelay);
            } else {
                cancelTimer();
            }
        });

        txtSearch.textProperty().addListener((_, _, newValue) -> {
            if (newValue == null || newValue.isBlank()) {
                Platform.runLater(() -> {
                    getAllSoundPanes().forEach(e -> {
                        e.setVisible(true);
                        e.setManaged(true);
                    });

                    final List<SoundPane> customSoundPanes = getSoundPanes(grpCustom);
                    if (!customSoundPanes.isEmpty()) {
                        pneCustom.setExpanded(true);
                        pneCustom.setVisible(true);
                        pneCustom.setManaged(true);

                        customSoundPanes.forEach(e -> {
                            e.setVisible(true);
                            e.setManaged(true);
                        });
                    }

                    btnClearSearch.setVisible(false);
                    btnClearSearch.setManaged(false);

                    pneNature.setExpanded(true);
                    pneNature.setVisible(true);
                    pneNature.setManaged(true);

                    pneOffice.setExpanded(false);
                    pneOffice.setVisible(true);
                    pneOffice.setManaged(true);

                    pneAudiences.setExpanded(false);
                    pneAudiences.setVisible(true);
                    pneAudiences.setManaged(true);

                    pneRadioFrequencyStatic.setExpanded(false);
                    pneRadioFrequencyStatic.setVisible(true);
                    pneRadioFrequencyStatic.setManaged(true);

                    pneOther.setExpanded(false);
                    pneOther.setVisible(true);
                    pneOther.setManaged(true);
                });
                return;
            }

            Platform.runLater(() -> {
                getAllSoundPanes()
                        .forEach(e -> {
                            e.setVisible(e.getName().toLowerCase().contains(newValue.trim().toLowerCase()));
                            e.setManaged(e.isVisible());
                        });

                getSoundPanes(grpCustom)
                        .forEach(e -> {
                            e.setVisible(e.getName().toLowerCase().contains(newValue.trim().toLowerCase()));
                            e.setManaged(e.isVisible());
                        });

                // Check if there are still active sound panes on pneCustom
                final List<SoundPane> customSoundPanes = getSoundPanes(grpCustom);
                if (!customSoundPanes.isEmpty()) {
                    customSoundPanes.stream().filter(Node::isVisible).findFirst().ifPresentOrElse(_ -> {
                        pneCustom.setExpanded(true);
                        pneCustom.setVisible(true);
                        pneCustom.setManaged(true);
                    }, () -> {
                        pneCustom.setExpanded(false);
                        pneCustom.setVisible(false);
                        pneCustom.setManaged(false);
                    });
                }

                // Check if there are still active sound panes on pneNature
                getSoundPanes(grpNature).stream().filter(Node::isVisible).findFirst().ifPresentOrElse(_ -> {
                    pneNature.setExpanded(true);
                    pneNature.setVisible(true);
                    pneNature.setManaged(true);
                }, () -> {
                    pneNature.setExpanded(false);
                    pneNature.setVisible(false);
                    pneNature.setManaged(false);
                });

                // Check if there are still active sound panes on pneOffice
                getSoundPanes(grpOffice).stream().filter(Node::isVisible).findFirst().ifPresentOrElse(_ -> {
                    pneOffice.setExpanded(true);
                    pneOffice.setVisible(true);
                    pneOffice.setManaged(true);
                }, () -> {
                    pneOffice.setExpanded(false);
                    pneOffice.setVisible(false);
                    pneOffice.setManaged(false);
                });

                // Check if there are still active sound panes on pneAudiences
                getSoundPanes(grpAudiences).stream().filter(Node::isVisible).findFirst().ifPresentOrElse(_ -> {
                    pneAudiences.setExpanded(true);
                    pneAudiences.setVisible(true);
                    pneAudiences.setManaged(true);
                }, () -> {
                    pneAudiences.setExpanded(false);
                    pneAudiences.setVisible(false);
                    pneAudiences.setManaged(false);
                });

                // Check if there are still active sound panes on pneRadioFrequencyStatic
                getSoundPanes(grpRadioFrequencyStatic).stream().filter(Node::isVisible).findFirst().ifPresentOrElse(_ -> {
                    pneRadioFrequencyStatic.setExpanded(true);
                    pneRadioFrequencyStatic.setVisible(true);
                    pneRadioFrequencyStatic.setManaged(true);
                }, () -> {
                    pneRadioFrequencyStatic.setExpanded(false);
                    pneRadioFrequencyStatic.setVisible(false);
                    pneRadioFrequencyStatic.setManaged(false);
                });

                // Check if there are still active sound panes on pneOther
                getSoundPanes(grpOther).stream().filter(Node::isVisible).findFirst().ifPresentOrElse(_ -> {
                    pneOther.setExpanded(true);
                    pneOther.setVisible(true);
                    pneOther.setManaged(true);
                }, () -> {
                    pneOther.setExpanded(false);
                    pneOther.setVisible(false);
                    pneOther.setManaged(false);
                });

                btnClearSearch.setVisible(true);
                btnClearSearch.setManaged(true);
            });
        });
    }

    /**
     * Hide the current stage
     */
    private void hideShowStage() {
        final Stage stage = (Stage) grpNature.getScene().getWindow();
        if (stage.isShowing()) {
            stage.hide();
        } else {
            stage.show();
        }
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
     * Open a custom sound
     */
    @FXML
    private void addCustomSound() {
        logger.info("Opening a custom sound");
        final FileChooser chooser = new FileChooser();

        final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 (*.mp3)", "*.mp3");
        chooser.getExtensionFilters().add(extFilter);

        final File file = chooser.showOpenDialog(new Stage());

        if (file != null && file.exists()) {
            if (file.getAbsolutePath().isBlank())
                throw new IllegalArgumentException("Path cannot be blank!");

            logger.info("Loading custom sound from {}", file.getAbsolutePath());

            try {
                final RowConstraints row = new RowConstraints();
                row.setVgrow(Priority.ALWAYS);

                grpCustom.getRowConstraints().add(row);

                final SoundPane customSoundPane = new SoundPane();
                final int count = getSoundPanes(grpCustom).size() + 1;

                customSoundPane.setName(resourceFactory.getResources().getString("CustomSound") + " #" + count);
                customSoundPane.setMediaKey("custom" + count);
                customSoundPane.setMediaPath(file.toURI().toURL().toString());
                customSoundPane.setImage("/images/customsound.png");
                customSoundPane.setResourceFile(false);

                grpCustom.add(customSoundPane, 0, count - 1);

                pneCustom.setExpanded(true);
                pneCustom.setVisible(true);
                pneCustom.setManaged(true);
            } catch (final IOException ex) {
                logger.error("Unable to open the custom sound from {}", file.getAbsolutePath(), ex);
                FxUtils.showErrorAlert(resourceFactory.getResources().getString("OpenCustomSoundError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            }
        }
    }

    /**
     * Open a sound preset
     *
     * @param path The absolute path of the sound preset file
     */
    private void openSoundPreset(final String path) {
        if (path == null)
            throw new NullPointerException("Path cannot be null!");
        if (path.isBlank())
            throw new IllegalArgumentException("Path cannot be blank!");

        logger.info("Loading sound preset from {}", path);

        try {
            final Path filePath = Path.of(path);
            final String actual = Files.readString(filePath);

            if (actual.isBlank())
                throw new IllegalArgumentException("Sound preset cannot be blank!");

            final TypeReference<HashMap<String, Double>> typeRef = new TypeReference<>() {
            };

            final Map<String, Double> mediaVolumes = jsonMapper.readValue(actual, typeRef);
            final List<SoundPane> soundPanes = getAllSoundPanes();

            mediaVolumes.forEach((key, value) -> soundPanes.stream().filter(e -> e.getMediaKey().equals(key)).forEach(e -> e.getSlider().setValue(value)));
        } catch (final IOException ex) {
            logger.error("Unable to open the sound preset from {}", path, ex);
            FxUtils.showErrorAlert(resourceFactory.getResources().getString("OpenSoundPresetError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
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
            if (!filePath.toLowerCase().contains(".json")) {
                filePath += ".json";
            }

            final Map<String, Double> mediaVolumes = new HashMap<>();
            getAllSoundPanes().forEach(e -> mediaVolumes.put(e.getMediaKey(), e.getSlider().getValue()));

            jsonMapper.writeValue(new File(filePath), mediaVolumes);
        } else {
            logger.info("Cancelled saving a sound settings");
        }
    }

    /**
     * Play or pause all media
     */
    @FXML
    private void playPauseAction() {
        logger.info("Play / pause all media");
        try {
            for (final SoundPane soundPane : getAllSoundPanes()) {
                soundPane.playPause();
            }

            for (final SoundPane soundPane : getSoundPanes(grpCustom)) {
                soundPane.playPause();
            }
        } catch (final MediaPlayerException ex) {
            logger.error("Unable to play / pause MediaPlayer", ex);
            FxUtils.showErrorAlert(resourceFactory.getResources().getString("PlayPauseError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Method that is called when all players should be reset
     */
    @FXML
    private void resetAction() {
        logger.info("Resetting all audio sliders");
        getAllSoundPanes().forEach(e -> e.getSlider().setValue(0));

        getSoundPanes(grpCustom).forEach(SoundPane::disposeMediaPlayer);
        grpCustom.getChildren().clear();

        pneCustom.setExpanded(false);
        pneCustom.setVisible(false);
        pneCustom.setManaged(false);
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
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/windows/SettingsWindow.fxml"), resourceFactory.getResources());
            final Parent root = loader.load();

            final SettingsWindowController settingsWindowController = loader.getController();
            settingsWindowController.setSettingsController(getSettingsController());
            settingsWindowController.setMainWindowController(this);
            settingsWindowController.setTrayIconController(trayIconController);

            final Stage primaryStage = new Stage();

            primaryStage.setTitle(resourceFactory.getResources().getString("SettingsWindowTitle"));
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(SharedVariables.ICON_URL))));
            primaryStage.setScene(new Scene(root));

            primaryStage.setOnHiding(_ -> ThemeController.setTheme(settingsController.getProperties().getProperty("theme", "Light").toLowerCase()));

            logger.info("Showing the SettingsWindow");
            primaryStage.show();
            primaryStage.setWidth(450);
            primaryStage.setHeight(350);
        } catch (final IOException ex) {
            logger.error("Unable to open the SettingsWindow", ex);
            FxUtils.showErrorAlert(resourceFactory.getResources().getString("SettingsWindowError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
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
                            FxUtils.showErrorAlert(resourceFactory.getResources().getString("HelpFileError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
                        }
                    });
                }
            }), SharedVariables.HELP_DOCUMENTATION_RESOURCE_LOCATION);
        } catch (final IOException ex) {
            logger.error("Error opening the help file", ex);
            FxUtils.showErrorAlert(resourceFactory.getResources().getString("HelpFileError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Method that is called when the homepage should be opened
     */
    @FXML
    private void homepageAction() {
        helpUtils.openWebsite("https://codedead.com", resourceFactory.getResources());
    }

    /**
     * Method that is called when the license should be opened
     */
    @FXML
    private void licenseAction() {
        helpUtils.openLicenseFile(resourceFactory.getResources());
    }

    /**
     * Method that is called when the donation website should be opened
     */
    @FXML
    private void donateAction() {
        helpUtils.openWebsite("https://codedead.com/donate", resourceFactory.getResources());
    }

    /**
     * Method that is called when the AboutWindow should be opened
     */
    @FXML
    private void aboutAction() {
        logger.info("Attempting to open the AboutWindow");

        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/windows/AboutWindow.fxml"), resourceFactory.getResources());
            final Parent root = loader.load();

            final AboutWindowController aboutWindowController = loader.getController();
            aboutWindowController.setResourceBundle(resourceFactory.getResources());

            final Stage primaryStage = new Stage();

            primaryStage.setTitle(resourceFactory.getResources().getString("AboutWindowTitle"));
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/opal.png"))));
            primaryStage.setScene(new Scene(root));

            logger.info("Showing the AboutWindow");
            primaryStage.show();
            primaryStage.setWidth(450);
            primaryStage.setHeight(280);
        } catch (final IOException ex) {
            logger.error("Unable to open the AboutWindow", ex);
            FxUtils.showErrorAlert(resourceFactory.getResources().getString("AboutWindowError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Method that is called when the user wants to check for updates
     */
    @FXML
    private void updateAction() {
        checkForUpdates(true, true);
    }

    /**
     * Method that is called when the search field should be cleared
     */
    @FXML
    private void clearSearchAction() {
        txtSearch.clear();
    }

    /**
     * Method that is called when the {@link Timer} object has fired
     */
    @Override
    public void fired() {
        cancelTimer();
        getAllSoundPanes().forEach(SoundPane::pause);
        getSoundPanes(grpCustom).forEach(SoundPane::pause);

        if (Boolean.parseBoolean(settingsController.getProperties().getProperty("timerComputerShutdown", "false"))) {
            final String command = switch (platformName.toLowerCase()) {
                case "windows" -> "shutdown -s -t 0";
                case "linux", "macos" -> "shutdown -h now";
                default -> null;
            };

            if (command != null) {
                try {
                    final ProcessBuilder p = new ProcessBuilder(command.split(" "));

                    try (final BufferedReader reader = new BufferedReader(new InputStreamReader(p.start().getInputStream()))) {
                        final StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                            builder.append(System.lineSeparator());
                        }
                        final String result = builder.toString();
                        logger.info("Shutdown command result: {}", result);
                    }
                    exitAction();
                } catch (final IOException ex) {
                    logger.error("Unable to execute shutdown command", ex);
                }
            } else {
                logger.error("Unable to execute shutdown command, unsupported platform {}", platformName);
            }
        }

        if (Boolean.parseBoolean(settingsController.getProperties().getProperty("timerApplicationShutdown", "false"))) {
            exitAction();
        }
    }

    /**
     * Method that is invoked when the object's {@link Timer} object has cancelled
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
        if (!Boolean.parseBoolean(settingsController.getProperties().getProperty("dragDrop", "true")))
            return;

        if (dragEvent.getDragboard().hasFiles())
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);

        dragEvent.consume();
    }

    /**
     * Method that is invoked when a drag-drop event occurred
     *
     * @param dragEvent The {@link DragEvent} object
     */
    @FXML
    private void onDragDropped(final DragEvent dragEvent) {
        if (!Boolean.parseBoolean(settingsController.getProperties().getProperty("dragDrop", "true")))
            return;

        final Dragboard db = dragEvent.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            openSoundPreset(db.getFiles().getFirst().getAbsolutePath());
            success = true;
        }

        dragEvent.setDropCompleted(success);
        dragEvent.consume();
    }

    /**
     * Cancel the {@link Timer} object
     */
    public void cancelTimer() {
        logger.info("Cancelling the Timer to stop all MediaPlayer objects");

        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }

        if (countDownTask != null) {
            countDownTask.cancel();
            countDownTimer.purge();
        }

        Platform.runLater(() -> mniCountDown.setVisible(false));

        if (audioTimer != null) {
            audioTimer.cancelled();
        }
    }

    /**
     * Schedule the {@link Timer} object to cancel all {@link MediaPlayer} objects
     *
     * @param delay The delay in milliseconds before the {@link Timer} object executes its function
     */
    public void scheduleTimer(final long delay) {
        if (delay < 1)
            throw new IllegalArgumentException("Delay cannot be smaller than 1");

        logger.info("Scheduling the Timer to stop all MediaPlayer objects after {} millisecond(s)", delay);

        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }

        if (countDownTask != null) {
            countDownTask.cancel();
            countDownTimer.purge();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                logger.info("Timer has fired");
                audioTimer.fired();
            }
        };

        countDownTask = new TimerTask() {
            final long seconds = delay / 1000;
            int i = 0;

            @Override
            public void run() {
                i++;
                long timeLeft = (seconds - (i % seconds));

                // Calculate hours, minutes and seconds
                long hours = timeLeft / 3600;
                long minutes = (timeLeft % 3600) / 60;
                long seconds = timeLeft % 60;

                // Format the values to HH:MM:SS with leading zeros if necessary
                final String timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                Platform.runLater(() -> mniCountDown.setText(timeLeftFormatted));
            }
        };

        timer.schedule(timerTask, delay);
        countDownTimer.schedule(countDownTask, 0, 1000);

        mniCountDown.setVisible(true);
    }

    /**
     * Set the audio balance
     *
     * @param audioBalance The audio balance
     */
    public void setAudioBalance(final double audioBalance) {
        if (audioBalance < -1 || audioBalance > 1)
            throw new IllegalArgumentException("Balance must be between -1.0 and 1.0!");

        logger.info("Setting the audio balance to {}", audioBalance);
        getAllSoundPanes().forEach(s -> s.setBalance(audioBalance));
    }

    /**
     * Update the {@link ResourceBundle} object
     *
     * @param resourceBundle The {@link ResourceBundle} object
     */
    public void updateResourceBundle(final ResourceBundle resourceBundle) {
        resourceFactory.setResources(resourceBundle);
    }

    /**
     * Method that is called when the Window should be hidden or shown
     */
    @Override
    public void onShowHide() {
        hideShowStage();
    }

    /**
     * Method that is called when the SettingsWindow should be opened
     */
    @Override
    public void onSettings() {
        settingsAction();
    }

    /**
     * Method that is called when the AboutWindow should be opened
     */
    @Override
    public void onAbout() {
        aboutAction();
    }

    /**
     * Method that is called when the application should exit
     */
    @Override
    public void onExit() {
        exitAction();
    }
}
