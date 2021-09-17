package com.codedead.opal.controller;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class AudioController {

    private final Logger logger;

    private Map<String, Double> mediaVolumes;

    private final Map<String, MediaPlayer> mediaPlayers;
    private final ObjectMapper objectMapper;

    /**
     * Initialize a new AudioController
     *
     * @throws URISyntaxException When the URI syntax is incorrect
     */
    public AudioController() throws URISyntaxException {
        logger = LogManager.getLogger(AudioController.class);

        logger.info("Initializing new AudioController object");
        logger.info("Initializing MediaPlayer objects");

        mediaPlayers = new HashMap<>();
        mediaPlayers.put("rain", new MediaPlayer(new Media(getClass().getResource("/audio/rain.mp3").toURI().toString())));
        mediaPlayers.put("wind", new MediaPlayer(new Media(getClass().getResource("/audio/wind.mp3").toURI().toString())));
        mediaPlayers.put("thunder", new MediaPlayer(new Media(getClass().getResource("/audio/thunder.mp3").toURI().toString())));
        mediaPlayers.put("birds", new MediaPlayer(new Media(getClass().getResource("/audio/birds.mp3").toURI().toString())));
        mediaPlayers.put("river", new MediaPlayer(new Media(getClass().getResource("/audio/river.mp3").toURI().toString())));
        mediaPlayers.put("keyboard", new MediaPlayer(new Media(getClass().getResource("/audio/typing.mp3").toURI().toString())));
        mediaPlayers.put("telephone", new MediaPlayer(new Media(getClass().getResource("/audio/telephone.mp3").toURI().toString())));
        mediaPlayers.put("officeChatter", new MediaPlayer(new Media(getClass().getResource("/audio/office.mp3").toURI().toString())));
        mediaPlayers.put("traffic", new MediaPlayer(new Media(getClass().getResource("/audio/traffic.mp3").toURI().toString())));
        mediaPlayers.put("clock", new MediaPlayer(new Media(getClass().getResource("/audio/clock.mp3").toURI().toString())));
        mediaPlayers.put("fireplace", new MediaPlayer(new Media(getClass().getResource("/audio/fireplace.mp3").toURI().toString())));

        mediaVolumes = new HashMap<>();
        for (final Map.Entry<String, MediaPlayer> entry : mediaPlayers.entrySet()) {
            final MediaPlayer player = entry.getValue();
            player.setOnEndOfMedia(() -> player.seek(Duration.ZERO));
            mediaVolumes.put(entry.getKey(), 0.0);
        }

        objectMapper = new ObjectMapper();
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

        final TypeReference<HashMap<String, Double>> typeRef = new TypeReference<>() {};

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
}
