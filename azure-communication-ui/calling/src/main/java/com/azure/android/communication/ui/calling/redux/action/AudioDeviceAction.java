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
     * Action dispatched when an audio device is manually selected.
     */
    public static final class DeviceSelected {
        private final AudioDevice device;

        public DeviceSelected(AudioDevice device) {
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
}
