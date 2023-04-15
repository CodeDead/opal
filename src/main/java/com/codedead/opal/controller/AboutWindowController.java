package com.codedead.opal.controller;

import com.codedead.opal.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ResourceBundle;

public final class AboutWindowController {

    private ResourceBundle translationBundle;
    private final HelpUtils helpUtils;
    private final Logger logger;

    /**
     * Initialize a new AboutWindowController
     */
    public AboutWindowController() {
        logger = LogManager.getLogger(AboutWindowController.class);
        logger.info("Initializing new AboutWindowController object");

        helpUtils = new HelpUtils();
    }

    /**
     * Set the resource bundle
     *
     * @param resourceBundle The {@link ResourceBundle} object
     */
    public void setResourceBundle(final ResourceBundle resourceBundle) {
        if (resourceBundle == null)
            throw new NullPointerException("ResourceBundle cannot be null");

        this.translationBundle = resourceBundle;
    }

    /**
     * Method that is called when the close button is selected
     *
     * @param event The {@link ActionEvent} object
     */
    @FXML
    private void closeAction(final ActionEvent event) {
        logger.info("Closing AboutWindow");
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }

    /**
     * Method that is called when the license button is selected
     */
    @FXML
    private void licenseAction() {
        helpUtils.openLicenseFile(translationBundle);
    }

    /**
     * Method that is called when the CodeDead button is selected
     */
    @FXML
    private void codeDeadAction() {
        helpUtils.openCodeDeadWebSite(translationBundle);
    }
}
