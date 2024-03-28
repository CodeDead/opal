package com.codedead.opal.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.InputStream;

public final class FxUtils {

    /**
     * Initialize a new FxUtils
     */
    private FxUtils() {
        // Default constructor
    }

    /**
     * Display an information alert
     *
     * @param content     The content of the information alert
     * @param imageStream The {@link InputStream} that contains the image for the {@link Alert} object
     */
    public static void showInformationAlert(final String content, final InputStream imageStream) {
        if (content == null)
            throw new NullPointerException("Content cannot be null!");

        final Alert alert = new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
        final Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        if (imageStream != null)
            stage.getIcons().add(new Image(imageStream));

        alert.showAndWait();
    }

    /**
     * Display a confirmation dialog
     *
     * @param content     The content of the confirmation alert
     * @param imageStream The {@link InputStream} object that contains the image for the {@link Alert} object
     * @return True if the user pressed YES, otherwise false
     */
    public static boolean showConfirmationAlert(final String content, final InputStream imageStream) {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.YES, ButtonType.NO);
        final Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        if (imageStream != null)
            stage.getIcons().add(new Image(imageStream));

        alert.showAndWait();

        return alert.getResult() == ButtonType.YES;
    }

    /**
     * Show an error message to the user
     *
     * @param header      The content of the header
     * @param content     The content of the error message
     * @param imageStream The {@link InputStream} object that contains the image for the {@link Alert} object
     */
    public static void showErrorAlert(final String header, final String content, final InputStream imageStream) {
        if (header == null)
            throw new NullPointerException("Header cannot be null!");
        if (content == null)
            throw new NullPointerException("Content cannot be null!");

        final Alert alert = getErrorAlert(content);
        alert.setHeaderText(header);

        final Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        if (imageStream != null) {
            stage.getIcons().add(new Image(imageStream));
        }

        alert.showAndWait();
    }

    /**
     * Generate a new error {@link Alert} object
     *
     * @param content The content that should be displayed to the user
     * @return The {@link Alert} object that was generated
     */
    private static Alert getErrorAlert(final String content) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);

        final GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);

        final TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        expContent.add(textArea, 0, 0);
        alert.getDialogPane().setExpandableContent(expContent);

        return alert;
    }
}
