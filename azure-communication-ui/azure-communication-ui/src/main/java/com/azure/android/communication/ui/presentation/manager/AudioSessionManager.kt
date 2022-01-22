// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import android.app.Activity
import android.media.AudioManager
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import kotlinx.coroutines.flow.collect

internal class AudioSessionManager(
    private val store: Store<ReduxState>,
) {
    private lateinit var audioManager: AudioManager

    private var previousAudioDeviceSelectionStatus: AudioDeviceSelectionStatus? = null
    suspend fun start(
        activity: Activity,
        audioManager: AudioManager,
    ) {
        this.audioManager = audioManager

        activity.volumeControlStream = AudioManager.STREAM_VOICE_CALL
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        initializeAudioDeviceState()
        store.getStateFlow().collect {
            if (previousAudioDeviceSelectionStatus == null ||
                previousAudioDeviceSelectionStatus != it.localParticipantState.audioState.device
            ) {
                onAudioDeviceStateChange(it.localParticipantState.audioState.device)
            }
            previousAudioDeviceSelectionStatus = it.localParticipantState.audioState.device
        }
    }

    private fun initializeAudioDeviceState() {
        if (audioManager.isSpeakerphoneOn) {
            store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.SPEAKER_SELECTED))
        } else {
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
                setSpeakerPhoneStatus(true)
                setBluetoothEnabled(false)
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.SPEAKER_SELECTED))
            }
            AudioDeviceSelectionStatus.RECEIVER_REQUESTED -> {
                setSpeakerPhoneStatus(false)
                setBluetoothEnabled(false)
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.RECEIVER_SELECTED))
            }
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED -> {
                setSpeakerPhoneStatus(true)
                setBluetoothEnabled(true)
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED))
            }
        }
    }

    private fun setSpeakerPhoneStatus(status: Boolean) {
        audioManager.isSpeakerphoneOn = status
    }

    private fun setBluetoothEnabled(enabled: Boolean) {

        if (enabled && !audioManager.isBluetoothScoOn) {

            audioManager.startBluetoothSco()
            audioManager.isBluetoothScoOn = true
        } else if (!enabled && audioManager.isBluetoothScoOn){
            audioManager.stopBluetoothSco()
            audioManager.isBluetoothScoOn = false

        }
    }
}
