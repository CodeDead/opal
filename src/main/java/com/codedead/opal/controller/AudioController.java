package com.codedead.opal.controller;

import com.codedead.opal.domain.SoundPreset;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AudioController {

    private static final Logger logger = LogManager.getLogger(AudioController.class);

    private final MediaPlayer rainPlayer;
    private final MediaPlayer windPlayer;
    private final MediaPlayer thunderPlayer;
    private final MediaPlayer birdPlayer;
    private final MediaPlayer keyboardPlayer;
    private final MediaPlayer phonePlayer;
    private final MediaPlayer chatterPlayer;
    private final MediaPlayer trafficPlayer;
    private final MediaPlayer fireplacePlayer;

    private final ObjectMapper objectMapper;

    private SoundPreset soundPreset;

    /**
     * Initialize a new AudioController
     *
     * @throws URISyntaxException When the URI syntax is incorrect
     */
    public AudioController() throws URISyntaxException {
        this(new SoundPreset());
    }

    /**
     * Initialize a new AudioController
     *
     * @param soundPreset The {@link com.codedead.opal.domain.SoundPreset} object that contains volume levels
     * @throws URISyntaxException When the URI syntax is incorrect
     */
    public AudioController(final SoundPreset soundPreset) throws URISyntaxException {
        logger.info("Initializing new AudioController object");

        logger.info("Loading rain MediaPlayer");
        final Media rain = new Media(getClass().getResource("/audio/rain.mp3").toURI().toString());
        rainPlayer = new MediaPlayer(rain);
        rainPlayer.setOnEndOfMedia(() -> rainPlayer.seek(Duration.ZERO));

        logger.info("Loading wind MediaPlayer");
        final Media wind = new Media(getClass().getResource("/audio/wind.mp3").toURI().toString());
        windPlayer = new MediaPlayer(wind);
        windPlayer.setOnEndOfMedia(() -> windPlayer.seek(Duration.ZERO));

        logger.info("Loading thunder MediaPlayer");
        final Media thunder = new Media(getClass().getResource("/audio/thunder.mp3").toURI().toString());
        thunderPlayer = new MediaPlayer(thunder);
        thunderPlayer.setOnEndOfMedia(() -> thunderPlayer.seek(Duration.ZERO));

        logger.info("Loading bird MediaPlayer");
        final Media birds = new Media(getClass().getResource("/audio/birds.mp3").toURI().toString());
        birdPlayer = new MediaPlayer(birds);
        birdPlayer.setOnEndOfMedia(() -> birdPlayer.seek(Duration.ZERO));

        logger.info("Loading keyboard MediaPlayer");
        final Media typing = new Media(getClass().getResource("/audio/typing.mp3").toURI().toString());
        keyboardPlayer = new MediaPlayer(typing);
        keyboardPlayer.setOnEndOfMedia(() -> keyboardPlayer.seek(Duration.ZERO));

        logger.info("Loading telephone MediaPlayer");
        final Media ringing = new Media(getClass().getResource("/audio/telephone.mp3").toURI().toString());
        phonePlayer = new MediaPlayer(ringing);
        phonePlayer.setOnEndOfMedia(() -> phonePlayer.seek(Duration.ZERO));

        logger.info("Loading chatter MediaPlayer");
        final Media office = new Media(getClass().getResource("/audio/office.mp3").toURI().toString());
        chatterPlayer = new MediaPlayer(office);
        chatterPlayer.setOnEndOfMedia(() -> chatterPlayer.seek(Duration.ZERO));

        logger.info("Loading traffic MediaPlayer");
        final Media traffic = new Media(getClass().getResource("/audio/traffic.mp3").toURI().toString());
        trafficPlayer = new MediaPlayer(traffic);
        trafficPlayer.setOnEndOfMedia(() -> trafficPlayer.seek(Duration.ZERO));

        logger.info("Loading fireplace MediaPlayer");
        final Media fireplace = new Media(getClass().getResource("/audio/fireplace.mp3").toURI().toString());
        fireplacePlayer = new MediaPlayer(fireplace);
        fireplacePlayer.setOnEndOfMedia(() -> fireplacePlayer.seek(Duration.ZERO));

        objectMapper = new ObjectMapper();
        setSoundPreset(soundPreset);
    }

    /**
     * Play the rain sound
     */
    public final void playRain() {
        if (!rainPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            logger.info("Playing rain sound");
            rainPlayer.play();
        }
    }

    /**
     * Stop the rain sound
     */
    public final void stopRain() {
        logger.info("Stopping rain sound");
        rainPlayer.stop();
    }

    /**
     * Set the volume of the rain sound
     *
     * @param rainVolume The volume of the rain sound (between 0 and 1)
     */
    public final void setRainVolume(final double rainVolume) {
        if (rainVolume < 0)
            throw new IllegalArgumentException("Rain volume cannot be smaller than 0!");
        if (rainVolume > 1)
            throw new IllegalArgumentException("Rain volume cannot be larger than 1!");

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Setting rain volume to %s", rainVolume));
        }

        rainPlayer.setVolume(rainVolume);
        soundPreset.setRainVolume(rainVolume);
    }

    /**
     * Play the wind sound
     */
    public final void playWind() {
        if (!windPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            logger.info("Playing wind sound");
            windPlayer.play();
        }
    }

    /**
     * Stop the wind sound
     */
    public final void stopWind() {
        logger.info("Stopping wind sound");
        windPlayer.stop();
    }

    /**
     * Set the volume of the wind sound
     *
     * @param windVolume The volume of the wind sound (between 0 and 1)
     */
    public final void setWindVolume(final double windVolume) {
        if (windVolume < 0)
            throw new IllegalArgumentException("Wind volume cannot be smaller than 0!");
        if (windVolume > 1)
            throw new IllegalArgumentException("Wind volume cannot be larger than 1!");

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Setting wind volume to %s", windVolume));
        }

        windPlayer.setVolume(windVolume);
        soundPreset.setWindVolume(windVolume);
    }

    /**
     * Play the thunder sound
     */
    public final void playThunder() {
        if (!thunderPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            logger.info("Playing thunder sound");
            thunderPlayer.play();
        }
    }

    /**
     * Stop the thunder sound
     */
    public final void stopThunder() {
        logger.info("Stopping thunder sound");
        thunderPlayer.stop();
    }

    /**
     * Set the volume of the thunder sound
     *
     * @param thunderVolume The volume of the thunder sound (between 0 and 1)
     */
    public final void setThunderVolume(final double thunderVolume) {
        if (thunderVolume < 0)
            throw new IllegalArgumentException("Thunder volume cannot be smaller than 0!");
        if (thunderVolume > 1)
            throw new IllegalArgumentException("Thunder volume cannot be larger than 1!");

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Setting thunder volume to %s", thunderVolume));
        }

        thunderPlayer.setVolume(thunderVolume);
        soundPreset.setThunderVolume(thunderVolume);
    }

    /**
     * Play bird sounds
     */
    public final void playBirds() {
        if (!birdPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            logger.info("Playing bird sounds");
            birdPlayer.play();
        }
    }

    /**
     * Stop bird sounds
     */
    public final void stopBirds() {
        logger.info("Stopping bird sounds");
        birdPlayer.stop();
    }

    /**
     * Set the volume of the bird sounds
     *
     * @param birdsVolume The volume of the bird sounds (between 0 and 1)
     */
    public final void setBirdsVolume(final double birdsVolume) {
        if (birdsVolume < 0)
            throw new IllegalArgumentException("Bird volume cannot be smaller than 0!");
        if (birdsVolume > 1)
            throw new IllegalArgumentException("Bird volume cannot be larger than 1!");

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Setting bird volume to %s", birdsVolume));
        }

        birdPlayer.setVolume(birdsVolume);
        soundPreset.setBirdsVolume(birdsVolume);
    }

    /**
     * Play the keyboard sound
     */
    public final void playKeyboard() {
        if (!keyboardPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            logger.info("Playing keyboard sound");
            keyboardPlayer.play();
        }
    }

    /**
     * Stop the keyboard sound
     */
    public final void stopKeyboard() {
        logger.info("Stopping keyboard sound");
        keyboardPlayer.stop();
    }

    /**
     * Set the volume of the keyboard sound
     *
     * @param keyboardVolume The volume of the keyboard sound (between 0 and 1)
     */
    public final void setKeyboardVolume(final double keyboardVolume) {
        if (keyboardVolume < 0)
            throw new IllegalArgumentException("Keyboard volume cannot be smaller than 0!");
        if (keyboardVolume > 1)
            throw new IllegalArgumentException("Keyboard volume cannot be larger than 1!");

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Setting keyboard volume to %s", keyboardVolume));
        }

        keyboardPlayer.setVolume(keyboardVolume);
        soundPreset.setKeyboardVolume(keyboardVolume);
    }

    /**
     * Play the phone sound
     */
    public final void playPhone() {
        if (!phonePlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            logger.info("Playing telephone sound");
            phonePlayer.play();
        }
    }

    /**
     * Stop the phone sound
     */
    public final void stopPhone() {
        logger.info("Stopping telephone sound");
        phonePlayer.stop();
    }

    /**
     * Set the volume of the telephone sound
     *
     * @param phoneVolume The volume of the telephone sound (between 0 and 1)
     */
    public final void setPhoneVolume(final double phoneVolume) {
        if (phoneVolume < 0)
            throw new IllegalArgumentException("Telephone volume cannot be smaller than 0!");
        if (phoneVolume > 1)
            throw new IllegalArgumentException("Telephone volume cannot be larger than 1!");

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Setting telephone volume to %s", phoneVolume));
        }

        phonePlayer.setVolume(phoneVolume);
        soundPreset.setPhoneVolume(phoneVolume);
    }

    /**
     * Play the chatter sound
     */
    public final void playChatter() {
        if (!chatterPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            logger.info("Playing chatter sound");
            chatterPlayer.play();
        }
    }

    /**
     * Stop the chatter sound
     */
    public final void stopChatter() {
        logger.info("Stopping chatter sound");
        chatterPlayer.stop();
    }

    /**
     * Set the volume of the chatter sound
     *
     * @param chatterVolume The volume of the chatter sound (between 0 and 1)
     */
    public final void setChatterVolume(final double chatterVolume) {
        if (chatterVolume < 0)
            throw new IllegalArgumentException("Chatter volume cannot be smaller than 0!");
        if (chatterVolume > 1)
            throw new IllegalArgumentException("Chatter volume cannot be larger than 1!");

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Setting chatter volume to %s", chatterVolume));
        }

        chatterPlayer.setVolume(chatterVolume);
        soundPreset.setChatterVolume(chatterVolume);
    }

    /**
     * Play the traffic sound
     */
    public final void playTraffic() {
        if (!trafficPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            logger.info("Playing traffic sound");
            trafficPlayer.play();
        }
    }

    /**
     * Stop the traffic sound
     */
    public final void stopTraffic() {
        logger.info("Stopping traffic sound");
        trafficPlayer.stop();
    }

    /**
     * Set the volume of the traffic sound
     *
     * @param trafficVolume The volume of the traffic sound (between 0 and 1)
     */
    public final void setTrafficVolume(final double trafficVolume) {
        if (trafficVolume < 0)
            throw new IllegalArgumentException("Traffic volume cannot be smaller than 0!");
        if (trafficVolume > 1)
            throw new IllegalArgumentException("Traffic volume cannot be larger than 1!");

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Setting traffic volume to %s", trafficVolume));
        }

        trafficPlayer.setVolume(trafficVolume);
        soundPreset.setTrafficVolume(trafficVolume);
    }

    /**
     * Play the fireplace sound
     */
    public final void playFireplace() {
        if (!fireplacePlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            logger.info("Playing fireplace sound");
            fireplacePlayer.play();
        }
    }

    /**
     * Stop the fireplace sound
     */
    public final void stopFireplace() {
        logger.info("Stopping fireplace sound");
        fireplacePlayer.stop();
    }

    /**
     * Set the volume of the fireplace sound
     *
     * @param fireplaceVolume The volume of the fireplace sound (between 0 and 1)
     */
    public void setFireplaceVolume(final double fireplaceVolume) {
        if (fireplaceVolume < 0)
            throw new IllegalArgumentException("Fireplace volume cannot be smaller than 0!");
        if (fireplaceVolume > 1)
            throw new IllegalArgumentException("Fireplace volume cannot be larger than 1!");

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Setting fireplace volume to %s", fireplaceVolume));
        }

        fireplacePlayer.setVolume(fireplaceVolume);
        soundPreset.setFirePlaceVolume(fireplaceVolume);
    }

    /**
     * Get the {@link com.codedead.opal.domain.SoundPreset} object
     *
     * @return The {@link com.codedead.opal.domain.SoundPreset} object
     */
    public SoundPreset getSoundPreset() {
        return soundPreset;
    }

    /**
     * Set the {@link com.codedead.opal.domain.SoundPreset} object
     *
     * @param soundPreset The {@link com.codedead.opal.domain.SoundPreset} object
     */
    public void setSoundPreset(final SoundPreset soundPreset) {
        logger.debug("Setting the SoundPreset object");
        this.soundPreset = soundPreset;

        if (soundPreset != null) {
            setRainVolume(soundPreset.getRainVolume());
            if (soundPreset.getRainVolume() <= 0) {
                stopRain();
            } else {
                playRain();
            }

            setWindVolume(soundPreset.getWindVolume());
            if (soundPreset.getWindVolume() <= 0) {
                stopWind();
            } else {
                playWind();
            }

            setThunderVolume(soundPreset.getThunderVolume());
            if (soundPreset.getThunderVolume() <= 0) {
                stopThunder();
            } else {
                playThunder();
            }

            setBirdsVolume(soundPreset.getBirdsVolume());
            if (soundPreset.getBirdsVolume() <= 0) {
                stopBirds();
            } else {
                playBirds();
            }

            setKeyboardVolume(soundPreset.getKeyboardVolume());
            if (soundPreset.getKeyboardVolume() <= 0) {
                stopKeyboard();
            } else {
                playKeyboard();
            }

            setPhoneVolume(soundPreset.getPhoneVolume());
            if (soundPreset.getPhoneVolume() <= 0) {
                stopPhone();
            } else {
                playPhone();
            }

            setChatterVolume(soundPreset.getChatterVolume());
            if (soundPreset.getChatterVolume() <= 0) {
                stopChatter();
            } else {
                playChatter();
            }

            setTrafficVolume(soundPreset.getTrafficVolume());
            if (soundPreset.getTrafficVolume() <= 0) {
                stopTraffic();
            } else {
                playTraffic();
            }

            setFireplaceVolume(soundPreset.getFirePlaceVolume());
            if (soundPreset.getFirePlaceVolume() <= 0) {
                stopFireplace();
            } else {
                playFireplace();
            }
        }
    }

    /**
     * Load a {@link com.codedead.opal.domain.SoundPreset} object from disk
     *
     * @param path The full path where the {@link com.codedead.opal.domain.SoundPreset} object is stored on disk
     * @return The {@link com.codedead.opal.domain.SoundPreset} object that could be parsed or null if the file is not a valid {@link com.codedead.opal.domain.SoundPreset} object
     * @throws IOException When the file could not be read or the data inside the file is not a {@link com.codedead.opal.domain.SoundPreset} object
     */
    public final SoundPreset loadSoundPreset(final String path) throws IOException {
        if (path == null)
            throw new NullPointerException("Path cannot be null!");
        if (path.isEmpty())
            throw new IllegalArgumentException("Path cannot be empty!");

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Loading SoundPreset from %s", path));
        }

        final Path filePath = Path.of(path);
        final String actual = Files.readString(filePath);

        if (actual == null || actual.isEmpty()) return null;
        return objectMapper.readValue(actual, SoundPreset.class);
    }

    /**
     * Save a {@link com.codedead.opal.domain.SoundPreset} object to disk
     *
     * @param soundPreset The {@link com.codedead.opal.domain.SoundPreset} object that should be stored to disk
     * @param path        The full path where the {@link com.codedead.opal.domain.SoundPreset} object should be stored on disk
     * @throws IOException When the {@link com.codedead.opal.domain.SoundPreset} object could not be stored to disk
     */
    public final void saveSoundPreset(final SoundPreset soundPreset, final String path) throws IOException {
        if (soundPreset == null)
            throw new NullPointerException("SoundPreset cannot be null!");
        if (path == null)
            throw new NullPointerException("Path cannot be null!");
        if (path.isEmpty())
            throw new IllegalArgumentException("Path cannot be empty!");

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Saving SoundPreset to %s", path));
        }

        objectMapper.writeValue(new File(path), soundPreset);
    }
}
