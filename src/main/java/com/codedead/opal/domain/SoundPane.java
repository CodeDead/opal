package com.codedead.opal.domain;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;

@SuppressWarnings("unused")
public final class SoundPane extends GridPane {

    @FXML
    private ImageView soundImage;
    @FXML
    private Label lblName;
    @FXML
    private Slider sldVolume;

    /**
     * Initialize a new SoundPane
     *
     * @throws IOException When the SoundPane resource could not be loaded
     */
    public SoundPane() throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/controls/SoundPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    /**
     * Get the image
     *
     * @return The image
     */
    @FXML
    public String getImage() {
        return soundImage.getImage().getUrl();
    }

    /**
     * Set the image
     *
     * @param image The image
     */
    @FXML
    public void setImage(final String image) {
        if (image == null)
            return;

        final URL url = getClass().getResource(image);
        if (url != null) {
            soundImage.setImage(new Image(url.toString()));
        } else {
            soundImage.setImage(null);
        }
    }

    /**
     * Get the name
     *
     * @return The name
     */
    @FXML
    public String getName() {
        return lblName.getText();
    }

    /**
     * Set the name
     *
     * @param name The name
     */
    @FXML
    public void setName(final String name) {
        lblName.setText(name);
    }

    /**
     * Get the volume
     *
     * @return The volumne
     */
    @FXML
    public double getVolume() {
        return sldVolume.getValue();
    }

    /**
     * Set the volume
     *
     * @param volume The volume
     */
    @FXML
    public void setVolume(final double volume) {
        sldVolume.setValue(volume);
    }

    /**
     * Get the slider
     *
     * @return The slider
     */
    public Slider getSlider() {
        return sldVolume;
    }
}
