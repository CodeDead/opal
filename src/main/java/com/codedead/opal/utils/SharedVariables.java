package com.codedead.opal.utils;

public final class SharedVariables {

    public static final String ICON_URL = "/images/opal.png";
    public static final String CURRENT_VERSION = "1.0.0.0";

    public static final String PROPERTIES_RESOURCE_LOCATION = "default.properties";
    public static final String PROPERTIES_FILE_LOCATION = System.getProperty("user.home") + "/.opal/opal.properties";

    public static final String HELP_DOCUMENTATION_RESOURCE_LOCATION = "/documents/help.pdf";
    public static final String HELP_DOCUMENTATION_FILE_LOCATION = System.getProperty("user.home") + "/.opal/help.pdf";

    public static final String LICENSE_RESOURCE_LOCATION = "/documents/license.pdf";
    public static final String LICENSE_FILE_LOCATION = System.getProperty("user.home") + "/.opal/license.pdf";

    /**
     * Initialize a new SharedVariables
     */
    private SharedVariables() {
        // Private constructor
    }
}
