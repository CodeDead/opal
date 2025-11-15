package com.codedead.opal.utils;

public final class SharedVariables {

    public static final String ICON_URL = "/images/opal.png";
    public static final String CURRENT_VERSION = "1.5.1.0";
    public static final boolean PORTABLE = false;
    public static final String DEFAULT_LOCALE = "en-US";

    public static final String PROPERTIES_BASE_PATH = PORTABLE
            ? System.getProperty("user.dir") + "/.com.codedead.opal"
            : System.getProperty("user.home") + "/.config/com.codedead.opal";

    public static final String PROPERTIES_RESOURCE_LOCATION = "default.properties";
    public static final String PROPERTIES_FILE_LOCATION = PROPERTIES_BASE_PATH + "/opal.properties";

    public static final String HELP_DOCUMENTATION_RESOURCE_LOCATION = "/documents/help.pdf";
    public static final String HELP_DOCUMENTATION_FILE_LOCATION = PROPERTIES_BASE_PATH + "/help.pdf";

    public static final String LICENSE_RESOURCE_LOCATION = "/documents/license.pdf";
    public static final String LICENSE_FILE_LOCATION = PROPERTIES_BASE_PATH + "/license.pdf";

    /**
     * Initialize a new SharedVariables
     */
    private SharedVariables() {
        // Private constructor
    }
}
