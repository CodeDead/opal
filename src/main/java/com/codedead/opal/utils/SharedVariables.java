package com.codedead.opal.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class SharedVariables {

    public static final String ICON_URL = "/images/opal.png";
    public static final String APP_VERSION = loadVersion();
    public static final String CURRENT_VERSION = APP_VERSION + ".0";
    public static final boolean PORTABLE = false;
    public static final String DEFAULT_LOCALE = "en-US";

    public static final String PROPERTIES_BASE_PATH = PORTABLE
            ? System.getProperty("user.dir") + "/.com.codedead.opal"
            : System.getProperty("user.home") + "/.config/com.codedead.opal";

    public static final String PROPERTIES_RESOURCE_LOCATION = "default.properties";
    public static final String PROPERTIES_FILE_LOCATION = PROPERTIES_BASE_PATH + "/opal.properties";
    public static final String PROXY_PASSWORD_KEY_FILE_LOCATION = PROPERTIES_BASE_PATH + "/proxy.key";

    public static final String HELP_DOCUMENTATION_RESOURCE_LOCATION = "/documents/help.pdf";
    public static final String HELP_DOCUMENTATION_FILE_LOCATION = PROPERTIES_BASE_PATH + "/help.pdf";

    public static final String LICENSE_RESOURCE_LOCATION = "/documents/license.pdf";
    public static final String LICENSE_FILE_LOCATION = PROPERTIES_BASE_PATH + "/license.pdf";

    /**
     * Load the application version from the bundled version.properties file
     *
     * @return The application version
     */
    private static String loadVersion() {
        try (final InputStream inputStream = SharedVariables.class.getResourceAsStream("/version.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("Unable to find version.properties");
            }

            final Properties properties = new Properties();
            properties.load(inputStream);

            final String version = properties.getProperty("version");
            if (version == null || version.isBlank()) {
                throw new IllegalStateException("The version property is missing from version.properties");
            }

            return version;
        } catch (final IOException ex) {
            throw new IllegalStateException("Unable to load version.properties", ex);
        }
    }

    /**
     * Initialize a new SharedVariables
     */
    private SharedVariables() {
        // Private constructor
    }
}
