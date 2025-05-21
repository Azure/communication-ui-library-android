package com.azure.android.communication.ui.calling.redux.state;

/**
 * Enum representing different types of audio devices.
 */
public enum AudioDeviceType {
    /**
     * Wired headset device.
     */
    WIRED_HEADSET(0),

    /**
     * Bluetooth audio device.
     */
    BLUETOOTH(1),

    /**
     * System speaker.
     */
    SPEAKER(2);

    private final int priority;

    AudioDeviceType(int priority) {
        this.priority = priority;
    }

    /**
     * Gets the priority of the device type.
     * Lower number means higher priority.
     *
     * @return priority value
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Checks if this device type has higher priority than another.
     *
     * @param other The other device type to compare with
     * @return true if this device has higher priority
     */
    public boolean hasHigherPriorityThan(AudioDeviceType other) {
        return this.priority < other.priority;
    }
}
