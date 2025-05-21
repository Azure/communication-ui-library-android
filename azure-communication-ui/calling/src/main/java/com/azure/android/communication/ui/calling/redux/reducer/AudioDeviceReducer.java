package com.azure.android.communication.ui.calling.redux.reducer;

import com.azure.android.communication.ui.calling.redux.action.AudioDeviceAction;
import com.azure.android.communication.ui.calling.redux.state.AudioDevice;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceState;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceType;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSwitchingState;

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
        if (action instanceof AudioDeviceAction.DeviceSwitchStarted) {
            return handleDeviceSwitchStarted(state, (AudioDeviceAction.DeviceSwitchStarted) action);
        } else if (action instanceof AudioDeviceAction.DeviceSwitchCompleted) {
            return handleDeviceSwitchCompleted(state, (AudioDeviceAction.DeviceSwitchCompleted) action);
        } else if (action instanceof AudioDeviceAction.DeviceSwitchFailed) {
            return handleDeviceSwitchFailed(state, (AudioDeviceAction.DeviceSwitchFailed) action);
        } else if (action instanceof AudioDeviceAction.DeviceConnected) {
            return handleDeviceConnected(state, (AudioDeviceAction.DeviceConnected) action);
        } else if (action instanceof AudioDeviceAction.DeviceDisconnected) {
            return handleDeviceDisconnected(state, (AudioDeviceAction.DeviceDisconnected) action);
        } else if (action instanceof AudioDeviceAction.DevicesDiscovered) {
            return handleDevicesDiscovered(state, (AudioDeviceAction.DevicesDiscovered) action);
        } else if (action instanceof AudioDeviceAction.WiredHeadsetStateChanged) {
            return handleWiredHeadsetStateChanged(state, (AudioDeviceAction.WiredHeadsetStateChanged) action);
        } else if (action instanceof AudioDeviceAction.BluetoothStateChanged) {
            return handleBluetoothStateChanged(state, (AudioDeviceAction.BluetoothStateChanged) action);
        } else if (action instanceof AudioDeviceAction.AudioBecomingNoisy) {
            return handleAudioBecomingNoisy(state, (AudioDeviceAction.AudioBecomingNoisy) action);
        }
        return state;
    }

    private static AudioDeviceState handleDeviceSwitchStarted(
            AudioDeviceState state,
            AudioDeviceAction.DeviceSwitchStarted action) {
        return state.withSwitchingToDevice(action.getTargetDevice());
    }

    private static AudioDeviceState handleDeviceSwitchCompleted(
            AudioDeviceState state,
            AudioDeviceAction.DeviceSwitchCompleted action) {
        return state.withSwitchingCompleted(action.getDevice());
    }

    private static AudioDeviceState handleDeviceSwitchFailed(
            AudioDeviceState state,
            AudioDeviceAction.DeviceSwitchFailed action) {
        return state.withSwitchingFailed(action.getFallbackDevice(), action.getError());
    }

    private static AudioDeviceState handleDeviceConnected(
            AudioDeviceState state,
            AudioDeviceAction.DeviceConnected action) {
        // Don't modify state during device switching
        if (state.getSwitchingState() == AudioDeviceSwitchingState.SWITCHING) {
            return state;
        }

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
        // Don't modify state during device switching
        if (state.getSwitchingState() == AudioDeviceSwitchingState.SWITCHING) {
            return state;
        }

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

    private static AudioDeviceState handleDevicesDiscovered(
            AudioDeviceState state,
            AudioDeviceAction.DevicesDiscovered action) {
        // Don't modify state during device switching
        if (state.getSwitchingState() == AudioDeviceSwitchingState.SWITCHING) {
            return state;
        }

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

    private static AudioDeviceState handleAudioBecomingNoisy(
            AudioDeviceState state,
            AudioDeviceAction.AudioBecomingNoisy action) {
        // Don't modify state during device switching
        if (state.getSwitchingState() == AudioDeviceSwitchingState.SWITCHING) {
            return state;
        }

        AudioDevice fallbackDevice = action.getFallbackDevice();
        if (state.getAvailableDevices().contains(fallbackDevice)) {
            return state.withCurrentDevice(fallbackDevice);
        }
        return state;
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
