// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CaptionsError
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class CaptionsErrorHeaderViewModel(private val dispatch: (Action) -> Unit) {
    private lateinit var displayCaptionsErrorHeaderFlow: MutableStateFlow<Boolean>
    private lateinit var captionsErrorFlow: MutableStateFlow<CaptionsError?>

    fun getDisplayCaptionsErrorHeaderFlow(): StateFlow<Boolean> = displayCaptionsErrorHeaderFlow

    fun getCaptionsErrorFlow(): StateFlow<CaptionsError?> = captionsErrorFlow

    fun update(
        callingStatus: CallingStatus,
        error: CaptionsError?,
        visibilityStatus: VisibilityState
    ) {
        displayCaptionsErrorHeaderFlow.value = error != null &&
            callingStatus == CallingStatus.CONNECTED &&
            visibilityStatus.status == VisibilityStatus.VISIBLE
        captionsErrorFlow.value = error
    }

    fun init(
        callingStatus: CallingStatus,
        error: CaptionsError?,
        visibilityStatus: VisibilityState
    ) {
        displayCaptionsErrorHeaderFlow = MutableStateFlow(
            error != null &&
                callingStatus == CallingStatus.CONNECTED &&
                visibilityStatus.status == VisibilityStatus.VISIBLE
        )
        captionsErrorFlow = MutableStateFlow(error)
    }

    fun close() {
        dispatch(CaptionsAction.ClearError())
    }
}
