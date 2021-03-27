package com.codedead.opal.domain;

import java.util.List;

public final class PlatformUpdate {

    private String platformName;
    private int majorVersion;
    private int minorVersion;
    private int buildVersion;
    private int revisionVersion;

    private String downloadUrl;
    private List<ExtraAttribute> extraAttributes;

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
     * Get the List of {@link com.codedead.opal.domain.ExtraAttribute} objects
     *
     * @return The List of {@link com.codedead.opal.domain.ExtraAttribute} objects
     */
    public final List<ExtraAttribute> getExtraAttributes() {
        return extraAttributes;
    }

    /**
     * Set the List of {@link com.codedead.opal.domain.ExtraAttribute} objects
     *
     * @param extraAttributes The List of {@link com.codedead.opal.domain.ExtraAttribute} objects
     */
    public final void setExtraAttributes(final List<ExtraAttribute> extraAttributes) {
        this.extraAttributes = extraAttributes;
    }
}
