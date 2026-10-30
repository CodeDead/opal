package com.codedead.opal.domain;

import java.lang.module.ModuleDescriptor.Version;

public final class Semver {

    /**
     * Initialize a new Semver
     */
    private Semver() {
        // Private constructor
    }

    /**
     * Compare two semantic versions.
     * <p>
     * A leading {@code v} is ignored, build metadata is ignored, and missing trailing numeric
     * components are treated as zero. This allows the application's four-part version
     * ({@code 1.5.1.0}) to be compared with the API's three-part version ({@code v1.5.1}).
     *
     * @param first  The first version
     * @param second The second version
     * @return A negative value if the first version is older, zero if both versions are equal,
     * or a positive value if the first version is newer
     */
    public static int compare(final String first, final String second) {
        return parse(first).compareTo(parse(second));
    }

    /**
     * Parse a version string into a comparable version
     *
     * @param value The version string
     * @return The parsed version
     */
    private static Version parse(final String value) {
        if (value == null)
            throw new NullPointerException("Version cannot be null!");

        String version = value.trim();
        if (version.startsWith("v") || version.startsWith("V")) {
            version = version.substring(1);
        }

        final int buildMetadataIndex = version.indexOf('+');
        if (buildMetadataIndex >= 0) {
            version = version.substring(0, buildMetadataIndex);
        }

        if (version.isBlank())
            throw new IllegalArgumentException("Version cannot be blank!");

        return Version.parse(version);
    }
}
