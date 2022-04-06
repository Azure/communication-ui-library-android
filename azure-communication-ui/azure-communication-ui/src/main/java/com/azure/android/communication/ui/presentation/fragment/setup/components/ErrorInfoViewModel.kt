// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.error.CallStateError
import com.azure.android.communication.ui.redux.state.ErrorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ErrorInfoViewModel {
    private val callStateErrorFlow: MutableStateFlow<CallStateError?> = MutableStateFlow(null)

    fun update(errorState: ErrorState) {
        callStateErrorFlow.value = errorState.callStateError
    }

    fun getCallStateErrorStateFlow(): StateFlow<CallStateError?> {
        return callStateErrorFlow
    }
}
