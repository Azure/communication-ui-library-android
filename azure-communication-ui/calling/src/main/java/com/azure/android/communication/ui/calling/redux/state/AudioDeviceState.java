package com.azure.android.communication.ui.calling.redux.state;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of audio devices in the system.
 */
public final class AudioDeviceState {
    private final AudioDevice currentDevice;
    private final List<AudioDevice> availableDevices;
    private final boolean isWiredHeadsetConnected;
    private final boolean isBluetoothConnected;

    /**
     * Creates a new instance of AudioDeviceState.
     *
     * @param currentDevice The currently selected audio device
     * @param availableDevices List of available audio devices
     * @param isWiredHeadsetConnected Whether a wired headset is connected
     * @param isBluetoothConnected Whether a bluetooth device is connected
     */
    public AudioDeviceState(
            AudioDevice currentDevice,
            List<AudioDevice> availableDevices,
            boolean isWiredHeadsetConnected,
            boolean isBluetoothConnected) {
        this.currentDevice = currentDevice;
        this.availableDevices = new ArrayList<>(availableDevices);
        this.isWiredHeadsetConnected = isWiredHeadsetConnected;
        this.isBluetoothConnected = isBluetoothConnected;
    }

    /**
     * Creates initial state with speaker as default device.
     *
     * @return Initial AudioDeviceState
     */
    public static AudioDeviceState getInitialState() {
        List<AudioDevice> devices = new ArrayList<>();
        AudioDevice speaker = new AudioDevice(AudioDeviceType.SPEAKER, "speaker", "Speaker");
        devices.add(speaker);
        return new AudioDeviceState(speaker, devices, false, false);
    }

    /**
     * Gets the currently selected audio device.
     *
     * @return Current AudioDevice
     */
    public AudioDevice getCurrentDevice() {
        return currentDevice;
    }

    /**
     * Gets list of available audio devices.
     *
     * @return List of available AudioDevices
     */
    public List<AudioDevice> getAvailableDevices() {
        return new ArrayList<>(availableDevices);
    }

    /**
     * Checks if wired headset is connected.
     *
     * @return true if wired headset is connected
     */
    public boolean isWiredHeadsetConnected() {
        return isWiredHeadsetConnected;
    }

    /**
     * Checks if bluetooth device is connected.
     *
     * @return true if bluetooth device is connected
     */
    public boolean isBluetoothConnected() {
        return isBluetoothConnected;
    }

    /**
     * Creates a new state with updated current device.
     *
     * @param device New current device
     * @return Updated AudioDeviceState
     */
    public AudioDeviceState withCurrentDevice(AudioDevice device) {
        return new AudioDeviceState(device, availableDevices, isWiredHeadsetConnected, isBluetoothConnected);
    }

    /**
     * Creates a new state with updated available devices.
     *
     * @param devices New list of available devices
     * @return Updated AudioDeviceState
     */
    public AudioDeviceState withAvailableDevices(List<AudioDevice> devices) {
        return new AudioDeviceState(currentDevice, devices, isWiredHeadsetConnected, isBluetoothConnected);
    }

    /**
     * Creates a new state with updated wired headset connection status.
     *
     * @param connected New wired headset connection status
     * @return Updated AudioDeviceState
     */
    public AudioDeviceState withWiredHeadsetConnected(boolean connected) {
        return new AudioDeviceState(currentDevice, availableDevices, connected, isBluetoothConnected);
    }

    /**
     * Creates a new state with updated bluetooth connection status.
     *
     * @param connected New bluetooth connection status
     * @return Updated AudioDeviceState
     */
    public AudioDeviceState withBluetoothConnected(boolean connected) {
        return new AudioDeviceState(currentDevice, availableDevices, isWiredHeadsetConnected, connected);
    }
}
