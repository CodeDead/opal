package com.codedead.opal.domain;

import java.util.Objects;

public final class PlatformUpdate implements Comparable<PlatformUpdate> {

    private String semver;
    private String platformName;
    private String arch;
    private boolean portable;
    private String downloadUrl;
    private String infoUrl;

    /**
     * Initialize a new PlatformUpdate
     */
    public PlatformUpdate() {
        // Default constructor
    }

    /**
     * Get the semantic version
     *
     * @return The semantic version
     */
    public String getSemver() {
        return semver;
    }

    /**
     * Set the semantic version
     *
     * @param semver The semantic version
     */
    public void setSemver(final String semver) {
        this.semver = semver;
    }

    /**
     * Get the platform name
     *
     * @return The platform name
     */
    public String getPlatformName() {
        return platformName;
    }

    /**
     * Set the platform name
     *
     * @param platformName The platform name
     */
    public void setPlatformName(final String platformName) {
        this.platformName = platformName;
    }

    /**
     * Get the platform architecture
     *
     * @return The platform architecture
     */
    public String getArch() {
        return arch;
    }

    /**
     * Set the platform architecture
     *
     * @param arch The platform architecture
     */
    public void setArch(final String arch) {
        this.arch = arch;
    }

    /**
     * Get whether the update is portable
     *
     * @return True if the update is portable, otherwise false
     */
    public boolean isPortable() {
        return portable;
    }

    /**
     * Set whether the update is portable
     *
     * @param portable True if the update is portable, otherwise false
     */
    public void setPortable(final boolean portable) {
        this.portable = portable;
    }

    /**
     * Get the download URL
     *
     * @return The download URL
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * Set the download URL
     *
     * @param downloadUrl The download URL
     */
    public void setDownloadUrl(final String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    /**
     * Get the information URL
     *
     * @return The information URL
     */
    public String getInfoUrl() {
        return infoUrl;
    }

    /**
     * Set the information URL
     *
     * @param infoUrl The information URL
     */
    public void setInfoUrl(final String infoUrl) {
        this.infoUrl = infoUrl;
    }

    /**
     * Compare this update with another update using semantic version ordering
     *
     * @param other The platform update that should be compared
     * @return A negative value if this update is older, zero if both versions are equal,
     * or a positive value if this update is newer
     */
    @Override
    public int compareTo(final PlatformUpdate other) {
        Objects.requireNonNull(other, "other");
        return Semver.compare(semver, other.semver);
    }
}
