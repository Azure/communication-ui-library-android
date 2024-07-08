// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class CaptionsLinearLayoutViewModel {
    private lateinit var displayCaptionsInfoViewFlow: MutableStateFlow<Boolean>
    private lateinit var captionsStatusStateFlow: MutableStateFlow<CaptionsStatus>
    fun getDisplayCaptionsInfoViewFlow(): StateFlow<Boolean> = displayCaptionsInfoViewFlow
    fun getCaptionsStatusStateFlow(): StateFlow<CaptionsStatus> = captionsStatusStateFlow

    fun update(
        captionsState: CaptionsState,
        visibilityState: VisibilityState
    ) {
        displayCaptionsInfoViewFlow.value = canShowCaptionsUI(visibilityState, captionsState)
        captionsStatusStateFlow.value = captionsState.status
    }

    fun init(
        captionsState: CaptionsState,
        visibilityState: VisibilityState
    ) {
        displayCaptionsInfoViewFlow = MutableStateFlow(canShowCaptionsUI(visibilityState, captionsState))
        captionsStatusStateFlow = MutableStateFlow(captionsState.status)
    }

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
