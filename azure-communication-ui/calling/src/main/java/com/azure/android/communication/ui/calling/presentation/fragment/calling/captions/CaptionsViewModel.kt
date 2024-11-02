// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class CaptionsViewModel {
    private lateinit var displayCaptionsInfoViewMutableFlow: MutableStateFlow<Boolean>
    private lateinit var captionsStartInProgressStateMutableFlow: MutableStateFlow<Boolean>
    val displayCaptionsInfoViewFlow: StateFlow<Boolean>
        get() = displayCaptionsInfoViewMutableFlow
    val captionsStartProgressStateFlow: StateFlow<Boolean>
        get() = captionsStartInProgressStateMutableFlow

    fun update(
        captionsState: CaptionsState,
        visibilityState: VisibilityState
    ) {
        displayCaptionsInfoViewMutableFlow.value = canShowCaptionsUI(visibilityState, captionsState)
        captionsStartInProgressStateMutableFlow.value = canShowCaptionsStartInProgressUI(captionsState)
    }

    fun init(
        captionsState: CaptionsState,
        visibilityState: VisibilityState
    ) {
        displayCaptionsInfoViewMutableFlow = MutableStateFlow(canShowCaptionsUI(visibilityState, captionsState))
        captionsStartInProgressStateMutableFlow = MutableStateFlow(canShowCaptionsStartInProgressUI(captionsState))
    }

    private fun canShowCaptionsStartInProgressUI(
        captionsState: CaptionsState
    ) = captionsState.status == CaptionsStatus.START_REQUESTED

    private fun canShowCaptionsUI(
        visibilityState: VisibilityState,
        captionsState: CaptionsState
    ) =
        visibilityState.status == VisibilityStatus.VISIBLE && (
            captionsState.status == CaptionsStatus.STARTED ||
                captionsState.status == CaptionsStatus.START_REQUESTED ||
                captionsState.status == CaptionsStatus.STOP_REQUESTED
            )
}
