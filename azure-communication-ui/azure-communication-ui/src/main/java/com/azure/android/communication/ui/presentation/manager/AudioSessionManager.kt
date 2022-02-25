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
import com.azure.android.communication.ui.utilities.FeatureFlags

internal class AudioSessionManager(
    private val store: Store<ReduxState>,
    private val context: Context,
    private val audioManager: AudioManager,
) : BluetoothProfile.ServiceListener, BroadcastReceiver() {

    private var bluetoothAudioProxy: BluetoothHeadset? = null

    private var started = false

    private val isBluetoothScoAvailable
        get() =
            FeatureFlags.BluetoothAudio.active &&
                (bluetoothAudioProxy?.connectedDevices?.size ?: 0 > 0)

    fun start() {
        if (started) return
        started = true

        BluetoothAdapter.getDefaultAdapter()?.run {
            getProfileProxy(context, this@AudioSessionManager, BluetoothProfile.HEADSET)

            val filter = IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
            context.registerReceiver(this@AudioSessionManager, filter)
        }

        initializeAudioDeviceState()
        updateBluetoothStatus()

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



    fun stop() {
        BluetoothAdapter.getDefaultAdapter()?.run {
            closeProfileProxy(BluetoothProfile.HEADSET, bluetoothAudioProxy)
            context.unregisterReceiver(this@AudioSessionManager)
        }
        started = false
    }

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
        bluetoothAudioProxy = proxy as BluetoothHeadset
        updateBluetoothStatus()
    }

    override fun onServiceDisconnected(profile: Int) {
        bluetoothAudioProxy = null
    }
}
