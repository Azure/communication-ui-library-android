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
    private lateinit var displayCaptionsInfoViewFlow: MutableStateFlow<Boolean>
    private lateinit var captionsStartInProgressStateFlow: MutableStateFlow<Boolean>
    fun getDisplayCaptionsInfoViewFlow(): StateFlow<Boolean> = displayCaptionsInfoViewFlow
    fun getCaptionsStartProgressStateFlow(): StateFlow<Boolean> = captionsStartInProgressStateFlow

    fun update(
        captionsState: CaptionsState,
        visibilityState: VisibilityState
    ) {
        displayCaptionsInfoViewFlow.value = canShowCaptionsUI(visibilityState, captionsState)
        captionsStartInProgressStateFlow.value = canShowCaptionsStartInProgressUI(captionsState)
    }

    fun init(
        captionsState: CaptionsState,
        visibilityState: VisibilityState
    ) {
        displayCaptionsInfoViewFlow = MutableStateFlow(canShowCaptionsUI(visibilityState, captionsState))
        captionsStartInProgressStateFlow = MutableStateFlow(canShowCaptionsStartInProgressUI(captionsState))
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
