// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.utilities.implementation.FeatureFlags
import kotlinx.coroutines.flow.collect
import java.lang.IllegalArgumentException

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

    private var previousAudioDeviceSelectionStatus: AudioDeviceSelectionStatus? = null

    suspend fun start() {
        BluetoothAdapter.getDefaultAdapter()?.run {
            getProfileProxy(context, this@AudioSessionManager, BluetoothProfile.HEADSET)

            val filter = IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)

            try {
                context.unregisterReceiver(this@AudioSessionManager)
            } catch (exception: IllegalArgumentException) {
                // Unregister in case already registered
                // IllegalArgs expected in case that it's not registered
                // Do nothing in case of exception
            }
            context.registerReceiver(this@AudioSessionManager, filter)
        }

        initializeAudioDeviceState()
        updateBluetoothStatus()
        store.getStateFlow().collect {
            if (previousAudioDeviceSelectionStatus == null ||
                previousAudioDeviceSelectionStatus != it.localParticipantState.audioState.device
            ) {
                onAudioDeviceStateChange(it.localParticipantState.audioState.device)
            }

            previousAudioDeviceSelectionStatus = it.localParticipantState.audioState.device
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) = updateBluetoothStatus()

    // Update the status of bluetooth
    // Connect a headset automatically if bluetooth is connected
    // When disconnected revert to "Speaker"
    // When disconnected (and not selected), just update availability
    private fun updateBluetoothStatus() {
        if (!isBluetoothScoAvailable &&
            store.getCurrentState().localParticipantState.audioState.isBluetoothSCOAvailable &&
            store.getCurrentState().localParticipantState.audioState.device == AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED
        ) {
            // If bluetooth dropped
            store.dispatch(
                LocalParticipantAction.AudioDeviceChangeRequested(
                    AudioDeviceSelectionStatus.SPEAKER_REQUESTED
                )
            )
        }

        if (isBluetoothScoAvailable && !store.getCurrentState().localParticipantState.audioState.isBluetoothSCOAvailable) {
            // If Bluetooth has been connected and wasn't, switch to it
            store.dispatch(
                LocalParticipantAction.AudioDeviceBluetoothSCOAvailable(
                    isBluetoothScoAvailable
                )
            )
            store.dispatch(
                LocalParticipantAction.AudioDeviceChangeRequested(
                    AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED
                )
            )
        } else {
            // If bluetooth wasn't selected and is just being disconnected, just do the flag
            store.dispatch(
                LocalParticipantAction.AudioDeviceBluetoothSCOAvailable(
                    isBluetoothScoAvailable
                )
            )
        }
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
        BluetoothAdapter.getDefaultAdapter()?.run {
            closeProfileProxy(BluetoothProfile.HEADSET, bluetoothAudioProxy)
            context.unregisterReceiver(this@AudioSessionManager)
        }
    }

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
        bluetoothAudioProxy = proxy as BluetoothHeadset
        updateBluetoothStatus()
    }

    override fun onServiceDisconnected(profile: Int) {
        bluetoothAudioProxy = null
    }
}
