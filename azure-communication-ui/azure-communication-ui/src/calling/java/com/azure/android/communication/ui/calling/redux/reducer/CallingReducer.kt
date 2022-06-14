// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.CallingState
import org.reduxkotlin.Reducer


internal class CallStateReducer : Reducer<CallingState> {
    override fun invoke(state: CallingState, action: Any): CallingState {
        return when (action) {
            is CallingAction.StateUpdated -> {
                state.copy(callingStatus = action.callingState, joinCallIsRequested = false)
            }
            is CallingAction.IsRecordingUpdated -> {
                state.copy(isRecording = action.isRecording)
            }
            is CallingAction.IsTranscribingUpdated -> {
                state.copy(isTranscribing = action.isTranscribing)
            }
            is CallingAction.CallStartRequested -> {
                state.copy(joinCallIsRequested = true)
            }
            else -> state
        }
    }

}
