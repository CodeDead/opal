package com.codedead.opal.domain;

public final class PlatformRelease {

    private String name;
    private String arch;
    private Boolean portable;
    private String downloadUrl;
    private String infoUrl;

    /**
     * Initialize a new PlatformRelease
     */
    public PlatformRelease() {
        // Default constructor
    }

    /**
     * Get the platform name
     *
     * @return The platform name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the platform name
     *
     * @param name The platform name
     */
    public void setName(final String name) {
        this.name = name;
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
     * Get whether the release is portable
     *
     * @return True if the release is portable, false if it is not, or null if this is unknown
     */
    public Boolean getPortable() {
        return portable;
    }

    /**
     * Set whether the release is portable
     *
     * @param portable True if the release is portable, false if it is not, or null if this is unknown
     */
    public void setPortable(final Boolean portable) {
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
}
