package com.codedead.opal.domain;

public class ExtraAttribute {

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
    public String getKey() {
        return key;
    }

    /**
     * Set the key
     *
     * @param key The key
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * Get the value
     *
     * @return The value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value
     *
     * @param value The value
     */
    public void setValue(final String value) {
        this.value = value;
    }
}
