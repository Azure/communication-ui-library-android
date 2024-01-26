// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.events

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEndedEvent

class IncomingCallEndEventHandler(private val handler: CallCompositeEventsHandler) :
    CallCompositeEventHandler<CallCompositeIncomingCallEndedEvent> {
    override fun handle(eventArgs: CallCompositeIncomingCallEndedEvent) {
        handler.onIncomingCallEnd(eventArgs)
    }
}
