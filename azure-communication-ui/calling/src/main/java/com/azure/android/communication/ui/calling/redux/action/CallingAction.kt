// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

import com.azure.android.communication.ui.calling.redux.state.CallStatus

internal sealed class CallingAction : Action {
    class SetupCall : CallingAction()
    class CallStartRequested : CallingAction()
    class CallEndRequested : CallingAction()
    class HoldRequested : CallingAction()
    class ResumeRequested : CallingAction()
    class StateUpdated(val callingState: CallStatus) : CallingAction()
    class IsRecordingUpdated(val isRecording: Boolean) : CallingAction()
    class IsTranscribingUpdated(val isTranscribing: Boolean) : CallingAction()
    class CallIdUpdated(val callId: String?) : CallingAction()
    class CallRequestedWithoutSetup : CallingAction()
}
