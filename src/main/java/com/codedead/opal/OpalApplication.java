package com.codedead.opal;

import com.codedead.opal.controller.UpdateController;
import com.codedead.opal.utils.SharedVariables;
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

import java.io.IOException;
import java.util.*;

public class OpalApplication extends Application {

    private static final Logger logger = LogManager.getLogger(OpalApplication.class);

    /**
     * Initialize the application
     *
     * @param args The application arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }

    /**
     * Method that is called by the JavaFX runtime
     *
     * @param primaryStage The initial Stage object
     * @throws IOException When the {@link SettingsController} object could not be initialized
     */
    @Override
    public void start(final Stage primaryStage) throws IOException {
        logger.info("Creating the SettingsController");
        final SettingsController settingsController = new SettingsController(SharedVariables.PROPERTIES_LOCATION, SharedVariables.PROPERTIES_LOCATION);
        final Properties properties = settingsController.getProperties();

        final String languageTag = properties.getProperty("locale", "en-US");

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Attempting to load the ResourceBundle for locale %s", languageTag));
        }

        final Locale locale = Locale.forLanguageTag(languageTag);
        final ResourceBundle translationBundle = ResourceBundle.getBundle("translations.OpalApplication", locale);

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/windows/MainWindow.fxml"), translationBundle);
        Parent root;
        try {
            root = loader.load();
        } catch (final IOException ex) {
            logger.error("Unable to load FXML for MainWindow", ex);
            return;
        }

        logger.info("Creating the MainWindowController");

        final MainWindowController mainWindowController = loader.getController();
        mainWindowController.setControllers(
                settingsController,
                new UpdateController(properties.getProperty("updateApi", "https://codedead.com/Software/Opal/version.json"))
        );

        final Scene scene = new Scene(root);

        primaryStage.setTitle(translationBundle.getString("MainWindowTitle"));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(SharedVariables.ICON_URL)));
        primaryStage.setScene(scene);

        logger.info("Showing the MainWindow");
        primaryStage.show();
    }
}
