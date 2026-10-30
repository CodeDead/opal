package com.codedead.opal.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SettingsControllerTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    void storesNewPropertiesAndReplacesTheLiveProperties() throws IOException {
        final Path propertiesFile = temporaryDirectory.resolve("opal.properties");
        Files.writeString(propertiesFile, "locale=de-DE\ntheme=dark\n");

        final SettingsController settingsController = new SettingsController(propertiesFile.toString(), "default.properties");
        final Properties updatedProperties = new Properties();
        updatedProperties.putAll(settingsController.getProperties());
        updatedProperties.setProperty("theme", "light");

        settingsController.saveProperties(updatedProperties);

        assertEquals("light", settingsController.getProperties().getProperty("theme"));
        assertEquals("de-DE", settingsController.getProperties().getProperty("locale"));

        final Properties storedProperties = settingsController.readPropertiesFile();
        assertEquals("light", storedProperties.getProperty("theme"));
        assertEquals("de-DE", storedProperties.getProperty("locale"));
    }

    @Test
    void keepsTheLivePropertiesWhenStoringFails() throws IOException {
        final Path propertiesFile = temporaryDirectory.resolve("opal.properties");
        Files.writeString(propertiesFile, "locale=en-US\n");

        final SettingsController settingsController = new SettingsController(propertiesFile.toString(), "default.properties");
        final Properties originalProperties = settingsController.getProperties();

        Files.delete(propertiesFile);
        Files.createDirectory(propertiesFile);

        final Properties updatedProperties = new Properties();
        updatedProperties.putAll(originalProperties);
        updatedProperties.setProperty("theme", "dark");

        assertThrows(IOException.class, () -> settingsController.saveProperties(updatedProperties));
        assertSame(originalProperties, settingsController.getProperties());
        assertEquals("en-US", settingsController.getProperties().getProperty("locale"));
    }
}
