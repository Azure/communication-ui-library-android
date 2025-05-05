package com.azure.android.communication.ui.calling.redux.middleware;

import android.content.Context;

import com.azure.android.communication.ui.calling.redux.action.AudioDeviceAction;
import com.azure.android.communication.ui.calling.service.AudioDeviceService;
import com.azure.android.communication.ui.calling.redux.Store;
import com.azure.android.communication.ui.calling.redux.state.AudioDevice;

import java.util.List;

/**
 * Middleware that connects AudioDeviceService system events to Redux actions.
 */
public final class AudioDeviceServiceMiddleware implements AudioDeviceService.AudioDeviceCallback {
    private final Store store;
    private final AudioDeviceService audioDeviceService;

    public AudioDeviceServiceMiddleware(Context context, Store store) {
        this.store = store;
        this.audioDeviceService = new AudioDeviceService(context, this);
    }

    /**
     * Start monitoring audio device changes.
     */
    public void start() {
        audioDeviceService.start();
    }

    /**
     * Stop monitoring audio device changes.
     */
    public void stop() {
        audioDeviceService.stop();
    }

    /**
     * Get the audio device service instance.
     *
     * @return AudioDeviceService instance
     */
    public AudioDeviceService getAudioDeviceService() {
        return audioDeviceService;
    }

    @Override
    public void onDeviceConnected(AudioDevice device) {
        store.dispatch(new AudioDeviceAction.DeviceConnected(device));
    }

    @Override
    public void onDeviceDisconnected(AudioDevice device) {
        store.dispatch(new AudioDeviceAction.DeviceDisconnected(device));
    }

    @Override
    public void onAvailableDevicesChanged(List<AudioDevice> devices) {
        store.dispatch(new AudioDeviceAction.DevicesDiscovered(devices));
    }

    @Override
    public void onWiredHeadsetStateChanged(boolean connected) {
        store.dispatch(new AudioDeviceAction.WiredHeadsetStateChanged(connected));
    }

    @Override
    public void onBluetoothStateChanged(boolean connected) {
        store.dispatch(new AudioDeviceAction.BluetoothStateChanged(connected));
    }

    @Override
    public void onAudioBecomingNoisy() {
        AudioDevice speaker = new AudioDevice(
            com.azure.android.communication.ui.calling.redux.state.AudioDeviceType.SPEAKER,
            "speaker",
            "Speaker"
        );
        store.dispatch(new AudioDeviceAction.AudioBecomingNoisy(speaker));
    }
}
