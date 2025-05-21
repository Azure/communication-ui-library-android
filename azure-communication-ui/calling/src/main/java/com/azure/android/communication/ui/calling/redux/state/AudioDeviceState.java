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
    private final AudioDeviceSwitchingState switchingState;
    private final String error;

    /**
     * Creates a new instance of AudioDeviceState.
     *
     * @param currentDevice The currently selected audio device
     * @param availableDevices List of available audio devices
     * @param isWiredHeadsetConnected Whether a wired headset is connected
     * @param isBluetoothConnected Whether a bluetooth device is connected
     * @param switchingState The current device switching state
     * @param error Error message if device switching failed
     */
    public AudioDeviceState(
            AudioDevice currentDevice,
            List<AudioDevice> availableDevices,
            boolean isWiredHeadsetConnected,
            boolean isBluetoothConnected,
            AudioDeviceSwitchingState switchingState,
            String error) {
        this.currentDevice = currentDevice;
        this.availableDevices = new ArrayList<>(availableDevices);
        this.isWiredHeadsetConnected = isWiredHeadsetConnected;
        this.isBluetoothConnected = isBluetoothConnected;
        this.switchingState = switchingState;
        this.error = error;
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
        return new AudioDeviceState(speaker, devices, false, false, 
            AudioDeviceSwitchingState.NONE, null);
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
     * Gets the current device switching state.
     *
     * @return AudioDeviceSwitchingState
     */
    public AudioDeviceSwitchingState getSwitchingState() {
        return switchingState;
    }

    /**
     * Gets the error message if device switching failed.
     *
     * @return Error message or null if no error
     */
    public String getError() {
        return error;
    }

    /**
     * Creates a new state with updated current device.
     *
     * @param device New current device
     * @return Updated AudioDeviceState
     */
    public AudioDeviceState withCurrentDevice(AudioDevice device) {
        return new AudioDeviceState(device, availableDevices, isWiredHeadsetConnected, 
            isBluetoothConnected, AudioDeviceSwitchingState.NONE, null);
    }

    /**
     * Creates a new state with updated available devices.
     *
     * @param devices New list of available devices
     * @return Updated AudioDeviceState
     */
    public AudioDeviceState withAvailableDevices(List<AudioDevice> devices) {
        return new AudioDeviceState(currentDevice, devices, isWiredHeadsetConnected, 
            isBluetoothConnected, switchingState, error);
    }

    /**
     * Creates a new state with updated wired headset connection status.
     *
     * @param connected New wired headset connection status
     * @return Updated AudioDeviceState
     */
    public AudioDeviceState withWiredHeadsetConnected(boolean connected) {
        return new AudioDeviceState(currentDevice, availableDevices, connected, 
            isBluetoothConnected, switchingState, error);
    }

    /**
     * Creates a new state with updated bluetooth connection status.
     *
     * @param connected New bluetooth connection status
     * @return Updated AudioDeviceState
     */
    public AudioDeviceState withBluetoothConnected(boolean connected) {
        return new AudioDeviceState(currentDevice, availableDevices, isWiredHeadsetConnected, 
            connected, switchingState, error);
    }

    /**
     * Creates a new state with updated switching state.
     *
     * @param newSwitchingState New switching state
     * @return Updated AudioDeviceState
     */
    public AudioDeviceState withSwitchingState(AudioDeviceSwitchingState newSwitchingState) {
        return new AudioDeviceState(currentDevice, availableDevices, isWiredHeadsetConnected, 
            isBluetoothConnected, newSwitchingState, error);
    }

    /**
     * Creates a new state with updated error message.
     *
     * @param newError New error message
     * @return Updated AudioDeviceState
     */
    public AudioDeviceState withError(String newError) {
        return new AudioDeviceState(currentDevice, availableDevices, isWiredHeadsetConnected, 
            isBluetoothConnected, switchingState, newError);
    }

    /**
     * Creates a new state for device switching in progress.
     *
     * @param targetDevice Device being switched to
     * @return Updated AudioDeviceState
     */
    public AudioDeviceState withSwitchingToDevice(AudioDevice targetDevice) {
        return new AudioDeviceState(currentDevice, availableDevices, isWiredHeadsetConnected, 
            isBluetoothConnected, AudioDeviceSwitchingState.SWITCHING, null);
    }

    /**
     * Creates a new state for device switching completion.
     *
     * @param newDevice Successfully switched device
     * @return Updated AudioDeviceState
     */
    public AudioDeviceState withSwitchingCompleted(AudioDevice newDevice) {
        return new AudioDeviceState(newDevice, availableDevices, isWiredHeadsetConnected, 
            isBluetoothConnected, AudioDeviceSwitchingState.NONE, null);
    }

    /**
     * Creates a new state for device switching failure.
     *
     * @param fallbackDevice Device to fall back to
     * @param errorMessage Error message
     * @return Updated AudioDeviceState
     */
    public AudioDeviceState withSwitchingFailed(AudioDevice fallbackDevice, String errorMessage) {
        return new AudioDeviceState(fallbackDevice, availableDevices, isWiredHeadsetConnected, 
            isBluetoothConnected, AudioDeviceSwitchingState.NONE, errorMessage);
    }
}
