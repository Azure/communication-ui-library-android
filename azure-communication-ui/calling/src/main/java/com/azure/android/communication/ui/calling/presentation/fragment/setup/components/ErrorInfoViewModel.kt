// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import com.azure.android.communication.ui.calling.error.CallCompositeError
import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.redux.state.ErrorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ErrorInfoViewModel {
    private val callStateErrorFlow: MutableStateFlow<CallStateError?> = MutableStateFlow(null)
    private val _callCompositeErrorFlow: MutableStateFlow<CallCompositeError?> = MutableStateFlow(null)
    private val audioFocusRejectedFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var callCompositeErrorFlow = _callCompositeErrorFlow

    fun updateCallStateError(errorState: ErrorState) {
        callStateErrorFlow.value = errorState.callStateError
    }

    fun updateCallCompositeError(error: CallCompositeError) {
        callCompositeErrorFlow.value = error
    }

    fun updateAudioFocusRejectedState(isAudioFocusRejected: Boolean) {
        audioFocusRejectedFlow.value = isAudioFocusRejected
    }

    fun getCallStateErrorStateFlow(): StateFlow<CallStateError?> {
        return callStateErrorFlow
    }

    fun getAudioFocusRejectedFlow(): MutableStateFlow<Boolean> {
        return audioFocusRejectedFlow
    }
}
