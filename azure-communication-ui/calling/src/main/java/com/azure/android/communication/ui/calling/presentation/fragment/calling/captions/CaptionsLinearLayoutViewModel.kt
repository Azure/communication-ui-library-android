// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class CaptionsLinearLayoutViewModel {
    private lateinit var displayCaptionsInfoViewFlow: MutableStateFlow<Boolean>

    fun getDisplayCaptionsInfoViewFlow(): StateFlow<Boolean> = displayCaptionsInfoViewFlow

    fun update(
        captionsState: CaptionsState,
        visibilityState: VisibilityState
    ) {
        displayCaptionsInfoViewFlow.value = captionsState.isCaptionsStarted && visibilityState.status == VisibilityStatus.VISIBLE
    }

    fun init(
        isCaptionsStarted: Boolean
    ) {
        displayCaptionsInfoViewFlow = MutableStateFlow(isCaptionsStarted)
    }
}
