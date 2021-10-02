package com.codedead.opal.domain;

import java.util.Locale;

public final class OsCheck {

    private static OSType detectedOS;

    /**
     * Initialize a new OsCheck
     */
    private OsCheck() {
        // Default constructor
    }

    /**
     * Check which type of OS is currently being used to run the application
     *
     * @return The {@link OSType} which contains the current operating system
     */
    public static OSType getOperatingSystemType() {
        if (detectedOS == null) {
            final String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((OS.contains("mac")) || (OS.contains("darwin"))) {
                detectedOS = OSType.MacOS;
            } else if (OS.contains("win")) {
                detectedOS = OSType.Windows;
            } else if (OS.contains("nux")) {
                detectedOS = OSType.Linux;
            } else {
                detectedOS = OSType.Other;
            }
        }
        return detectedOS;
    }
}
