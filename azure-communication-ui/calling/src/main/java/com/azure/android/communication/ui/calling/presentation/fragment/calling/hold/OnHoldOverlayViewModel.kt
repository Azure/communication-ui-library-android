// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.hold

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.AudioSessionAction
import com.azure.android.communication.ui.calling.redux.state.AudioFocusStatus
import com.azure.android.communication.ui.calling.redux.state.CallStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class OnHoldOverlayViewModel(private val dispatch: (Action) -> Unit) {
    private lateinit var displayHoldOverlayFlow: MutableStateFlow<Boolean>
    private lateinit var displayMicUsedToast: MutableStateFlow<Boolean>

    fun getDisplayHoldOverlayFlow(): StateFlow<Boolean> = displayHoldOverlayFlow
    fun getDisplayMicUsedToastStateFlow(): StateFlow<Boolean> = displayMicUsedToast

    fun init(
        callingState: CallStatus,
        audioFocusStatus: AudioFocusStatus?,
    ) {
        val displayLobbyOverlay = shouldDisplayHoldOverlay(callingState)
        displayHoldOverlayFlow = MutableStateFlow(displayLobbyOverlay)
        displayMicUsedToast = MutableStateFlow(audioFocusStatus == AudioFocusStatus.REJECTED)
    }

    fun update(
        callingState: CallStatus,
        audioFocusStatus: AudioFocusStatus?,
    ) {
        val displayHoldOverlay = shouldDisplayHoldOverlay(callingState)
        displayHoldOverlayFlow.value = displayHoldOverlay
        displayMicUsedToast.value = audioFocusStatus == AudioFocusStatus.REJECTED
    }

    fun resumeCall() {
        dispatch(AudioSessionAction.AudioFocusRequesting())
    }

    private fun shouldDisplayHoldOverlay(callStatus: CallStatus) =
        callStatus == CallStatus.LOCAL_HOLD
}
