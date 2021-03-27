package com.codedead.opal;

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
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class OpalApplication extends Application {

    private static final Logger logger = LogManager.getLogger(OpalApplication.class);
    private static final String PROPERTIES_LOCATION = "default.properties";

    /**
     * Initialize the application
     *
     * @param args The application arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        logger.info("Creating the SettingsController");
        final SettingsController settingsController = new SettingsController(PROPERTIES_LOCATION, PROPERTIES_LOCATION);
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
        mainWindowController.setSettingsController(settingsController);

        final Scene scene = new Scene(root);

        primaryStage.setTitle(translationBundle.getString("MainWindowTitle"));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/opal.png")));
        primaryStage.setScene(scene);

        logger.info("Showing the MainWindow");
        primaryStage.show();
    }
}
