package com.azure.android.communication.ui.calling.redux.reducer;

import com.azure.android.communication.ui.calling.redux.action.AudioDeviceAction;
import com.azure.android.communication.ui.calling.redux.state.AudioDevice;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceState;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceType;

import java.util.ArrayList;
import java.util.List;

/**
 * Reducer for handling audio device state changes.
 */
public final class AudioDeviceReducer {
    private AudioDeviceReducer() {
    }

    /**
     * Reduces the state based on the action.
     *
     * @param state Current state
     * @param action Action to process
     * @return New state
     */
    public static AudioDeviceState reduce(final AudioDeviceState state, final Object action) {
        if (action instanceof AudioDeviceAction.DeviceConnected) {
            return handleDeviceConnected(state, (AudioDeviceAction.DeviceConnected) action);
        } else if (action instanceof AudioDeviceAction.DeviceDisconnected) {
            return handleDeviceDisconnected(state, (AudioDeviceAction.DeviceDisconnected) action);
        } else if (action instanceof AudioDeviceAction.DeviceSelected) {
            return handleDeviceSelected(state, (AudioDeviceAction.DeviceSelected) action);
        } else if (action instanceof AudioDeviceAction.DevicesDiscovered) {
            return handleDevicesDiscovered(state, (AudioDeviceAction.DevicesDiscovered) action);
        } else if (action instanceof AudioDeviceAction.WiredHeadsetStateChanged) {
            return handleWiredHeadsetStateChanged(state, (AudioDeviceAction.WiredHeadsetStateChanged) action);
        } else if (action instanceof AudioDeviceAction.BluetoothStateChanged) {
            return handleBluetoothStateChanged(state, (AudioDeviceAction.BluetoothStateChanged) action);
        }
        return state;
    }

    private static AudioDeviceState handleDeviceConnected(
            AudioDeviceState state,
            AudioDeviceAction.DeviceConnected action) {
        AudioDevice newDevice = action.getDevice();
        List<AudioDevice> updatedDevices = new ArrayList<>(state.getAvailableDevices());
        
        // Add device if not already present
        if (!updatedDevices.contains(newDevice)) {
            updatedDevices.add(newDevice);
        }

        // Auto-switch based on priority
        AudioDevice currentDevice = state.getCurrentDevice();
        if (newDevice.getType().hasHigherPriorityThan(currentDevice.getType())) {
            return state.withAvailableDevices(updatedDevices).withCurrentDevice(newDevice);
        }
        
        return state.withAvailableDevices(updatedDevices);
    }

    private static AudioDeviceState handleDeviceDisconnected(
            AudioDeviceState state,
            AudioDeviceAction.DeviceDisconnected action) {
        AudioDevice disconnectedDevice = action.getDevice();
        List<AudioDevice> updatedDevices = new ArrayList<>(state.getAvailableDevices());
        updatedDevices.remove(disconnectedDevice);

        // If current device was disconnected, switch to highest priority available device
        if (state.getCurrentDevice().equals(disconnectedDevice)) {
            AudioDevice newDevice = findHighestPriorityDevice(updatedDevices);
            return state.withAvailableDevices(updatedDevices).withCurrentDevice(newDevice);
        }

        return state.withAvailableDevices(updatedDevices);
    }

    private static AudioDeviceState handleDeviceSelected(
            AudioDeviceState state,
            AudioDeviceAction.DeviceSelected action) {
        AudioDevice selectedDevice = action.getDevice();
        if (state.getAvailableDevices().contains(selectedDevice)) {
            return state.withCurrentDevice(selectedDevice);
        }
        return state;
    }

    private static AudioDeviceState handleDevicesDiscovered(
            AudioDeviceState state,
            AudioDeviceAction.DevicesDiscovered action) {
        List<AudioDevice> newDevices = action.getDevices();
        
        // If current device is no longer available, switch to highest priority device
        AudioDevice currentDevice = state.getCurrentDevice();
        if (!newDevices.contains(currentDevice)) {
            AudioDevice newDevice = findHighestPriorityDevice(newDevices);
            return state.withAvailableDevices(newDevices).withCurrentDevice(newDevice);
        }
        
        return state.withAvailableDevices(newDevices);
    }

    private static AudioDeviceState handleWiredHeadsetStateChanged(
            AudioDeviceState state,
            AudioDeviceAction.WiredHeadsetStateChanged action) {
        return state.withWiredHeadsetConnected(action.isConnected());
    }

    private static AudioDeviceState handleBluetoothStateChanged(
            AudioDeviceState state,
            AudioDeviceAction.BluetoothStateChanged action) {
        return state.withBluetoothConnected(action.isConnected());
    }

    private static AudioDevice findHighestPriorityDevice(List<AudioDevice> devices) {
        AudioDevice highestPriority = null;
        for (AudioDevice device : devices) {
            if (highestPriority == null || 
                device.getType().hasHigherPriorityThan(highestPriority.getType())) {
                highestPriority = device;
            }
        }
        // Fallback to speaker if no devices available
        if (highestPriority == null) {
            highestPriority = new AudioDevice(AudioDeviceType.SPEAKER, "speaker", "Speaker");
        }
        return highestPriority;
    }
}
