package com.codedead.opal.domain;

import javafx.application.Platform;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private String mediaPath;
    @FXML
    private String mediaKey;
    private double balance;
    private MediaPlayer mediaPlayer;
    private final Logger logger;

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

        this.logger = LogManager.getLogger(SoundPane.class);
    }

    /**
     * Method that is invoked to initialize the FXML object
     */
    @FXML
    private void initialize() {
        sldVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.doubleValue() == 0 && (oldValue != null && oldValue.doubleValue() != 0)) {
                pause();
                disposeMediaPlayer();
            } else if (newValue != null && newValue.doubleValue() > 0 && (oldValue != null && oldValue.doubleValue() == 0)) {
                try {
                    play();
                } catch (final MediaPlayerException e) {
                    logger.fatal("Could not play the media file!", e);
                    Platform.exit();
                }
            }
        });
    }

    /**
     * Initialize the {@link MediaPlayer} object
     *
     * @param value The value of the media path
     * @throws URISyntaxException When the media path could not be converted to a URI
     */
    private void initializeMediaPlayer(final String value) throws URISyntaxException {
        if (value == null)
            throw new NullPointerException("Value cannot be null!");
        if (value.isEmpty())
            throw new IllegalArgumentException("Value cannot be empty!");

        disposeMediaPlayer();

        mediaPlayer = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource(value)).toURI().toString()));
        mediaPlayer.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
            // Quality of life improvement to reduce audio lag when restarting the media
            if (mediaPlayer != null && newDuration.toSeconds() >= mediaPlayer.getMedia().getDuration().toSeconds() - 0.5) {
                mediaPlayer.seek(Duration.ZERO);
            }
        });
        mediaPlayer.setOnEndOfMedia(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.ZERO);
            }
        });
        mediaPlayer.volumeProperty().bindBidirectional(sldVolume.valueProperty());
        mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == MediaPlayer.Status.PLAYING) {
                imgMediaButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/pause.png"))));
            } else {
                imgMediaButton.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/play.png"))));
            }
        });
        mediaPlayer.setBalance(balance);
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
        return mediaPath;
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

        this.mediaPath = mediaPath;
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
     *
     * @throws MediaPlayerException When the {@link MediaPlayer} object could not be initialized
     */
    public void play() throws MediaPlayerException {
        if (mediaPlayer == null) {
            try {
                initializeMediaPlayer(mediaPath);
            } catch (final URISyntaxException e) {
                logger.fatal("Could not convert the media path to a URI!", e);
                throw new MediaPlayerException(e.getMessage());
            }
        }
        this.mediaPlayer.play();
    }

    /**
     * Pause the {@link Media} object
     */
    public void pause() {
        if (mediaPlayer != null) {
            this.mediaPlayer.pause();
        }
    }

    /**
     * Play or pause the {@link Media} object
     *
     * @throws MediaPlayerException When the {@link MediaPlayer} object could not be initialized
     */
    @FXML
    private void playPause() throws MediaPlayerException {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            pause();
        } else {
            play();
        }
    }

    /**
     * Dispose of the {@link MediaPlayer} object and all bindings
     */
    private void disposeMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.volumeProperty().unbindBidirectional(sldVolume.valueProperty());
            mediaPlayer.stop();
            mediaPlayer.dispose();

            mediaPlayer = null;
        }
    }

    /**
     * Get the audio balance
     *
     * @return The audio balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Set the audio balance
     *
     * @param balance The audio balance
     */
    public void setBalance(final double balance) {
        if (balance < -1.0 || balance > 1.0)
            throw new IllegalArgumentException("Balance must be between -1.0 and 1.0!");

        logger.info("Setting audio balance to {}", balance);

        this.balance = balance;
        if (mediaPlayer != null) {
            mediaPlayer.setBalance(balance);
        }
    }
}
