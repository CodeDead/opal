package com.codedead.opal.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateControllerTest {

    private static final String UPDATE_URL = "https://example.com/updates.json";
    private static final String CURRENT_VERSION = "1.0.0.0";

    @Test
    void rejectsNullProxyHostWhenEnabled() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertThrows(NullPointerException.class, () -> updateController.setProxy(true, null, 8080));
    }

    @Test
    void rejectsBlankProxyHostWhenEnabled() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertThrows(IllegalArgumentException.class, () -> updateController.setProxy(true, " ", 8080));
    }

    @Test
    void rejectsInvalidProxyPort() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertThrows(IllegalArgumentException.class, () -> updateController.setProxy(true, "proxy.example.com", 0));
        assertThrows(IllegalArgumentException.class, () -> updateController.setProxy(true, "proxy.example.com", 65536));
    }

    @Test
    void allowsDisabledProxyWithoutConfiguration() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertDoesNotThrow(() -> updateController.setProxy(false, null, 0));
    }

    @Test
    void acceptsValidProxyConfiguration() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertDoesNotThrow(() -> updateController.setProxy(true, "proxy.example.com", 8080));
    }

    @Test
    void rejectsNullOrBlankUsernameWhenAuthenticationIsEnabled() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertThrows(NullPointerException.class, () -> updateController.setProxyAuthentication(true, null, "password"));
        assertThrows(IllegalArgumentException.class, () -> updateController.setProxyAuthentication(true, " ", "password"));
    }

    @Test
    void rejectsNullOrEmptyPasswordWhenAuthenticationIsEnabled() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertThrows(NullPointerException.class, () -> updateController.setProxyAuthentication(true, "username", null));
        assertThrows(IllegalArgumentException.class, () -> updateController.setProxyAuthentication(true, "username", ""));
    }

    @Test
    void allowsDisabledAuthenticationWithoutCredentials() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertDoesNotThrow(() -> updateController.setProxyAuthentication(false, null, null));
    }

    @Test
    void acceptsValidProxyAuthentication() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertDoesNotThrow(() -> updateController.setProxyAuthentication(true, "username", "password"));
    }
}
