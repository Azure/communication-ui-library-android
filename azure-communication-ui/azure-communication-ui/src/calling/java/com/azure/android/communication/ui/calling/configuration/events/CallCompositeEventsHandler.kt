// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration.events

import com.azure.android.communication.ui.calling.CallingEventHandler

internal class CallCompositeEventsHandler {
    private var errorHandlers: CallingEventHandler<CommunicationUIErrorEvent>? = null
    private var remoteParticipantJoinedHandler: CallingEventHandler<CommunicationUIRemoteParticipantJoinedEvent>? =
        null

    fun getOnErrorHandler() = errorHandlers

    fun setOnErrorHandler(errorHandler: CallingEventHandler<CommunicationUIErrorEvent>?) {
        errorHandlers = errorHandler
    }

    fun getOnRemoteParticipantJoinedHandler() = remoteParticipantJoinedHandler

    fun setOnRemoteParticipantJoinedHandler(handler: CallingEventHandler<CommunicationUIRemoteParticipantJoinedEvent>?) {
        remoteParticipantJoinedHandler = handler
    }
}
