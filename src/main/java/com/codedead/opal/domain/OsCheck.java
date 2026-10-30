package com.codedead.opal.domain;

import java.util.Locale;

public final class OsCheck {

    private static OSType detectedOS;
    private static String detectedArchitecture;

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
                detectedOS = OSType.MACOS;
            } else if (OS.contains("win")) {
                detectedOS = OSType.WINDOWS;
            } else if (OS.contains("nux")) {
                detectedOS = OSType.LINUX;
            } else {
                detectedOS = OSType.OTHER;
            }
        }
        return detectedOS;
    }

    /**
     * Get the platform name used by the update API
     *
     * @return The lowercase platform name
     */
    public static String getPlatformName() {
        return getOperatingSystemType().name().toLowerCase(Locale.ROOT);
    }

    /**
     * Get the architecture used by the update API
     *
     * @return The normalized architecture
     */
    public static String getArchitecture() {
        if (detectedArchitecture == null) {
            detectedArchitecture = normalizeArchitecture(System.getProperty("os.arch", ""));
        }
        return detectedArchitecture;
    }

    /**
     * Normalize an architecture name to the format used by the update API
     *
     * @param architecture The architecture name
     * @return The normalized architecture
     */
    static String normalizeArchitecture(final String architecture) {
        if (architecture == null)
            throw new NullPointerException("Architecture cannot be null!");

        final String value = architecture.trim().toLowerCase(Locale.ROOT);
        return switch (value) {
            case "amd64", "x86_64", "x64" -> "x86_64";
            case "aarch64", "arm64" -> "aarch64";
            case "x86", "i386", "i486", "i586", "i686" -> "x86";
            default -> value;
        };
    }
}
