package com.azure.android.communication.ui.calling.redux.state;

/**
 * Represents an audio device in the system.
 */
public final class AudioDevice {
    private final AudioDeviceType type;
    private final String deviceId;
    private final String deviceName;

    /**
     * Creates a new instance of AudioDevice.
     *
     * @param type The type of audio device
     * @param deviceId Unique identifier for the device
     * @param deviceName Human-readable name of the device
     */
    public AudioDevice(AudioDeviceType type, String deviceId, String deviceName) {
        this.type = type;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }

    /**
     * Gets the type of audio device.
     *
     * @return AudioDeviceType
     */
    public AudioDeviceType getType() {
        return type;
    }

    /**
     * Gets the device ID.
     *
     * @return Device ID string
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Gets the device name.
     *
     * @return Device name string
     */
    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AudioDevice other = (AudioDevice) obj;
        return type == other.type && deviceId.equals(other.deviceId);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + deviceId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AudioDevice{" +
                "type=" + type +
                ", deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                '}';
    }
}
