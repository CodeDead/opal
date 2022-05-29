package com.codedead.opal.controller;

import com.codedead.opal.interfaces.IAudioTimer;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.*;

public final class AudioController {

    private Map<String, Double> mediaVolumes;

    private final Map<String, MediaPlayer> mediaPlayers;
    private final Timer timer;
    private TimerTask timerTask;

    private boolean timerEnabled;

    private final IAudioTimer audioTimer;
    private final ObjectMapper objectMapper;
    private final Logger logger;

    /**
     * Initialize a new AudioController
     *
     * @param audioTimer The {@link IAudioTimer} interface that can be used to call back certain timer functionalities
     * @throws URISyntaxException When the URI syntax is incorrect
     */
    public AudioController(final IAudioTimer audioTimer) throws URISyntaxException {
        logger = LogManager.getLogger(AudioController.class);

        logger.info("Initializing new AudioController object");
        logger.info("Initializing MediaPlayer objects");

        mediaPlayers = new HashMap<>();
        mediaPlayers.put("rain", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/rain.mp3")).toURI().toString())));
        mediaPlayers.put("wind", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/wind.mp3")).toURI().toString())));
        mediaPlayers.put("thunder", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/thunder.mp3")).toURI().toString())));
        mediaPlayers.put("birds", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/birds.mp3")).toURI().toString())));
        mediaPlayers.put("river", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/river.mp3")).toURI().toString())));
        mediaPlayers.put("keyboard", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/typing.mp3")).toURI().toString())));
        mediaPlayers.put("telephone", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/telephone.mp3")).toURI().toString())));
        mediaPlayers.put("officeChatter", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/office.mp3")).toURI().toString())));
        mediaPlayers.put("traffic", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/traffic.mp3")).toURI().toString())));
        mediaPlayers.put("clock", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/clock.mp3")).toURI().toString())));
        mediaPlayers.put("fireplace", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/fireplace.mp3")).toURI().toString())));
        mediaPlayers.put("static", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/static.mp3")).toURI().toString())));
        mediaPlayers.put("fantasy", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/fantasy.mp3")).toURI().toString())));
        mediaPlayers.put("fan", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/fan.mp3")).toURI().toString())));
        mediaPlayers.put("cave", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/cave.mp3")).toURI().toString())));
        mediaPlayers.put("frogs", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/frogs.mp3")).toURI().toString())));
        mediaPlayers.put("zen", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/zen.mp3")).toURI().toString())));
        mediaPlayers.put("coffee", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/coffee.mp3")).toURI().toString())));
        mediaPlayers.put("zoo", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/zoo.mp3")).toURI().toString())));
        mediaPlayers.put("networking", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/networking.mp3")).toURI().toString())));
        mediaPlayers.put("tribal", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/tribal.mp3")).toURI().toString())));
        mediaPlayers.put("drumtribal", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/tribal2.mp3")).toURI().toString())));
        mediaPlayers.put("football", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/football.mp3")).toURI().toString())));
        mediaPlayers.put("sleepy", new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/audio/sleepy.mp3")).toURI().toString())));

        mediaVolumes = new HashMap<>();
        for (final Map.Entry<String, MediaPlayer> entry : mediaPlayers.entrySet()) {
            final MediaPlayer player = entry.getValue();
            player.setOnEndOfMedia(() -> player.seek(Duration.ZERO));
            mediaVolumes.put(entry.getKey(), 0.0);
        }

        timer = new Timer();

        this.audioTimer = audioTimer;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Play the {@link Media} object for a {@link MediaPlayer} object with the specified key
     *
     * @param key The key for which the {@link Media} object should be played
     */
    public void playMedia(final String key) {
        if (key == null)
            throw new NullPointerException("Key cannot be null!");
        if (key.isEmpty())
            throw new IllegalArgumentException("Key cannot be empty!");

        final MediaPlayer player = mediaPlayers.get(key);

        if (player == null)
            throw new NullPointerException(String.format("MediaPlayer with key %s cannot be found!", key));

        if (!player.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            logger.info("Playing media for MediaPlayer with key {}", key);
            player.play();
        }
    }

    /**
     * Stop playing the {@link Media} object for a {@link MediaPlayer} object with a specified key
     *
     * @param key The key for the {@link MediaPlayer} object for which the {@link Media} object should stop playing
     */
    public void stopMedia(final String key) {
        if (key == null)
            throw new NullPointerException("Key cannot be null!");
        if (key.isEmpty())
            throw new IllegalArgumentException("Key cannot be empty!");

        logger.info("Stopping media for MediaPlayer with key {}", key);

        final MediaPlayer player = mediaPlayers.get(key);

        if (player == null)
            throw new NullPointerException(String.format("MediaPlayer with key %s cannot be found!", key));

        player.stop();
    }

    /**
     * Set the volume for the {@link MediaPlayer} with the specified key
     *
     * @param key       The key for the {@link MediaPlayer} object
     * @param newVolume The new volume for the {@link MediaPlayer} with the specified key
     */
    public void setPlayerVolume(final String key, final double newVolume) {
        if (key == null)
            throw new NullPointerException("Key cannot be null!");
        if (key.isEmpty())
            throw new IllegalArgumentException("Key cannot be empty!");
        if (newVolume < 0)
            throw new IllegalArgumentException("Volume cannot be lower than 0!");
        if (newVolume > 1)
            throw new IllegalArgumentException("Volume cannot be higher than 1!");

        logger.debug("Setting volume for MediaPlayer with key {} to {}", key, newVolume);

        final MediaPlayer player = mediaPlayers.get(key);

        if (player == null)
            throw new NullPointerException(String.format("MediaPlayer with key %s cannot be found!", key));

        player.setVolume(newVolume);

        mediaVolumes.replace(key, newVolume);

        if (newVolume == 0) {
            stopMedia(key);
        } else {
            playMedia(key);
        }
    }

    /**
     * Add a new {@link MediaPlayer} object
     *
     * @param key         The key for the {@link MediaPlayer} object
     * @param mediaPlayer The {@link MediaPlayer} object that should be added
     */
    public void addPlayer(final String key, final MediaPlayer mediaPlayer) {
        if (key == null)
            throw new NullPointerException("Key cannot be null!");
        if (key.isEmpty())
            throw new IllegalArgumentException("Key cannot be empty!");
        if (mediaPlayer == null)
            throw new NullPointerException("MediaPlayer cannot be null!");

        logger.info("Adding MediaPlayer with key {}", key);

        if (mediaPlayers.containsKey(key))
            throw new IllegalArgumentException(String.format("MediaPlayer with key %s already exists!", key));

        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));

        mediaPlayers.put(key, mediaPlayer);
        mediaVolumes.put(key, 0.0);
    }

    /**
     * Delete the entry for the {@link MediaPlayer} with the specified key
     *
     * @param key The key of the {@link MediaPlayer} that should be removed
     */
    public void deletePlayer(final String key) {
        if (key == null)
            throw new NullPointerException("Key cannot be null!");
        if (key.isEmpty())
            throw new IllegalArgumentException("Key cannot be empty!");

        logger.info("Deleting MediaPlayer with key {}", key);

        mediaPlayers.remove(key);
        mediaVolumes.remove(key);
    }

    /**
     * Load a sound preset from disk
     *
     * @param path The full path where the sound preset is stored on disk
     * @throws IOException When the file could not be read or the data inside the file could not be parsed
     */
    public void loadSoundPreset(final String path) throws IOException {
        if (path == null)
            throw new NullPointerException("Path cannot be null!");
        if (path.isEmpty())
            throw new IllegalArgumentException("Path cannot be empty!");

        logger.info("Loading sound preset from {}", path);

        final Path filePath = Path.of(path);
        final String actual = Files.readString(filePath);

        if (actual == null || actual.isEmpty())
            throw new IllegalArgumentException("Sound preset cannot be null or empty!");

        final TypeReference<HashMap<String, Double>> typeRef = new TypeReference<>() {
        };

        mediaVolumes = objectMapper.readValue(actual, typeRef);

        for (Map.Entry<String, Double> entry : mediaVolumes.entrySet()) {
            setPlayerVolume(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Save the volume settings to disk
     *
     * @param path The full path where the volume settings should be stored on disk
     * @throws IOException When the media volumes could not be stored to disk
     */
    public void saveSoundPreset(final String path) throws IOException {
        if (path == null)
            throw new NullPointerException("Path cannot be null!");
        if (path.isEmpty())
            throw new IllegalArgumentException("Path cannot be empty!");

        logger.info("Saving sound preset to {}", path);

        objectMapper.writeValue(new File(path), mediaVolumes);
    }

    /**
     * Get the {@link Set} that contains the keys and volume values for all {@link MediaPlayer} objects
     *
     * @return The {@link Set} that contains the keys and volume values for all {@link MediaPlayer} objects
     */
    public Set<Map.Entry<String, Double>> getVolumes() {
        return mediaVolumes.entrySet();
    }

    /**
     * Cancel the {@link Timer} object
     */
    public void cancelTimer() {
        logger.info("Cancelling the Timer to stop all MediaPlayer objects");

        timerEnabled = false;

        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }

        if (audioTimer != null) {
            audioTimer.cancelled();
        }
    }

    /**
     * Schedule the {@link Timer} object to cancel all {@link MediaPlayer} objects
     *
     * @param delay The delay in milliseconds before the {@link Timer} object executes its function
     */
    public void scheduleTimer(final long delay) {
        if (delay <= 1)
            throw new IllegalArgumentException("Delay cannot be smaller than 1");

        logger.info("Scheduling the Timer to stop all MediaPlayer objects after {} millisecond(s)", delay);

        timerEnabled = true;

        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                logger.info("Timer has fired");
                if (timerEnabled) {
                    for (final String key : mediaPlayers.keySet()) {
                        stopMedia(key);
                    }
                    if (audioTimer != null) {
                        audioTimer.fired();
                    }
                }
                timerEnabled = false;
            }
        };

        timer.schedule(timerTask, delay);
    }
}
