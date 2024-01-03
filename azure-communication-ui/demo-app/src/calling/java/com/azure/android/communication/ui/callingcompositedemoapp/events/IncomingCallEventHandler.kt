// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.events

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEvent

class IncomingCallEventHandler(private val handler: CallCompositeEventsHandler) :
    CallCompositeEventHandler<CallCompositeIncomingCallEvent> {
    override fun handle(eventArgs: CallCompositeIncomingCallEvent) {
        handler.onIncomingCall(eventArgs)
    }
}
