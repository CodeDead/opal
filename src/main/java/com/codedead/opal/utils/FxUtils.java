package com.codedead.opal.utils;

import javafx.scene.control.Alert;
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
     * Show an error message to the user
     *
     * @param header      The content of the header
     * @param content     The content of the error message
     * @param imageStream The InputStream that contains an image
     */
    public static void showErrorAlert(final String header, final String content, final InputStream imageStream) {
        if (header == null) throw new NullPointerException("Header cannot be null!");
        if (content == null) throw new NullPointerException("Content cannot be null!");

        final Alert alert = getAlert(content);
        alert.setHeaderText(header);

        final Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        if (imageStream != null) {
            stage.getIcons().add(new Image(imageStream));
        }

        alert.showAndWait();
    }

    /**
     * Generate a new Alert
     *
     * @param content The content that should be displayed to the user
     * @return The Alert object that was generated
     */
    private static Alert getAlert(final String content) {
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
