// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.events

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent

class ErrorEventHandler(
    private val handler: CallCompositeEventsHandler
) : CallCompositeEventHandler<CallCompositeErrorEvent> {

    override fun handle(it: CallCompositeErrorEvent) {
        handler.showError("${it.errorCode} ${it.cause?.message}")
    }
}
