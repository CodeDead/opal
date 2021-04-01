package com.codedead.opal.domain;

import java.util.Map;

public final class PlatformUpdate implements Comparable<PlatformUpdate> {

    private String platformName;
    private boolean portable;
    private int majorVersion;
    private int minorVersion;
    private int buildVersion;
    private int revisionVersion;

    private String downloadUrl;
    private Map<String, String> extraAttributes;

    /**
     * Initialize a new PlatformUpdate
     */
    public PlatformUpdate() {
        // Default constructor
    }

    /**
     * Get the platform name
     *
     * @return The platform name
     */
    public final String getPlatformName() {
        return platformName;
    }

    /**
     * Set the platform name
     *
     * @param platformName The platform name
     */
    public final void setPlatformName(final String platformName) {
        this.platformName = platformName;
    }

    /**
     * Get whether the version is portable or not
     *
     * @return True if the version is portable, otherwise false
     */
    public final boolean isPortable() {
        return portable;
    }

    /**
     * Set whether the version is portable or not
     *
     * @param portable True if the version is portable, otherwise false
     */
    public final void setPortable(final boolean portable) {
        this.portable = portable;
    }

    /**
     * Get the major version
     *
     * @return The major version
     */
    public final int getMajorVersion() {
        return majorVersion;
    }

    /**
     * Set the major version
     *
     * @param majorVersion The major version
     */
    public final void setMajorVersion(final int majorVersion) {
        this.majorVersion = majorVersion;
    }

    /**
     * Get the minor version
     *
     * @return The minor version
     */
    public final int getMinorVersion() {
        return minorVersion;
    }

    /**
     * Set the minor version
     *
     * @param minorVersion The minor version
     */
    public final void setMinorVersion(final int minorVersion) {
        this.minorVersion = minorVersion;
    }

    /**
     * Get the build version
     *
     * @return The build version
     */
    public final int getBuildVersion() {
        return buildVersion;
    }

    /**
     * Set the build version
     *
     * @param buildVersion The build version
     */
    public final void setBuildVersion(final int buildVersion) {
        this.buildVersion = buildVersion;
    }

    /**
     * Get the revision version
     *
     * @return The revision version
     */
    public final int getRevisionVersion() {
        return revisionVersion;
    }

    /**
     * Set the revision version
     *
     * @param revisionVersion The revision version
     */
    public final void setRevisionVersion(final int revisionVersion) {
        this.revisionVersion = revisionVersion;
    }

    /**
     * Get the download URL
     *
     * @return The download URL
     */
    public final String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * Set the download URL
     *
     * @param downloadUrl The download URL
     */
    public final void setDownloadUrl(final String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    /**
     * Get the extra attributes
     *
     * @return The Map that contains the extra attributes
     */
    public final Map<String, String> getExtraAttributes() {
        return extraAttributes;
    }

    /**
     * Set the extra attributes
     *
     * @param extraAttributes The Map that contains the extra attributes
     */
    public final void setExtraAttributes(final Map<String, String> extraAttributes) {
        this.extraAttributes = extraAttributes;
    }

    /**
     * Check if a {@link PlatformUpdate} is more recent than another
     *
     * @param o The platform update that should be compared
     * @return 1 if the {@link PlatformUpdate} object that is passed as a parameter is more recent than the given {@link PlatformUpdate}, otherwise 0
     */
    @Override
    public int compareTo(final PlatformUpdate o) {
        if (getMajorVersion() < o.getMajorVersion()) {
            return 1;
        } else if (getMajorVersion() > o.getMajorVersion()) {
            return 0;
        } else {
            if (getMinorVersion() < o.getMinorVersion()) {
                return 1;
            } else if (getMajorVersion() < o.getMinorVersion()) {
                return 0;
            } else {
                if (getBuildVersion() < o.getBuildVersion()) {
                    return 1;
                } else if (getBuildVersion() > o.getBuildVersion()) {
                    return 0;
                } else {
                    if (getRevisionVersion() < o.getRevisionVersion()) {
                        return 1;
                    } else if (getRevisionVersion() > o.getRevisionVersion()) {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }
}
