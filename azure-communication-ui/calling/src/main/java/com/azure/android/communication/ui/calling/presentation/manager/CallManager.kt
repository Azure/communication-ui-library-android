// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.error.ErrorHandler
import com.azure.android.communication.ui.calling.models.CallCompositeErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState

internal class CallManager(
    private val errorHandler: ErrorHandler,
    private val store: Store<ReduxState>,
) {
    fun hangup() {
        if (store.getCurrentState().callState.callingStatus == CallingStatus.CONNECTED) {
            store.dispatch(action = CallingAction.CallEndRequested())
        } else {
            val eventArgs =
                CallCompositeErrorEvent(
                    CallCompositeErrorCode.CALL_END_FAILED,
                    Throwable("call state should be connected to end call"),
                )
            errorHandler.notifyErrorEvent(eventArgs)
        }
    }
}
