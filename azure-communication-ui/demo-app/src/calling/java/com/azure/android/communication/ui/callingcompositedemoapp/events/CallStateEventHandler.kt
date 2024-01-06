// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.events

import android.util.Log
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity

class CallStateEventHandler(private val handler: CallCompositeEventsHandler) :
    CallCompositeEventHandler<CallCompositeCallStateChangedEvent> {
    override fun handle(callStateEvent: CallCompositeCallStateChangedEvent) {
        handler.onCallStateChanged(callStateEvent)
        Log.d(CallLauncherActivity.TAG, "handle: ${callStateEvent.code} ${callStateEvent.callEndReasonCode}")
    }
}
