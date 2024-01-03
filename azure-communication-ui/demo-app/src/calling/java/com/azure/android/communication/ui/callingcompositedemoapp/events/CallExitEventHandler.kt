// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.events

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent

class CallExitEventHandler(private val handler: CallCompositeEventsHandler) : CallCompositeEventHandler<CallCompositeDismissedEvent> {
    override fun handle(event: CallCompositeDismissedEvent) {
        handler.onExit(event)
    }
}
