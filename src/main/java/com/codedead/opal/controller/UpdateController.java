package com.codedead.opal.controller;

import com.codedead.opal.domain.InvalidHttpResponseCodeException;
import com.codedead.opal.domain.PlatformUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public final class UpdateController {

    private static final Logger logger = LogManager.getLogger(UpdateController.class);

    private final ObjectMapper objectMapper;

    /**
     * Initialize a new UpdateController
     */
    public UpdateController() {
        logger.info("Initializing new UpdateController object");
        objectMapper = new ObjectMapper();
    }

    /**
     * Retrieve a ({@link List}) of {@link com.codedead.opal.domain.PlatformUpdate} objects
     *
     * @param url The URL where the {@link List} of {@link com.codedead.opal.domain.PlatformUpdate} objects can be retrieved
     * @return The {@link List} of {@link com.codedead.opal.domain.PlatformUpdate} objects
     * @throws IOException                      When the HTTP request failed or when the response could not be parsed as a {@link List} of {@link com.codedead.opal.domain.PlatformUpdate} objects
     * @throws InterruptedException             When the HTTP request is interrupted
     * @throws InvalidHttpResponseCodeException When the HTTP response status code is not OK
     */
    public final List<PlatformUpdate> getUpdates(final String url) throws IOException, InterruptedException, InvalidHttpResponseCodeException {
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Attempting to retrieve List of PlatformUpdate objects from %s", url));
        }

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return Arrays.asList(objectMapper.readValue(response.body(), PlatformUpdate[].class));
        }

        throw new InvalidHttpResponseCodeException(String.format("Invalid HTTP response code (%s)", response.statusCode()));
    }
}
