// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.events

import com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEndedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEvent

interface CallCompositeEventsHandler {
    fun showError(message: String)
    fun onAudioSelectionChanged(audioSelection: CallCompositeAudioSelectionChangedEvent)
    fun onExit(event: CallCompositeDismissedEvent)
    fun onCallStateChanged(callStateEvent: CallCompositeCallStateChangedEvent)
    fun onIncomingCall(incomingCall: CallCompositeIncomingCallEvent)
    fun onIncomingCallEnd(incomingCallEnd: CallCompositeIncomingCallEndedEvent)
}
