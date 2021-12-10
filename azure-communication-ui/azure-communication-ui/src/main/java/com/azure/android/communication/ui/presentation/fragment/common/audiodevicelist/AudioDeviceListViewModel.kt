// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.common.audiodevicelist

import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class AudioDeviceListViewModel(
    private val dispatch: (Action) -> Unit,
) {
    private lateinit var audioDeviceSelectionStatusStateFlow: MutableStateFlow<AudioDeviceSelectionStatus>
    private val displayAudioDeviceSelectionMenuStateFlow = MutableStateFlow(false)

    fun init(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        audioDeviceSelectionStatusStateFlow = MutableStateFlow(audioDeviceSelectionStatus)
    }

    fun update(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        audioDeviceSelectionStatusStateFlow.value = audioDeviceSelectionStatus
    }

    fun getAudioDeviceSelectionStatusStateFlow(): StateFlow<AudioDeviceSelectionStatus> {
        return audioDeviceSelectionStatusStateFlow
    }

    fun getDisplayAudioDeviceSelectionMenuStateFlow(): StateFlow<Boolean> {
        return displayAudioDeviceSelectionMenuStateFlow
    }

    fun switchAudioDevice(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        dispatchAction(action = LocalParticipantAction.AudioDeviceChangeRequested(audioDeviceSelectionStatus))
    }

    fun displayAudioDeviceSelectionMenu() {
        displayAudioDeviceSelectionMenuStateFlow.value = true
    }

    fun closeAudioDeviceSelectionMenu() {
        displayAudioDeviceSelectionMenuStateFlow.value = false
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }
}
