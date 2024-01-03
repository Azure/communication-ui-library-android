// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.events

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEndEvent

class IncomingCallEndEventHandler(private val handler: CallCompositeEventsHandler) :
    CallCompositeEventHandler<CallCompositeIncomingCallEndEvent> {
    override fun handle(eventArgs: CallCompositeIncomingCallEndEvent) {
        handler.onIncomingCallEnd(eventArgs)
    }
}
