package com.codedead.opal.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

@SuppressWarnings("unused")
public final class SoundPane extends GridPane {

    @FXML
    private ImageView soundImage;
    @FXML
    private Label lblName;
    @FXML
    private Slider sldVolume;
    @FXML
    private Button btnPlayPause;
    @FXML
    private boolean mediaButton;
    @FXML
    private ImageView imgMediaButton;
    @FXML
    private final StringProperty mediaPath = new SimpleStringProperty();
    @FXML
    private String mediaKey;
    private MediaPlayer mediaPlayer;

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
     * Method that is invoked to initialize the FXML object
     */
    @FXML
    private void initialize() {
        sldVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.doubleValue() == 0 && (oldValue != null && oldValue.doubleValue() != 0)) {
                pause();
            } else if (newValue != null && newValue.doubleValue() > 0 && (oldValue != null && oldValue.doubleValue() == 0)) {
                play();
            }
        });

        mediaPath.addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null && !newValue.isEmpty()) {
                    try {
                        mediaPlayer = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource(newValue)).toURI().toString()));
                        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
                        mediaPlayer.volumeProperty().bindBidirectional(sldVolume.valueProperty());
                        mediaPlayer.statusProperty().addListener(new ChangeListener<>() {
                            @Override
                            public void changed(ObservableValue<? extends MediaPlayer.Status> observable, MediaPlayer.Status oldValue, MediaPlayer.Status newValue) {
                                if (newValue == MediaPlayer.Status.PLAYING) {
                                    imgMediaButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/pause.png"))));
                                } else {
                                    imgMediaButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/play.png"))));
                                }
                            }
                        });
                    } catch (final URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
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
     * Get the slider
     *
     * @return The slider
     */
    public Slider getSlider() {
        return sldVolume;
    }

    /**
     * Get whether media buttons are enabled
     *
     * @return True if media buttons are enabled, otherwise false
     */
    @FXML
    public boolean isMediaButton() {
        return mediaButton;
    }

    /**
     * Set whether media buttons are enabled
     *
     * @param mediaButton True if media buttons should be enabled, otherwise false
     */
    @FXML
    public void setMediaButton(final boolean mediaButton) {
        btnPlayPause.setVisible(mediaButton);
        btnPlayPause.setManaged(mediaButton);
        this.mediaButton = mediaButton;
    }

    /**
     * Get the {@link javafx.scene.media.Media} object path
     *
     * @return The {@link javafx.scene.media.Media} object path
     */
    @FXML
    public String getMediaPath() {
        return mediaPath.getValue();
    }

    /**
     * Set the {@link javafx.scene.media.Media} object path
     *
     * @param mediaPath The {@link javafx.scene.media.Media} object path
     */
    @FXML
    public void setMediaPath(final String mediaPath) {
        if (mediaPath == null)
            throw new NullPointerException("Media path cannot be null!");
        if (mediaPath.isEmpty())
            throw new IllegalArgumentException("Media path cannot be empty!");

        this.mediaPath.setValue(mediaPath);
    }

    /**
     * Get the media key
     *
     * @return The media key
     */
    @FXML
    public String getMediaKey() {
        return mediaKey;
    }

    /**
     * Set the media key
     *
     * @param mediaKey The media key
     */
    @FXML
    public void setMediaKey(final String mediaKey) {
        if (mediaKey == null)
            throw new NullPointerException("Media key cannot be null!");
        if (mediaKey.isEmpty())
            throw new IllegalArgumentException("Media key cannot be empty!");

        this.mediaKey = mediaKey;
    }

    /**
     * Play the {@link Media} object
     */
    public void play() {
        this.mediaPlayer.play();
    }

    /**
     * Pause the {@link Media} object
     */
    public void pause() {
        this.mediaPlayer.pause();
    }

    /**
     * Play or pause the {@link Media} object
     */
    @FXML
    private void playPause() {
        if (this.mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            pause();
        } else {
            play();
        }
    }
}
