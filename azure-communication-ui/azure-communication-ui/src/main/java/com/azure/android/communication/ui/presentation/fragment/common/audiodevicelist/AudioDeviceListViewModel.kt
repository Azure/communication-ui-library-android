// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.common.audiodevicelist

import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.AudioState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class AudioDeviceListViewModel(private val dispatch: (Action) -> Unit) {

    private val displayAudioDeviceSelectionMenuMutableStateFlow = MutableStateFlow(false)

    private lateinit var audioStateMutableStateFlow: MutableStateFlow<AudioState>
    val displayAudioDeviceSelectionMenuStateFlow = displayAudioDeviceSelectionMenuMutableStateFlow as StateFlow<Boolean>
    val audioStateFlow get() = audioStateMutableStateFlow as StateFlow<AudioState>

    fun init(audioState: AudioState) {
        audioStateMutableStateFlow = MutableStateFlow(audioState)
    }

    fun update(audioState: AudioState) {
        audioStateMutableStateFlow.value = audioState
    }

    fun switchAudioDevice(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        dispatch(
            LocalParticipantAction.AudioDeviceChangeRequested(
                audioDeviceSelectionStatus
            )
        )
    }

    fun displayAudioDeviceSelectionMenu() {
        displayAudioDeviceSelectionMenuMutableStateFlow.value = true
    }

    fun closeAudioDeviceSelectionMenu() {
        displayAudioDeviceSelectionMenuMutableStateFlow.value = false
    }
}
