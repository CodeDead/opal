package com.codedead.opal;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.codedead.opal.controller.UpdateController;
import com.codedead.opal.utils.FxUtils;
import com.codedead.opal.utils.SharedVariables;
import javafx.application.Platform;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.codedead.opal.controller.MainWindowController;
import com.codedead.opal.controller.SettingsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static com.codedead.opal.utils.SharedVariables.DEFAULT_LOCALE;

public class OpalApplication extends Application {

    private static final Logger logger = LogManager.getLogger(OpalApplication.class);

    /**
     * Initialize the application
     *
     * @param args The application arguments
     */
    public static void main(final String[] args) {
        Level logLevel = Level.ERROR;
        try (final FileInputStream fis = new FileInputStream(SharedVariables.PROPERTIES_FILE_LOCATION)) {
            final Properties prop = new Properties();
            prop.load(fis);

            logLevel = switch (prop.getProperty("loglevel", "ERROR")) {
                case "OFF" -> Level.OFF;
                case "FATAL" -> Level.FATAL;
                case "WARN" -> Level.WARN;
                case "DEBUG" -> Level.DEBUG;
                case "TRACE" -> Level.TRACE;
                case "INFO" -> Level.INFO;
                case "ALL" -> Level.ALL;
                default -> Level.ERROR;
            };
        } catch (final IOException ex) {
            logger.error("Properties object could not be loaded", ex);
        }

        Configurator.setAllLevels(LogManager.getRootLogger().getName(), logLevel);
        launch(args);
    }

    /**
     * Method that is called by the JavaFX runtime
     *
     * @param primaryStage The initial Stage object
     */
    @Override
    public void start(final Stage primaryStage) {
        Platform.setImplicitExit(false);
        final SettingsController settingsController;

        try {
            settingsController = new SettingsController(SharedVariables.PROPERTIES_FILE_LOCATION, SharedVariables.PROPERTIES_RESOURCE_LOCATION);
        } catch (final IOException ex) {
            logger.error("Unable to initialize the SettingsController", ex);
            FxUtils.showErrorAlert("Exception occurred", ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            Platform.exit();
            return;
        }

        final Properties properties = settingsController.getProperties();
        final String languageTag = properties.getProperty("locale", DEFAULT_LOCALE);

        final String theme = properties.getProperty("theme", "light");
        switch (theme.toLowerCase()) {
            case "nordlight" -> Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
            case "norddark" -> Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
            case "dark" -> Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
            default -> Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        }

        final Locale locale = Locale.forLanguageTag(languageTag);
        final ResourceBundle translationBundle = ResourceBundle.getBundle("translations.OpalApplication", locale);

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/windows/MainWindow.fxml"), translationBundle);
        Parent root;
        try {
            root = loader.load();
        } catch (final IOException ex) {
            logger.error("Unable to load FXML for MainWindow", ex);
            Platform.exit();
            return;
        }

        logger.info("Creating the MainWindowController");

        final MainWindowController mainWindowController = loader.getController();
        final UpdateController updateController = new UpdateController(properties.getProperty("updateApi", "https://codedead.com/Software/Opal/version.json"), SharedVariables.CURRENT_VERSION);
        mainWindowController.setControllers(settingsController, updateController);

        final Scene scene = new Scene(root);

        primaryStage.setTitle(translationBundle.getString("MainWindowTitle"));
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(SharedVariables.ICON_URL))));
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> System.exit(0));

        logger.info("Showing the MainWindow");
        primaryStage.show();

        // Load tray icons after displaying the main stage to display the proper icon in the task bar / activities bar (linux)
        if (Boolean.parseBoolean(properties.getProperty("trayIcon", "false"))) {
            try {
                mainWindowController.showTrayIcon();
            } catch (final IOException ex) {
                logger.error("Unable to create tray icon", ex);
                FxUtils.showErrorAlert(translationBundle.getString("TrayIconError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            }
        }
    }
}
