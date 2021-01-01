package com.codedead.opal.utils;

import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;
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
     * Initialize a new Stage using shared settings
     *
     * @param stage  The Stage object that should be initialized
     * @param root   The Parent object to which the Scene is linked
     * @param title  The title of the Stage object
     * @param width  The width of the Stage object
     * @param height The height of the Stage object
     */
    public static void initializeStage(final Stage stage, final Parent root, final String title, final double width, final double height) {
        if (stage == null)
            throw new NullPointerException("Stage cannot be null!");
        if (root == null)
            throw new NullPointerException("Parent cannot be null!");
        if (title == null)
            throw new NullPointerException("Title cannot be null!");

        if (width <= 0)
            throw new IllegalArgumentException("Width cannot be smaller than or equal to zero!");
        if (height <= 0)
            throw new IllegalArgumentException("Height cannot be smaller than or equal to zero!");

        stage.setTitle(title);
        stage.setScene(new Scene(root, width, height));

        final Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - width) / 2);
        stage.setY((primScreenBounds.getHeight() - height) / 2);
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

        final Alert alert = getAlert(Alert.AlertType.ERROR, content);
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
     * @param alertType The AlertType of the Alert
     * @param content   The content that should be displayed to the user
     * @return The Alert object that was generated
     */
    private static Alert getAlert(final Alert.AlertType alertType, final String content) {
        final Alert alert = new Alert(alertType);

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
