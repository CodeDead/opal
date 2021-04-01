package com.codedead.opal.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class SettingsController {

    private static final Logger logger = LogManager.getLogger(SettingsController.class);

    private String propertiesFileLocation;
    private String propertiesResourceLocation;
    private Properties properties;

    /**
     * Initialize a new SettingsController
     *
     * @param fileLocation               The location of the file that contains the properties
     * @param propertiesResourceLocation The location of the default properties file in the resources
     * @throws IOException When the properties file could not be loaded
     */
    public SettingsController(final String fileLocation,
                              final String propertiesResourceLocation) throws IOException {
        logger.info("Initializing new SettingsController object");

        setPropertiesFileLocation(fileLocation);
        setPropertiesResourceLocation(propertiesResourceLocation);

        try {
            properties = readPropertiesFile();
        } catch (final FileNotFoundException ex) {
            logger.error("Properties object could not be loaded", ex);
            createDefaultProperties();
            properties = readPropertiesFile();
        }
    }

    /**
     * Create the default properties file from resources
     *
     * @throws IOException When the default properties file could not be read from the application resources or a pre-existing properties file could not be deleted
     */
    public final void createDefaultProperties() throws IOException {
        logger.info("Attempting to create the default properties file");

        final Path propertiesPath = Paths.get(getPropertiesFileLocation());
        if (Files.exists(propertiesPath)) {
            logger.info("Default properties file already exists, deleting the previous version");
            Files.delete(propertiesPath);
        }

        try (final InputStream is = getClass().getClassLoader().getResourceAsStream(getPropertiesResourceLocation())) {
            if (is != null) {
                if (logger.isInfoEnabled()) {
                    logger.info(String.format("Creating default properties file at %s", getPropertiesFileLocation()));
                }
                Files.copy(is, propertiesPath);
                logger.info("Default properties file created");
            } else {
                throw new IOException(String.format("Could not load default properties from application resources (%s)!", getPropertiesResourceLocation()));
            }
        }
    }

    /**
     * Get the resource location of the default properties file
     *
     * @return The resource location of the default properties file
     */
    public final String getPropertiesResourceLocation() {
        return propertiesResourceLocation;
    }

    /**
     * Set the resource location of the default properties file
     *
     * @param propertiesResourceLocation The resource location of the default properties file
     */
    public final void setPropertiesResourceLocation(final String propertiesResourceLocation) {
        if (propertiesResourceLocation == null || propertiesResourceLocation.isEmpty())
            throw new IllegalArgumentException("Properties resource location cannot be null!");

        this.propertiesResourceLocation = propertiesResourceLocation;
    }

    /**
     * Get the properties file location
     *
     * @return The properties file location
     */
    public final String getPropertiesFileLocation() {
        return propertiesFileLocation;
    }

    /**
     * Set the properties file location
     *
     * @param propertiesFileLocation The properties file location
     */
    public final void setPropertiesFileLocation(final String propertiesFileLocation) {
        if (propertiesFileLocation == null || propertiesFileLocation.isEmpty())
            throw new IllegalArgumentException("Properties file location cannot be null or empty!");

        this.propertiesFileLocation = propertiesFileLocation;
    }

    /**
     * Get the Properties object
     *
     * @return The Properties object
     */
    public final Properties getProperties() {
        return properties;
    }

    /**
     * Set the Properties object
     *
     * @param properties The properties object
     */
    public final void setProperties(final Properties properties) {
        this.properties = properties;
    }

    /**
     * Save the properties
     *
     * @throws IOException When the Properties object could not be stored
     */
    public final void saveProperties() throws IOException {
        logger.info("Attempting to store the Properties object");
        try (final FileOutputStream fos = new FileOutputStream(getPropertiesFileLocation())) {
            properties.store(fos, null);
        }
        logger.info("Properties object stored");
    }

    /**
     * Retrieve the Properties object
     *
     * @return The Properties object
     * @throws IOException When the properties file could not be loaded
     */
    public final Properties readPropertiesFile() throws IOException {
        logger.info("Attempting to load the Properties object");
        try (final FileInputStream fis = new FileInputStream(getPropertiesFileLocation())) {
            final Properties prop = new Properties();

            prop.load(fis);

            logger.info("Properties object loaded");
            return prop;
        }
    }
}
