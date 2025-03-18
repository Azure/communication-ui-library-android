package com.azure.android.communication.ui.calling.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.azure.android.communication.ui.calling.redux.action.AudioDeviceAction;
import com.azure.android.communication.ui.calling.redux.state.AudioDevice;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceType;
import com.azure.android.communication.ui.calling.redux.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing audio device state and system audio routing.
 */
public final class AudioDeviceService {
    private final Context context;
    private final AudioManager audioManager;
    private final Store store;
    private final BroadcastReceiver audioDeviceReceiver;

    public AudioDeviceService(Context context, Store store) {
        this.context = context;
        this.store = store;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.audioDeviceReceiver = createAudioDeviceReceiver();
    }

    /**
     * Start monitoring audio device changes.
     */
    public void start() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        filter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        context.registerReceiver(audioDeviceReceiver, filter);

        // Initial device discovery
        updateAvailableDevices();
    }

    /**
     * Stop monitoring audio device changes.
     */
    public void stop() {
        context.unregisterReceiver(audioDeviceReceiver);
    }

    /**
     * Switch to a specific audio device.
     *
     * @param device The device to switch to
     */
    public void selectDevice(AudioDevice device) {
        switch (device.getType()) {
            case SPEAKER:
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(true);
                audioManager.setBluetoothScoOn(false);
                break;
            case BLUETOOTH:
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(false);
                audioManager.setBluetoothScoOn(true);
                audioManager.startBluetoothSco();
                break;
            case WIRED_HEADSET:
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(false);
                audioManager.setBluetoothScoOn(false);
                break;
        }
        store.dispatch(new AudioDeviceAction.DeviceSelected(device));
    }

    private BroadcastReceiver createAudioDeviceReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null) return;

                switch (action) {
                    case AudioManager.ACTION_HEADSET_PLUG:
                        boolean connected = intent.getIntExtra("state", 0) == 1;
                        handleWiredHeadsetConnection(connected);
                        break;
                    case AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED:
                        int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                        handleBluetoothScoStateChange(state);
                        break;
                    case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                        handleAudioBecomingNoisy();
                        break;
                }
            }
        };
    }

    private void handleWiredHeadsetConnection(boolean connected) {
        store.dispatch(new AudioDeviceAction.WiredHeadsetStateChanged(connected));
        if (connected) {
            AudioDevice headset = new AudioDevice(AudioDeviceType.WIRED_HEADSET, "wired_headset", "Wired Headset");
            store.dispatch(new AudioDeviceAction.DeviceConnected(headset));
        } else {
            AudioDevice headset = new AudioDevice(AudioDeviceType.WIRED_HEADSET, "wired_headset", "Wired Headset");
            store.dispatch(new AudioDeviceAction.DeviceDisconnected(headset));
        }
        updateAvailableDevices();
    }

    private void handleBluetoothScoStateChange(int state) {
        boolean connected = state == AudioManager.SCO_AUDIO_STATE_CONNECTED;
        store.dispatch(new AudioDeviceAction.BluetoothStateChanged(connected));
        if (connected) {
            AudioDevice bluetooth = new AudioDevice(AudioDeviceType.BLUETOOTH, "bluetooth", "Bluetooth");
            store.dispatch(new AudioDeviceAction.DeviceConnected(bluetooth));
        } else {
            AudioDevice bluetooth = new AudioDevice(AudioDeviceType.BLUETOOTH, "bluetooth", "Bluetooth");
            store.dispatch(new AudioDeviceAction.DeviceDisconnected(bluetooth));
        }
        updateAvailableDevices();
    }

    private void handleAudioBecomingNoisy() {
        // Switch to speaker when audio becomes noisy (e.g. headphones unplugged)
        AudioDevice speaker = new AudioDevice(AudioDeviceType.SPEAKER, "speaker", "Speaker");
        selectDevice(speaker);
    }

    private void updateAvailableDevices() {
        List<AudioDevice> devices = new ArrayList<>();
        
        // Speaker is always available
        devices.add(new AudioDevice(AudioDeviceType.SPEAKER, "speaker", "Speaker"));

        // Check for wired headset
        if (audioManager.isWiredHeadsetOn()) {
            devices.add(new AudioDevice(AudioDeviceType.WIRED_HEADSET, "wired_headset", "Wired Headset"));
        }

        // Check for bluetooth
        if (audioManager.isBluetoothScoAvailableOffCall()) {
            devices.add(new AudioDevice(AudioDeviceType.BLUETOOTH, "bluetooth", "Bluetooth"));
        }

        store.dispatch(new AudioDeviceAction.DevicesDiscovered(devices));
    }
}
