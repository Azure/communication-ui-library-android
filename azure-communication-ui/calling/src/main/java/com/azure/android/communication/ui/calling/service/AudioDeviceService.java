package com.azure.android.communication.ui.calling.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.azure.android.communication.ui.calling.redux.state.AudioDevice;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceType;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing system audio routing and device detection.
 */
public final class AudioDeviceService {
    private final Context context;
    private final AudioManager audioManager;
    private final AudioDeviceCallback callback;
    private final BroadcastReceiver audioDeviceReceiver;

    /**
     * Creates a new instance of AudioDeviceService.
     *
     * @param context Android context
     * @param callback Callback for audio device events
     */
    public AudioDeviceService(Context context, AudioDeviceCallback callback) {
        this.context = context;
        this.callback = callback;
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
        notifyAvailableDevices();
    }

    /**
     * Stop monitoring audio device changes.
     */
    public void stop() {
        context.unregisterReceiver(audioDeviceReceiver);
    }

    /**
     * Configure system audio routing for a specific device.
     *
     * @param device The device to configure
     * @return true if configuration was successful
     */
    public boolean configureAudioRouting(AudioDevice device) {
        try {
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
            return true;
        } catch (Exception e) {
            return false;
        }
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
        callback.onWiredHeadsetStateChanged(connected);
        AudioDevice headset = new AudioDevice(AudioDeviceType.WIRED_HEADSET, "wired_headset", "Wired Headset");
        if (connected) {
            callback.onDeviceConnected(headset);
        } else {
            callback.onDeviceDisconnected(headset);
        }
        notifyAvailableDevices();
    }

    private void handleBluetoothScoStateChange(int state) {
        boolean connected = state == AudioManager.SCO_AUDIO_STATE_CONNECTED;
        callback.onBluetoothStateChanged(connected);
        AudioDevice bluetooth = new AudioDevice(AudioDeviceType.BLUETOOTH, "bluetooth", "Bluetooth");
        if (connected) {
            callback.onDeviceConnected(bluetooth);
        } else {
            callback.onDeviceDisconnected(bluetooth);
        }
        notifyAvailableDevices();
    }

    private void handleAudioBecomingNoisy() {
        AudioDevice speaker = new AudioDevice(AudioDeviceType.SPEAKER, "speaker", "Speaker");
        configureAudioRouting(speaker);
        callback.onAudioBecomingNoisy();
    }

    private void notifyAvailableDevices() {
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

        callback.onAvailableDevicesChanged(devices);
    }

    /**
     * Callback interface for audio device events.
     */
    public interface AudioDeviceCallback {
        void onDeviceConnected(AudioDevice device);
        void onDeviceDisconnected(AudioDevice device);
        void onAvailableDevicesChanged(List<AudioDevice> devices);
        void onWiredHeadsetStateChanged(boolean connected);
        void onBluetoothStateChanged(boolean connected);
        void onAudioBecomingNoisy();
    }
}
