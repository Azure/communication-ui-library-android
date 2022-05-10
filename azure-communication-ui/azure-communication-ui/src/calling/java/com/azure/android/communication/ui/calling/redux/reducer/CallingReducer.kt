// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.CallingState

internal interface CallStateReducer : Reducer<CallingState>

internal class CallStateReducerImpl : CallStateReducer {
    override fun reduce(callingState: CallingState, action: Action): CallingState {
        return when (action) {
            is CallingAction.StateUpdated -> {
                callingState.copy(callingStatus = action.callingState, joinCallIsRequested = false)
            }
            is CallingAction.IsRecordingUpdated -> {
                callingState.copy(isRecording = action.isRecording)
            }
            is CallingAction.IsTranscribingUpdated -> {
                callingState.copy(isTranscribing = action.isTranscribing)
            }
            is CallingAction.CallStartRequested -> {
                callingState.copy(joinCallIsRequested = true)
            }
            else -> callingState
        }
    }
}
