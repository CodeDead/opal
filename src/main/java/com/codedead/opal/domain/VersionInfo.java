package com.codedead.opal.domain;

import java.util.ArrayList;
import java.util.List;

public final class VersionInfo {

    private String id;
    private String createdAt;
    private String updatedAt;
    private String name;
    private String description;
    private String semver;
    private List<PlatformRelease> platforms = new ArrayList<>();

    /**
     * Initialize a new VersionInfo
     */
    public VersionInfo() {
        // Default constructor
    }

    /**
     * Get the version identifier
     *
     * @return The version identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Set the version identifier
     *
     * @param id The version identifier
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Get the creation timestamp
     *
     * @return The creation timestamp
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Set the creation timestamp
     *
     * @param createdAt The creation timestamp
     */
    public void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get the last update timestamp
     *
     * @return The last update timestamp
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Set the last update timestamp
     *
     * @param updatedAt The last update timestamp
     */
    public void setUpdatedAt(final String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Get the application name
     *
     * @return The application name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the application name
     *
     * @param name The application name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the application description
     *
     * @return The application description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the application description
     *
     * @param description The application description
     */
    public void setDescription(final String description) {
        this.description = description;
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
     * Get the available platform releases
     *
     * @return The available platform releases
     */
    public List<PlatformRelease> getPlatforms() {
        return platforms;
    }

    /**
     * Set the available platform releases
     *
     * @param platforms The available platform releases
     */
    public void setPlatforms(final List<PlatformRelease> platforms) {
        this.platforms = platforms == null ? new ArrayList<>() : platforms;
    }
}
