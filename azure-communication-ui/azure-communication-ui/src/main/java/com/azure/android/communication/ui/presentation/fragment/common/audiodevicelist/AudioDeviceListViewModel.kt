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

    private lateinit var audioDeviceSelectionStatusMutableStateFlow: MutableStateFlow<AudioDeviceSelectionStatus>
    private val bluetoothScoAvailableMutableStateFlow = MutableStateFlow(false)
    private val displayAudioDeviceSelectionMenuMutableStateFlow = MutableStateFlow(false)

    val bluetoothScoAvailableStateFlow get() = bluetoothScoAvailableMutableStateFlow as StateFlow<Boolean>
    val displayAudioDeviceSelectionMenuStateFlow get() = displayAudioDeviceSelectionMenuMutableStateFlow as StateFlow<Boolean>
    val audioDeviceSelectionStatusStateFlow get() = audioDeviceSelectionStatusMutableStateFlow as StateFlow<AudioDeviceSelectionStatus>

    fun init(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        audioDeviceSelectionStatusMutableStateFlow = MutableStateFlow(audioDeviceSelectionStatus)
    }

    fun update(audioDeviceSelectionStatus: AudioDeviceSelectionStatus, isBluetoothSCOAvailable: Boolean) {
        audioDeviceSelectionStatusMutableStateFlow.value = audioDeviceSelectionStatus
        bluetoothScoAvailableMutableStateFlow.value = isBluetoothSCOAvailable
    }

    fun switchAudioDevice(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        dispatchAction(action = LocalParticipantAction.AudioDeviceChangeRequested(audioDeviceSelectionStatus))
    }

    fun displayAudioDeviceSelectionMenu() {
        displayAudioDeviceSelectionMenuMutableStateFlow.value = true
    }

    fun closeAudioDeviceSelectionMenu() {
        displayAudioDeviceSelectionMenuMutableStateFlow.value = false
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }
}
