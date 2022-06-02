// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration.events

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantJoinedEvent

internal class CallCompositeEventsHandler {
    private var errorHandlers: CallCompositeEventHandler<CallCompositeErrorEvent>? = null
    private var remoteParticipantJoinedHandler: CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>? =
        null

    fun getOnErrorHandler() = errorHandlers

    fun setOnErrorHandler(errorHandler: CallCompositeEventHandler<CallCompositeErrorEvent>?) {
        errorHandlers = errorHandler
    }

    fun getOnRemoteParticipantJoinedHandler() = remoteParticipantJoinedHandler

    fun setOnRemoteParticipantJoinedHandler(handler: CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>?) {
        remoteParticipantJoinedHandler = handler
    }
}
