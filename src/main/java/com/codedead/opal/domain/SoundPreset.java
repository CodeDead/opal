package com.codedead.opal.domain;

public final class SoundPreset {

    private double rainVolume;
    private double windVolume;
    private double thunderVolume;
    private double keyboardVolume;
    private double phoneVolume;
    private double chatterVolume;
    private double trafficVolume;
    private double firePlaceVolume;

    /**
     * Initialize a new SoundPreset
     */
    public SoundPreset() {
        // Default constructor
    }

    /**
     * Get the rain volume
     *
     * @return The rain volume
     */
    public final double getRainVolume() {
        return rainVolume;
    }

    /**
     * Set the rain volume
     *
     * @param rainVolume The rain volume
     */
    public final void setRainVolume(final double rainVolume) {
        this.rainVolume = rainVolume;
    }

    /**
     * Get the wind volume
     *
     * @return The wind volume
     */
    public final double getWindVolume() {
        return windVolume;
    }

    /**
     * Set the wind volume
     *
     * @param windVolume The wind volume
     */
    public final void setWindVolume(final double windVolume) {
        this.windVolume = windVolume;
    }

    /**
     * Get the thunder volume
     *
     * @return The thunder volume
     */
    public final double getThunderVolume() {
        return thunderVolume;
    }

    /**
     * Set the thunder volume
     *
     * @param thunderVolume The thunder volume
     */
    public final void setThunderVolume(final double thunderVolume) {
        this.thunderVolume = thunderVolume;
    }

    /**
     * Get the keyboard volume
     *
     * @return The keyboard volume
     */
    public final double getKeyboardVolume() {
        return keyboardVolume;
    }

    /**
     * Set the keyboard volume
     *
     * @param keyboardVolume The keyboard volume
     */
    public final void setKeyboardVolume(final double keyboardVolume) {
        this.keyboardVolume = keyboardVolume;
    }

    /**
     * Get the phone volume
     *
     * @return The phone volume
     */
    public final double getPhoneVolume() {
        return phoneVolume;
    }

    /**
     * Set the phone volume
     *
     * @param phoneVolume The phone volume
     */
    public final void setPhoneVolume(final double phoneVolume) {
        this.phoneVolume = phoneVolume;
    }

    /**
     * Get the chatter volume
     *
     * @return The chatter volume
     */
    public final double getChatterVolume() {
        return chatterVolume;
    }

    /**
     * Set the chatter volume
     *
     * @param chatterVolume The chatter volume
     */
    public final void setChatterVolume(final double chatterVolume) {
        this.chatterVolume = chatterVolume;
    }

    /**
     * Get the traffic volume
     *
     * @return The traffic volume
     */
    public final double getTrafficVolume() {
        return trafficVolume;
    }

    /**
     * Set the traffic volume
     *
     * @param trafficVolume The traffic volume
     */
    public final void setTrafficVolume(final double trafficVolume) {
        this.trafficVolume = trafficVolume;
    }

    /**
     * Get the fireplace volume
     *
     * @return The fireplace volume
     */
    public final double getFirePlaceVolume() {
        return firePlaceVolume;
    }

    /**
     * Set the fireplace volume
     *
     * @param firePlaceVolume The fireplace volume
     */
    public final void setFirePlaceVolume(final double firePlaceVolume) {
        this.firePlaceVolume = firePlaceVolume;
    }
}
