// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import android.app.Activity
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import kotlinx.coroutines.flow.collect

internal class AudioSessionManager(
    private val store: Store<ReduxState>,
) : AudioDeviceCallback() {
    private lateinit var audioManager: AudioManager

    private var audioDevices : Array<AudioDeviceInfo> = emptyArray();
    private val isBluetoothScoAvailable get() =
        audioDevices.fold(false) { acc, info -> acc || info.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO}


    private var previousAudioDeviceSelectionStatus: AudioDeviceSelectionStatus? = null
    suspend fun start(
        activity: Activity,
        audioManager: AudioManager,
    ) {
        this.audioManager = audioManager
        activity.volumeControlStream = AudioManager.STREAM_VOICE_CALL
        initializeAudioDeviceState()
        audioManager.registerAudioDeviceCallback(this, null);
        refreshDevices()
        store.getStateFlow().collect {
            if (previousAudioDeviceSelectionStatus == null ||
                previousAudioDeviceSelectionStatus != it.localParticipantState.audioState.device
            ) {
                onAudioDeviceStateChange(it.localParticipantState.audioState.device)
            }
            previousAudioDeviceSelectionStatus = it.localParticipantState.audioState.device
        }


    }

    override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
        super.onAudioDevicesAdded(addedDevices)
        refreshDevices()
    }

    override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
        super.onAudioDevicesRemoved(removedDevices)
        refreshDevices()
    }

    private fun refreshDevices() {
        audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        store.dispatch(LocalParticipantAction.AudioDeviceBluetoothSCOAvailable(isBluetoothScoAvailable))
    }

    private fun initializeAudioDeviceState() {
        when {
            audioManager.isSpeakerphoneOn ->
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.SPEAKER_SELECTED))
            audioManager.isBluetoothScoOn ->
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED))
            else ->
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.RECEIVER_SELECTED))

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
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.SPEAKER_SELECTED))
            }
            AudioDeviceSelectionStatus.RECEIVER_REQUESTED -> {
                enableEarpiece()
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.RECEIVER_SELECTED))
            }
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED -> {
                enableBluetooth()
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED))
            }
        }
    }



    private fun enableSpeakerPhone() {
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        audioManager.isSpeakerphoneOn = true
    }

    private fun enableEarpiece() {
        audioManager.stopBluetoothSco();
        audioManager.isBluetoothScoOn = false
        audioManager.isSpeakerphoneOn = false
    }

    private fun enableBluetooth() {
        audioManager.startBluetoothSco();
        audioManager.isBluetoothScoOn = true
        audioManager.isSpeakerphoneOn = false
    }

    fun stop() {
        audioManager.unregisterAudioDeviceCallback(this)
    }
}
