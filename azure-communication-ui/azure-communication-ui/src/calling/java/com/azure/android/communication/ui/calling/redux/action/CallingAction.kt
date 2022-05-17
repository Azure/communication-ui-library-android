// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

import com.azure.android.communication.ui.calling.redux.state.CallingStatus

internal sealed class CallingAction :
    Action {
    sealed class SetupCall : CallingAction()
    sealed class CallStartRequested : CallingAction()
    sealed class CallEndRequested : CallingAction()
    sealed class HoldRequested : CallingAction()
    sealed class StateUpdated(val callingState: CallingStatus) : CallingAction()

    sealed class IsRecordingUpdated(val isRecording: Boolean) : CallingAction()
    sealed class IsTranscribingUpdated(val isTranscribing: Boolean) : CallingAction()
}
