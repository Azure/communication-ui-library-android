// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.events

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent

class CallStateEventHandler(private val handler: CallCompositeEventsHandler) :
    CallCompositeEventHandler<CallCompositeCallStateChangedEvent> {
    override fun handle(callStateEvent: CallCompositeCallStateChangedEvent) {
        handler.onCallStateChanged(callStateEvent)
    }
}
