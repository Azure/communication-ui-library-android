// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.utilities.implementation.FeatureFlags
import kotlinx.coroutines.flow.collect
import android.media.AudioDeviceInfo
import android.os.Build

import androidx.core.content.ContextCompat.getSystemService
import com.azure.android.communication.ui.redux.state.PermissionStatus

internal class AudioSessionManager(
    private val store: Store<ReduxState>,
    private val context: Context,
    private val audioManager: AudioManager,
) : BluetoothProfile.ServiceListener, BroadcastReceiver() {

    private var bluetoothAudioProxy: BluetoothHeadset? = null

    private val isBluetoothScoAvailable
        get() = try {
            FeatureFlags.BluetoothAudio.active &&
                (bluetoothAudioProxy?.connectedDevices?.size ?: 0 > 0)
        } catch (exception: SecurityException) {
            false
        }

    private val bluetoothDeviceName: String
        get() = try {
            bluetoothAudioProxy?.connectedDevices?.firstOrNull()?.name
                ?: context.getString(R.string.azure_communication_ui_audio_device_drawer_bluetooth)
        } catch (exception: SecurityException) {
            context.getString(R.string.azure_communication_ui_audio_device_drawer_bluetooth)
        }

    private var previousPermissionState: PermissionStatus = PermissionStatus.UNKNOWN

    private var previousAudioDeviceSelectionStatus: AudioDeviceSelectionStatus? = null
    private var priorToBluetoothAudioSelectionStatus: AudioDeviceSelectionStatus? = null

    private val btAdapter: BluetoothAdapter? get() {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter
    }

    suspend fun start() {

        btAdapter?.run {
            getProfileProxy(context, this@AudioSessionManager, BluetoothProfile.HEADSET)
        }

        val filter = IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
        filter.addAction(AudioManager.ACTION_HEADSET_PLUG)

        context.registerReceiver(this@AudioSessionManager, filter)

        initializeAudioDeviceState()
        updateBluetoothStatus()
        updateHeadphoneStatus()

        store.getStateFlow().collect {
            if (previousAudioDeviceSelectionStatus == null ||
                previousAudioDeviceSelectionStatus != it.localParticipantState.audioState.device
            ) {
                onAudioDeviceStateChange(it.localParticipantState.audioState.device)
            }

            // After permission is granted, double check bluetooth status
            if (it.permissionState.audioPermissionState == PermissionStatus.GRANTED &&
                previousPermissionState != PermissionStatus.GRANTED
            ) {
                updateBluetoothStatus()
            }

            previousAudioDeviceSelectionStatus = it.localParticipantState.audioState.device
            previousPermissionState = it.permissionState.audioPermissionState
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.apply {
            when (action) {
                BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED -> updateBluetoothStatus()
                AudioManager.ACTION_HEADSET_PLUG -> updateHeadphoneStatus()
            }
        }
    }

    private fun updateHeadphoneStatus() {
        store.dispatch(LocalParticipantAction.AudioDeviceHeadsetAvailable(isHeadsetActive()))
    }

    // Update the status of bluetooth
    // Connect a headset automatically if bluetooth is connected
    // When disconnected revert to "Speaker"
    // When disconnected (and not selected), just update availability
    private fun updateBluetoothStatus() {
        if (!isBluetoothScoAvailable &&
            store.getCurrentState().localParticipantState.audioState.bluetoothState.available &&
            store.getCurrentState().localParticipantState.audioState.device == AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED
        ) {
            store.dispatch(
                LocalParticipantAction.AudioDeviceChangeRequested(
                    when (priorToBluetoothAudioSelectionStatus) {
                        AudioDeviceSelectionStatus.RECEIVER_SELECTED -> AudioDeviceSelectionStatus.RECEIVER_REQUESTED
                        else -> AudioDeviceSelectionStatus.SPEAKER_REQUESTED
                    }
                )
            )
            // If bluetooth dropped, go back to last device
        }

        if (isBluetoothScoAvailable && !store.getCurrentState().localParticipantState.audioState.bluetoothState.available) {
            // If Bluetooth has been connected and wasn't, switch to it
            store.dispatch(
                LocalParticipantAction.AudioDeviceBluetoothSCOAvailable(
                    isBluetoothScoAvailable,
                    bluetoothDeviceName
                )
            )

            priorToBluetoothAudioSelectionStatus = store.getCurrentState().localParticipantState.audioState.device
            store.dispatch(
                LocalParticipantAction.AudioDeviceChangeRequested(
                    AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED
                )
            )
        } else {
            // If bluetooth wasn't selected and is just being disconnected, just do the flag
            store.dispatch(
                LocalParticipantAction.AudioDeviceBluetoothSCOAvailable(
                    isBluetoothScoAvailable,
                    bluetoothDeviceName
                )
            )
        }
    }

    private fun isHeadsetActive(): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (deviceInfo in audioDevices) {
                if (deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                    deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADSET
                ) {
                    return true
                }
            }
            return false
        } else {
            return audioManager.isWiredHeadsetOn
        }
        return false
    }

    private fun initializeAudioDeviceState() {
        when {
            audioManager.isSpeakerphoneOn ->
                store.dispatch(
                    LocalParticipantAction.AudioDeviceChangeSucceeded(
                        AudioDeviceSelectionStatus.SPEAKER_SELECTED
                    )
                )
            audioManager.isBluetoothScoOn ->
                store.dispatch(
                    LocalParticipantAction.AudioDeviceChangeSucceeded(
                        AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED
                    )
                )
            else ->
                store.dispatch(
                    LocalParticipantAction.AudioDeviceChangeSucceeded(
                        AudioDeviceSelectionStatus.RECEIVER_SELECTED
                    )
                )
        }
    }

    private fun onAudioDeviceStateChange(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        when (audioDeviceSelectionStatus) {
            AudioDeviceSelectionStatus.SPEAKER_REQUESTED, AudioDeviceSelectionStatus.RECEIVER_REQUESTED, AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED ->
                switchAudioDevice(audioDeviceSelectionStatus)
        }
    }

    private fun switchAudioDevice(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        when (audioDeviceSelectionStatus) {
            AudioDeviceSelectionStatus.SPEAKER_REQUESTED -> {
                enableSpeakerPhone()
                store.dispatch(
                    LocalParticipantAction.AudioDeviceChangeSucceeded(
                        AudioDeviceSelectionStatus.SPEAKER_SELECTED
                    )
                )
            }
            AudioDeviceSelectionStatus.RECEIVER_REQUESTED -> {
                enableEarpiece()
                store.dispatch(
                    LocalParticipantAction.AudioDeviceChangeSucceeded(
                        AudioDeviceSelectionStatus.RECEIVER_SELECTED
                    )
                )
            }
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED -> {
                enableBluetooth()
                store.dispatch(
                    LocalParticipantAction.AudioDeviceChangeSucceeded(
                        AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED
                    )
                )
            }
        }
    }

    private fun enableSpeakerPhone() {
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        audioManager.isSpeakerphoneOn = true
    }

    private fun enableEarpiece() {
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        audioManager.isSpeakerphoneOn = false
    }

    private fun enableBluetooth() {
        audioManager.startBluetoothSco()
        audioManager.isBluetoothScoOn = true
        audioManager.isSpeakerphoneOn = false
    }

    fun stop() {
        btAdapter?.run {
            closeProfileProxy(BluetoothProfile.HEADSET, bluetoothAudioProxy)
        }
        context.unregisterReceiver(this)
    }

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
        bluetoothAudioProxy = proxy as BluetoothHeadset
        updateBluetoothStatus()
    }

    override fun onServiceDisconnected(profile: Int) {
        bluetoothAudioProxy = null
    }
}
