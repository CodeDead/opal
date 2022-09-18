package com.codedead.opal.controller;

import com.codedead.opal.domain.*;
import com.codedead.opal.interfaces.IAudioTimer;
import com.codedead.opal.interfaces.IRunnableHelper;
import com.codedead.opal.utils.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

import static com.codedead.opal.utils.SharedVariables.DEFAULT_LOCALE;

public final class MainWindowController implements IAudioTimer {

    @FXML
    private GridPane grpControls;
    @FXML
    private GridPane grpMain;
    @FXML
    private CheckMenuItem mniTimerEnabled;

    private TrayIcon trayIcon;
    private SettingsController settingsController;
    private UpdateController updateController;
    private ResourceBundle translationBundle;
    private TimerTask timerTask;
    private boolean timerEnabled;
    private final String platformName;
    private final HelpUtils helpUtils;
    private final ObjectMapper objectMapper;
    private final Timer timer;
    private final IAudioTimer audioTimer;
    private final Logger logger;

    /**
     * Initialize a new MainWindowController
     */
    public MainWindowController() {
        logger = LogManager.getLogger(MainWindowController.class);
        logger.info("Initializing new MainWindowController object");

        platformName = OsCheck.getOperatingSystemType().name();
        helpUtils = new HelpUtils();

        this.timer = new Timer();
        this.audioTimer = this;
        this.objectMapper = new ObjectMapper();
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
        getAllSoundPanes(grpControls).forEach(s -> s.setMediaButton(visible));
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
     * Get all {@link SoundPane} objects from a {@link GridPane} object
     *
     * @param parent The {@link GridPane} object
     * @return The {@link List} of {@link SoundPane} objects inside the given {@link GridPane} object
     */
    private List<SoundPane> getAllSoundPanes(final GridPane parent) {
        if (parent == null)
            throw new NullPointerException("GridPane cannot be null!");

        final List<SoundPane> elements = new ArrayList<>();
        parent.getChildren().forEach(e -> {
            if (e instanceof GridPane p)
                elements.addAll(getAllSoundPanes(p));
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
     * Method that is invoked to initialize the FXML object
     */
    @FXML
    private void initialize() {
        mniTimerEnabled.setOnAction(e ->
        {
            if (mniTimerEnabled.isSelected()) {
                final Properties properties = settingsController.getProperties();
                final long timerDelay = Long.parseLong(properties.getProperty("timerDelay", "3600000"));

                scheduleTimer(timerDelay);
            } else {
                cancelTimer();
            }
        });
    }

    /**
     * Create a tray icon
     *
     * @throws IOException When the {@link TrayIcon} could not be created
     */
    private void createTrayIcon() throws IOException {
        logger.info("Creating tray icon");
        if (!SystemTray.isSupported()) {
            logger.warn("SystemTray is not supported");
            return;
        }

        final SystemTray tray = SystemTray.getSystemTray();
        final Dimension trayIconSize = tray.getTrayIconSize();
        final PopupMenu popup = new PopupMenu();
        final BufferedImage trayIconImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/opal.png")));
        final TrayIcon localTrayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconSize.width, trayIconSize.height, java.awt.Image.SCALE_SMOOTH));
        final java.awt.MenuItem displayItem = new java.awt.MenuItem(translationBundle.getString("Display"));
        final java.awt.MenuItem settingsItem = new java.awt.MenuItem(translationBundle.getString("Settings"));
        final java.awt.MenuItem aboutItem = new java.awt.MenuItem(translationBundle.getString("About"));
        final java.awt.MenuItem exitItem = new java.awt.MenuItem(translationBundle.getString("Exit"));

        // Platform.runLater to run on the JavaFX thread
        displayItem.addActionListener(e -> Platform.runLater(this::hideShowStage));
        settingsItem.addActionListener(e -> Platform.runLater(this::settingsAction));
        aboutItem.addActionListener(e -> Platform.runLater(this::aboutAction));
        exitItem.addActionListener(e -> Platform.runLater(this::exitAction));

        popup.add(displayItem);
        popup.addSeparator();
        popup.add(settingsItem);
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(exitItem);

        localTrayIcon.setToolTip("Opal");
        localTrayIcon.setPopupMenu(popup);
        localTrayIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(final java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    Platform.runLater(MainWindowController.this::hideShowStage);
                }
            }
        });

        this.trayIcon = localTrayIcon;
    }

    /**
     * Display the tray icon
     *
     * @throws IOException When the {@link TrayIcon} could not be created
     */
    public void showTrayIcon() throws IOException {
        logger.info("Displaying tray icon");
        if (trayIcon == null) {
            createTrayIcon();
            if (trayIcon == null) {
                logger.warn("TrayIcon cannot be null!");
                return;
            }
        }

        final SystemTray tray = SystemTray.getSystemTray();
        try {
            if (!Arrays.asList(tray.getTrayIcons()).contains(trayIcon)) {
                tray.add(trayIcon);
            }
        } catch (final AWTException e) {
            logger.error("TrayIcon could not be added", e);
        }
    }

    /**
     * Hide the tray icon
     */
    public void hideTrayIcon() {
        logger.info("Hiding tray icon");
        if (trayIcon == null) {
            logger.warn("TrayIcon cannot be null!");
            return;
        }

        final SystemTray tray = SystemTray.getSystemTray();
        tray.remove(trayIcon);

        trayIcon = null;
    }

    /**
     * Hide the current stage
     */
    private void hideShowStage() {
        final Stage stage = (Stage) grpControls.getScene().getWindow();
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
     * Open a sound preset
     *
     * @param path The absolute path of the sound preset file
     */
    private void openSoundPreset(final String path) {
        if (path == null)
            throw new NullPointerException("Path cannot be null!");
        if (path.isEmpty())
            throw new IllegalArgumentException("Path cannot be empty!");

        logger.info("Loading sound preset from {}", path);

        try {
            final Path filePath = Path.of(path);
            final String actual = Files.readString(filePath);

            if (actual == null || actual.isEmpty())
                throw new IllegalArgumentException("Sound preset cannot be null or empty!");

            final TypeReference<HashMap<String, Double>> typeRef = new TypeReference<>() {
            };

            final Map<String, Double> mediaVolumes = objectMapper.readValue(actual, typeRef);
            final List<SoundPane> soundPanes = getAllSoundPanes(grpControls);

            mediaVolumes.forEach((key, value) -> soundPanes.stream().filter(e -> e.getMediaKey().equals(key)).forEach(e -> e.getSlider().setValue(value)));
        } catch (final IOException ex) {
            logger.error("Unable to open the sound preset from {}", path, ex);
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
            if (!filePath.toLowerCase().contains(".json")) {
                filePath += ".json";
            }

            final Map<String, Double> mediaVolumes = new HashMap<>();
            getAllSoundPanes(grpControls).forEach(e -> mediaVolumes.put(e.getMediaKey(), e.getSlider().getValue()));

            try {
                objectMapper.writeValue(new File(filePath), mediaVolumes);
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
        getAllSoundPanes(grpControls).forEach(e -> e.getSlider().setValue(0));
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
        } catch (final IOException ex) {
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
            aboutWindowController.setResourceBundle(translationBundle);

            final Stage primaryStage = new Stage();

            primaryStage.setTitle(translationBundle.getString("AboutWindowTitle"));
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/opal.png"))));
            primaryStage.setScene(new Scene(root));

            logger.info("Showing the AboutWindow");
            primaryStage.show();
        } catch (final IOException ex) {
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
     * Method that is called when the {@link Timer} object has fired
     */
    @Override
    public void fired() {
        getAllSoundPanes(grpControls).forEach(SoundPane::pause);
        mniTimerEnabled.setSelected(false);

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
        if (!Boolean.parseBoolean(settingsController.getProperties().getProperty("dragDrop", "true")))
            return;

        final Dragboard db = dragEvent.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            openSoundPreset(db.getFiles().get(0).getAbsolutePath());
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

        timerEnabled = false;

        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }

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

        timerEnabled = true;

        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                logger.info("Timer has fired");
                if (timerEnabled) {
                    audioTimer.fired();
                }
                timerEnabled = false;
            }
        };

        timer.schedule(timerTask, delay);
    }
}
