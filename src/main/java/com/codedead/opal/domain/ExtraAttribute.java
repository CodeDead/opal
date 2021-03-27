package com.codedead.opal.domain;

public final class ExtraAttribute {

    private String key;
    private String value;

    /**
     * Initialize a new ExtraAttribute
     */
    public ExtraAttribute() {
        // Default constructor
    }

    /**
     * Get the key
     *
     * @return The key
     */
    public final String getKey() {
        return key;
    }

    /**
     * Set the key
     *
     * @param key The key
     */
    public final void setKey(final String key) {
        this.key = key;
    }

    /**
     * Get the value
     *
     * @return The value
     */
    public final String getValue() {
        return value;
    }

    /**
     * Set the value
     *
     * @param value The value
     */
    public final void setValue(final String value) {
        this.value = value;
    }
}
