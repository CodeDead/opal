package com.codedead.opal.controller;

import com.codedead.opal.domain.InvalidHttpResponseCodeException;
import com.codedead.opal.domain.PlatformRelease;
import com.codedead.opal.domain.PlatformUpdate;
import com.codedead.opal.domain.Semver;
import com.codedead.opal.domain.VersionInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class UpdateController {

    private String updateUrl;
    private String currentVersion;
    private ProxySelector proxySelector;
    private boolean proxyAuthEnabled;
    private String proxyUsername;
    private char[] proxyPassword;

    private final Logger logger;
    private final JsonMapper jsonMapper;

    /**
     * Initialize a new UpdateController
     *
     * @param updateUrl      The URL that can be used to check for updates
     * @param currentVersion The current application version
     */
    public UpdateController(final String updateUrl, final String currentVersion) {
        logger = LogManager.getLogger(UpdateController.class);
        logger.info("Initializing new UpdateController object");

        jsonMapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        setUpdateUrl(updateUrl);
        setCurrentVersion(currentVersion);
    }

    /**
     * Check for updates
     *
     * @param currentPlatform The current platform
     * @param currentArch     The current architecture
     * @param isPortable      Check for portable application updates or not
     * @return The {@link Optional} {@link PlatformUpdate} object
     * @throws InterruptedException             When the retrieval of the {@link VersionInfo} object was interrupted
     * @throws InvalidHttpResponseCodeException When an invalid HTTP response code was returned when checking for updates
     * @throws IOException                      When the response from the server could not be serialized as a {@link VersionInfo} object
     */
    public Optional<PlatformUpdate> checkForUpdates(final String currentPlatform, final String currentArch, final boolean isPortable) throws InterruptedException, InvalidHttpResponseCodeException, IOException {
        if (currentPlatform == null)
            throw new NullPointerException("Current platform cannot be null!");
        if (currentPlatform.isBlank())
            throw new IllegalArgumentException("Current platform cannot be blank!");
        if (currentArch == null)
            throw new NullPointerException("Current architecture cannot be null!");
        if (currentArch.isBlank())
            throw new IllegalArgumentException("Current architecture cannot be blank!");

        logger.info("Attempting to retrieve the latest VersionInfo object");

        final VersionInfo version = getVersion();
        return resolveUpdate(version, currentPlatform, currentArch, isPortable, currentVersion);
    }

    /**
     * Resolve an available update from a version response
     *
     * @param version         The version response
     * @param currentPlatform The current platform
     * @param currentArch     The current architecture
     * @param isPortable      Whether a portable release is required
     * @param currentVersion  The current application version
     * @return The {@link Optional} {@link PlatformUpdate} object
     */
    static Optional<PlatformUpdate> resolveUpdate(final VersionInfo version, final String currentPlatform, final String currentArch, final boolean isPortable, final String currentVersion) {
        if (version == null)
            throw new NullPointerException("VersionInfo cannot be null!");
        if (version.getSemver() == null || version.getSemver().isBlank())
            throw new IllegalArgumentException("The version response does not contain a semantic version!");

        final Optional<PlatformRelease> platformRelease = findPlatform(version.getPlatforms(), currentPlatform, currentArch, isPortable);
        if (platformRelease.isEmpty())
            return Optional.empty();

        if (Semver.compare(currentVersion, version.getSemver()) >= 0)
            return Optional.empty();

        final PlatformRelease release = platformRelease.get();
        if (release.getDownloadUrl() == null || release.getDownloadUrl().isBlank())
            throw new IllegalArgumentException("The selected release does not contain a download URL!");

        final PlatformUpdate update = new PlatformUpdate();
        update.setSemver(version.getSemver());
        update.setPlatformName(release.getName());
        update.setArch(release.getArch());
        update.setPortable(isPortable);
        update.setDownloadUrl(release.getDownloadUrl());
        update.setInfoUrl(release.getInfoUrl());
        return Optional.of(update);
    }

    /**
     * Find the release that matches the provided platform, architecture and portability
     *
     * @param platforms      The available releases
     * @param platformName   The platform name
     * @param arch           The platform architecture
     * @param portable       Whether a portable release is required
     * @return The {@link Optional} {@link PlatformRelease} object
     */
    static Optional<PlatformRelease> findPlatform(final List<PlatformRelease> platforms, final String platformName, final String arch, final boolean portable) {
        if (platforms == null || platforms.isEmpty())
            return Optional.empty();

        final Optional<PlatformRelease> exactMatch = platforms.stream()
                .filter(release -> matches(release.getName(), platformName))
                .filter(release -> matches(release.getArch(), arch))
                .filter(release -> Boolean.valueOf(portable).equals(release.getPortable()))
                .findFirst();

        if (exactMatch.isPresent())
            return exactMatch;

        return platforms.stream()
                .filter(release -> matches(release.getName(), platformName))
                .filter(release -> matches(release.getArch(), arch))
                .filter(release -> release.getPortable() == null)
                .findFirst();
    }

    /**
     * Check whether two values match, ignoring case and surrounding whitespace
     *
     * @param actual   The actual value
     * @param expected The expected value
     * @return True if the values match, otherwise false
     */
    private static boolean matches(final String actual, final String expected) {
        return actual != null && expected != null && actual.trim().equalsIgnoreCase(expected.trim());
    }

    /**
     * Retrieve the latest version information
     *
     * @return The {@link VersionInfo} object
     * @throws IOException                      When the HTTP request failed or when the response could not be parsed
     * @throws InterruptedException             When the HTTP request is interrupted
     * @throws InvalidHttpResponseCodeException When the HTTP response status code is not OK
     */
    public VersionInfo getVersion() throws IOException, InterruptedException, InvalidHttpResponseCodeException {
        logger.info("Attempting to retrieve VersionInfo from {}", getUpdateUrl());

        try (final HttpClient client = createHttpClient()) {
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getUpdateUrl()))
                    .header("Accept", "application/json")
                    .build();

            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                try {
                    final VersionInfo version = jsonMapper.readValue(response.body(), VersionInfo.class);
                    if (version == null)
                        throw new IOException("The version response was empty");
                    return version;
                } catch (final JacksonException ex) {
                    throw new IOException("Unable to parse the version response", ex);
                }
            }

            throw new InvalidHttpResponseCodeException(String.format("Invalid HTTP response code (%s)", response.statusCode()));
        }
    }

    /**
     * Download a file from a URL to the local filesystem
     *
     * @param url  The URL of the file that should be downloaded
     * @param path The path on the local filesystem where the file should be stored
     * @throws IOException                      If the transfer failed
     * @throws InterruptedException             If the transfer was interrupted
     * @throws InvalidHttpResponseCodeException When the HTTP response status code is not OK
     */
    public void downloadFile(final String url, final String path) throws IOException, InterruptedException, InvalidHttpResponseCodeException {
        if (url == null)
            throw new NullPointerException("URL cannot be null!");
        if (url.isBlank())
            throw new IllegalArgumentException("URL cannot be blank!");
        if (path == null)
            throw new NullPointerException("Path cannot be null!");
        if (path.isBlank())
            throw new IllegalArgumentException("Path cannot be blank!");

        logger.info("Attempting to download file from {} and store it at {}", url, path);

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try (final HttpClient client = createHttpClient()) {
            final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200)
                throw new InvalidHttpResponseCodeException(String.format("Invalid HTTP response code (%s)", response.statusCode()));

            try (final InputStream inputStream = response.body();
                 final OutputStream outputStream = Files.newOutputStream(Path.of(path))) {
                inputStream.transferTo(outputStream);
            }
        }
    }

    /**
     * Create a new {@link HttpClient} that uses the configured proxy, if any
     *
     * @return The {@link HttpClient} object
     */
    private HttpClient createHttpClient() {
        final HttpClient.Builder builder = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL);

        if (proxySelector != null) {
            builder.proxy(proxySelector);
        }

        if (proxySelector != null && proxyAuthEnabled) {
            builder.authenticator(new ProxyAuthenticator(proxyUsername, proxyPassword));
        }

        return builder.build();
    }

    /**
     * Configure the proxy authentication that is used for update requests
     *
     * @param enabled  True if proxy authentication should be used, otherwise false
     * @param username The proxy username
     * @param password The proxy password
     */
    public void setProxyAuthentication(final boolean enabled, final String username, final String password) {
        clearProxyPassword();
        proxyAuthEnabled = false;
        proxyUsername = null;

        if (!enabled)
            return;
        if (username == null)
            throw new NullPointerException("Proxy username cannot be null!");

        final String proxyUser = username.trim();
        if (proxyUser.isBlank())
            throw new IllegalArgumentException("Proxy username cannot be blank!");
        if (password == null)
            throw new NullPointerException("Proxy password cannot be null!");
        if (password.isEmpty())
            throw new IllegalArgumentException("Proxy password cannot be empty!");

        proxyAuthEnabled = true;
        proxyUsername = proxyUser;
        proxyPassword = password.toCharArray();
    }

    /**
     * Clear the stored proxy password
     */
    private void clearProxyPassword() {
        if (proxyPassword != null) {
            Arrays.fill(proxyPassword, '\0');
            proxyPassword = null;
        }
    }

    /**
     * {@link Authenticator} that provides the configured proxy credentials
     */
    private static final class ProxyAuthenticator extends Authenticator {

        private final String username;
        private final char[] password;

        /**
         * Initialize a new ProxyAuthenticator
         *
         * @param username The proxy username
         * @param password The proxy password
         */
        private ProxyAuthenticator(final String username, final char[] password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            if (getRequestorType() == RequestorType.PROXY) {
                return new PasswordAuthentication(username, password);
            }
            return null;
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
        if (updateUrl.isBlank())
            throw new IllegalArgumentException("Update URL cannot be blank!");

        this.updateUrl = updateUrl;
    }

    /**
     * Configure the proxy that is used for update requests
     *
     * @param enabled True if a custom proxy should be used, otherwise false
     * @param host    The proxy host
     * @param port    The proxy port
     */
    public void setProxy(final boolean enabled, final String host, final int port) {
        if (!enabled) {
            proxySelector = null;
            return;
        }
        if (host == null)
            throw new NullPointerException("Proxy host cannot be null!");

        final String proxyHost = host.trim();
        if (proxyHost.isBlank())
            throw new IllegalArgumentException("Proxy host cannot be blank!");
        if (port < 1 || port > 65535)
            throw new IllegalArgumentException("Proxy port must be between 1 and 65535!");

        logger.info("Using custom proxy {}:{} for update requests", proxyHost, port);
        proxySelector = ProxySelector.of(InetSocketAddress.createUnresolved(proxyHost, port));
    }

    /**
     * Set the current version
     *
     * @param currentVersion The current version
     */
    public void setCurrentVersion(final String currentVersion) {
        if (currentVersion == null)
            throw new NullPointerException("currentVersion cannot be null!");
        if (currentVersion.isBlank())
            throw new IllegalArgumentException("currentVersion cannot be blank!");

        this.currentVersion = currentVersion;
    }
}
