package com.codedead.opal.controller;

import com.codedead.opal.domain.InvalidHttpResponseCodeException;
import com.codedead.opal.domain.PlatformUpdate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class UpdateController {

    private String updateUrl;
    private String currentVersion;

    private final Logger logger;
    private final ObjectMapper objectMapper;

    /**
     * Initialize a new UpdateController
     *
     * @param updateUrl      The URL that can be used to check for updates
     * @param currentVersion The current application version
     */
    public UpdateController(final String updateUrl, final String currentVersion) {
        logger = LogManager.getLogger(UpdateController.class);
        logger.info("Initializing new UpdateController object");

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        setUpdateUrl(updateUrl);
        setCurrentVersion(currentVersion);
    }

    /**
     * Check for updates
     *
     * @param currentPlatform The current platform
     * @param isPortable      Check for portable application updates or not
     * @return The {@link Optional} {@link PlatformUpdate} object
     * @throws InterruptedException             When the retrieval of the {@link List} of {@link PlatformUpdate} objects was interrupted
     * @throws InvalidHttpResponseCodeException When an invalid HTTP response code was returned when checking for updates
     * @throws IOException                      When the response that was returned from the server could not be serialized as a {@link List} of {@link PlatformUpdate} object
     */
    public Optional<PlatformUpdate> checkForUpdates(final String currentPlatform, final boolean isPortable) throws InterruptedException, InvalidHttpResponseCodeException, IOException {
        if (currentPlatform == null)
            throw new NullPointerException("Current platform cannot be null!");
        if (currentPlatform.isEmpty())
            throw new IllegalArgumentException("Current platform cannot be empty!");

        logger.info("Attempting to retrieve the latest PlatformUpdate objects");

        final List<PlatformUpdate> updates = getUpdates();

        final Optional<PlatformUpdate> update = updates
                .stream()
                .filter(e -> e.getPlatformName().equalsIgnoreCase(currentPlatform) && e.isPortable() == isPortable)
                .findFirst();

        if (update.isPresent()) {
            final PlatformUpdate platformUpdate = update.get();
            if (versionCompare(currentVersion, platformUpdate.getMajorVersion() + "." + platformUpdate.getMinorVersion() + "." + platformUpdate.getBuildVersion() + "." + platformUpdate.getRevisionVersion()) < 0) {
                return update;
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    /**
     * Compare to strings to check which version is larger
     *
     * @param v1 The first {@link String} object that contains a version
     * @param v2 The second {@link String} object that contains a version
     * @return 1 if v1 is larger than v2, -1 if v2 is larger than v1 and 0 if both are equal
     */
    private int versionCompare(final String v1, final String v2) {
        int vnum1 = 0;
        int vnum2 = 0;

        for (int i = 0, j = 0; (i < v1.length() || j < v2.length()); ) {
            while (i < v1.length() && v1.charAt(i) != '.') {
                vnum1 = vnum1 * 10 + (v1.charAt(i) - '0');
                i++;
            }
            while (j < v2.length() && v2.charAt(j) != '.') {
                vnum2 = vnum2 * 10 + (v2.charAt(j) - '0');
                j++;
            }

            if (vnum1 > vnum2)
                return 1;
            if (vnum2 > vnum1)
                return -1;

            vnum1 = vnum2 = 0;
            i++;
            j++;
        }
        return 0;
    }

    /**
     * Retrieve a ({@link List}) of {@link PlatformUpdate} objects
     *
     * @return The {@link List} of {@link PlatformUpdate} objects
     * @throws IOException                      When the HTTP request failed or when the response could not be parsed as a {@link List} of {@link PlatformUpdate} objects
     * @throws InterruptedException             When the HTTP request is interrupted
     * @throws InvalidHttpResponseCodeException When the HTTP response status code is not OK
     */
    public List<PlatformUpdate> getUpdates() throws IOException, InterruptedException, InvalidHttpResponseCodeException {
        logger.info("Attempting to retrieve List of PlatformUpdate objects from {}", getUpdateUrl());

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getUpdateUrl()))
                .build();

        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return Arrays.asList(objectMapper.readValue(response.body(), PlatformUpdate[].class));
        }

        throw new InvalidHttpResponseCodeException(String.format("Invalid HTTP response code (%s)", response.statusCode()));
    }

    /**
     * Download a file from a URL to the local filesystem
     *
     * @param url  The URL of the file that should be downloaded
     * @param path The path on the local filesystem where the file should be stored
     * @throws IOException If the transfer failed
     */
    public void downloadFile(final String url, final String path) throws IOException {
        if (url == null)
            throw new NullPointerException("URL cannot be null!");
        if (url.isEmpty())
            throw new IllegalArgumentException("URL cannot be empty!");
        if (path == null)
            throw new NullPointerException("Path cannot be null!");
        if (path.isEmpty())
            throw new IllegalArgumentException("Path cannot be empty!");

        logger.info("Attempting to download file from {} and store it at {}", url, path);

        final URL website = new URL(url);
        try (final InputStream inputStream = website.openStream()) {
            try (final ReadableByteChannel rbc = Channels.newChannel(inputStream)) {
                try (final FileOutputStream fos = new FileOutputStream(path)) {
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                }
            }
        }
    }

    /**
     * Get the update URL
     *
     * @return The update URL
     */
    public String getUpdateUrl() {
        return updateUrl;
    }

    /**
     * Set the update URL
     *
     * @param updateUrl The update URL
     */
    public void setUpdateUrl(final String updateUrl) {
        if (updateUrl == null)
            throw new NullPointerException("Update URL cannot be null!");
        if (updateUrl.isEmpty())
            throw new IllegalArgumentException("Update URL cannot be empty!");

        this.updateUrl = updateUrl;
    }

    /**
     * Get the current version
     *
     * @return The current version
     */
    public String getCurrentVersion() {
        return currentVersion;
    }

    /**
     * Set the current version
     *
     * @param currentVersion The current version
     */
    public void setCurrentVersion(final String currentVersion) {
        if (currentVersion == null)
            throw new NullPointerException("currentVersion cannot be null!");
        if (currentVersion.isEmpty())
            throw new IllegalArgumentException("currentVersion cannot be empty!");

        this.currentVersion = currentVersion;
    }
}
