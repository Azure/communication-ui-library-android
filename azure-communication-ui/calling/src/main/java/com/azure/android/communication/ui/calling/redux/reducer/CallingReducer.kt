// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.CallingState
import org.threeten.bp.OffsetDateTime

internal interface CallStateReducer : Reducer<CallingState>

internal class CallStateReducerImpl : CallStateReducer {
    override fun reduce(callingState: CallingState, action: Action): CallingState {
        return when (action) {
            is CallingAction.StateUpdated -> {
                callingState.copy(
                    callingStatus = action.callingState, joinCallIsRequested = false,
                    callEndReasonCode = action.callEndReasonCode, callEndReasonSubCode = action.callEndReasonSubCode
                )
            }
            is CallingAction.IsRecordingUpdated -> {
                callingState.copy(isRecording = action.isRecording)
            }
            is CallingAction.IsTranscribingUpdated -> {
                callingState.copy(isTranscribing = action.isTranscribing)
            }
            is CallingAction.CallStartRequested -> {
                callingState.copy(joinCallIsRequested = true, callStartDateTime = OffsetDateTime.now())
            }
            is CallingAction.CallIdUpdated -> {
                callingState.copy(callId = action.callId)
            }
            is CallingAction.CallRequestedWithoutSetup -> {
                callingState.copy(isDefaultParametersCallStarted = true)
            }
            is CallingAction.CallStartTimeUpdated -> {
                callingState.copy(callStartTime = action.callStartTime)
            }
            else -> callingState
        }
    }
}
