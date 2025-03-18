package com.azure.android.communication.ui.calling.redux.action;

import com.azure.android.communication.ui.calling.redux.state.AudioDevice;

import java.util.List;

/**
 * Actions related to audio device management.
 */
public abstract class AudioDeviceAction {
    private AudioDeviceAction() {
    }

    /**
     * Action dispatched when user requests to switch to a device.
     */
    public static final class SelectDeviceRequested {
        private final AudioDevice device;

        public SelectDeviceRequested(AudioDevice device) {
            this.device = device;
        }

        public AudioDevice getDevice() {
            return device;
        }
    }

    /**
     * Action dispatched when device switching begins.
     */
    public static final class DeviceSwitchStarted {
        private final AudioDevice targetDevice;
        private final AudioDevice previousDevice;

        public DeviceSwitchStarted(AudioDevice targetDevice, AudioDevice previousDevice) {
            this.targetDevice = targetDevice;
            this.previousDevice = previousDevice;
        }

        public AudioDevice getTargetDevice() {
            return targetDevice;
        }

        public AudioDevice getPreviousDevice() {
            return previousDevice;
        }
    }

    /**
     * Action dispatched when device switch completes successfully.
     */
    public static final class DeviceSwitchCompleted {
        private final AudioDevice device;

        public DeviceSwitchCompleted(AudioDevice device) {
            this.device = device;
        }

        public AudioDevice getDevice() {
            return device;
        }
    }

    /**
     * Action dispatched when device switch fails.
     */
    public static final class DeviceSwitchFailed {
        private final AudioDevice targetDevice;
        private final AudioDevice fallbackDevice;
        private final String error;

        public DeviceSwitchFailed(AudioDevice targetDevice, AudioDevice fallbackDevice, String error) {
            this.targetDevice = targetDevice;
            this.fallbackDevice = fallbackDevice;
            this.error = error;
        }

        public AudioDevice getTargetDevice() {
            return targetDevice;
        }

        public AudioDevice getFallbackDevice() {
            return fallbackDevice;
        }

        public String getError() {
            return error;
        }
    }

    /**
     * Action dispatched when a new audio device is connected.
     */
    public static final class DeviceConnected {
        private final AudioDevice device;

        public DeviceConnected(AudioDevice device) {
            this.device = device;
        }

        public AudioDevice getDevice() {
            return device;
        }
    }

    /**
     * Action dispatched when an audio device is disconnected.
     */
    public static final class DeviceDisconnected {
        private final AudioDevice device;

        public DeviceDisconnected(AudioDevice device) {
            this.device = device;
        }

        public AudioDevice getDevice() {
            return device;
        }
    }

    /**
     * Action dispatched when available audio devices are discovered.
     */
    public static final class DevicesDiscovered {
        private final List<AudioDevice> devices;

        public DevicesDiscovered(List<AudioDevice> devices) {
            this.devices = devices;
        }

        public List<AudioDevice> getDevices() {
            return devices;
        }
    }

    /**
     * Action dispatched when wired headset connection state changes.
     */
    public static final class WiredHeadsetStateChanged {
        private final boolean connected;

        public WiredHeadsetStateChanged(boolean connected) {
            this.connected = connected;
        }

        public boolean isConnected() {
            return connected;
        }
    }

    /**
     * Action dispatched when bluetooth connection state changes.
     */
    public static final class BluetoothStateChanged {
        private final boolean connected;

        public BluetoothStateChanged(boolean connected) {
            this.connected = connected;
        }

        public boolean isConnected() {
            return connected;
        }
    }

    /**
     * Action dispatched when audio becomes noisy.
     */
    public static final class AudioBecomingNoisy {
        private final AudioDevice fallbackDevice;

        public AudioBecomingNoisy(AudioDevice fallbackDevice) {
            this.fallbackDevice = fallbackDevice;
        }

        public AudioDevice getFallbackDevice() {
            return fallbackDevice;
        }
    }
}
