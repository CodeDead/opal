package com.codedead.opal.domain;

import java.util.List;

public final class Update {

    private List<PlatformUpdate> platformUpdates;

    /**
     * Initialize a new Update
     */
    public Update() {
        // Default constructor
    }

    /**
     * Get the List of {@link com.codedead.opal.domain.PlatformUpdate} objects
     *
     * @return The List of {@link com.codedead.opal.domain.PlatformUpdate} objects
     */
    public final List<PlatformUpdate> getPlatformUpdates() {
        return platformUpdates;
    }

    /**
     * Set the List of {@link com.codedead.opal.domain.PlatformUpdate} objects
     *
     * @param platformUpdates The List of {@link com.codedead.opal.domain.PlatformUpdate} objects
     */
    public final void setPlatformUpdates(final List<PlatformUpdate> platformUpdates) {
        this.platformUpdates = platformUpdates;
    }
}
