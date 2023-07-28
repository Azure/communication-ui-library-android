// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration.events

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateEvent
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent
import com.azure.android.communication.ui.calling.models.CallCompositeExitEvent
import com.azure.android.communication.ui.calling.models.CallCompositePictureInPictureChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantJoinedEvent

internal class CallCompositeEventsHandler {
    private val errorHandlers = mutableSetOf<CallCompositeEventHandler<CallCompositeErrorEvent>>()
    private val remoteParticipantJoinedHandlers =
        mutableSetOf<CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>>()
    private val callStateHandlers =
        mutableSetOf<CallCompositeEventHandler<CallCompositeCallStateEvent>>()
    private val exitEventHandlers =
        mutableSetOf<CallCompositeEventHandler<CallCompositeExitEvent>>()

    private val multitaskingStateChangedEvent =
        mutableSetOf<CallCompositeEventHandler<CallCompositePictureInPictureChangedEvent>>()

    fun getOnErrorHandlers() = errorHandlers.asIterable()

    fun addOnErrorEventHandler(errorHandler: CallCompositeEventHandler<CallCompositeErrorEvent>) =
        errorHandlers.add(errorHandler)

    fun removeOnErrorEventHandler(errorHandler: CallCompositeEventHandler<CallCompositeErrorEvent>) =
        errorHandlers.remove(errorHandler)

    fun getOnRemoteParticipantJoinedHandlers() = remoteParticipantJoinedHandlers.asIterable()

    fun addOnRemoteParticipantJoinedEventHandler(handler: CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>) =
        remoteParticipantJoinedHandlers.add(handler)

    fun removeOnRemoteParticipantJoinedEventHandler(handler: CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>) =
        remoteParticipantJoinedHandlers.remove(handler)

    fun getCallStateHandler() = callStateHandlers.asIterable()

    fun removeOnCallStateEventHandler(eventHandler: CallCompositeEventHandler<CallCompositeCallStateEvent>) =
        callStateHandlers.remove(eventHandler)

    fun addOnCallStateEventHandler(eventHandler: CallCompositeEventHandler<CallCompositeCallStateEvent>) =
        callStateHandlers.add(eventHandler)

    fun getOnExitEventHandlers() = exitEventHandlers.asIterable()

    fun addOnExitEventHandler(handler: CallCompositeEventHandler<CallCompositeExitEvent>) {
        exitEventHandlers.add(handler)
    }

    fun removeOnExitEventHandler(handler: CallCompositeEventHandler<CallCompositeExitEvent>) {
        exitEventHandlers.remove(handler)
    }

    fun getOnMultitaskingStateChangedEventHandlers() = multitaskingStateChangedEvent.asIterable()
    fun addOnMultitaskingStateChangedEventHandler(handler: CallCompositeEventHandler<CallCompositePictureInPictureChangedEvent>) =
        multitaskingStateChangedEvent.add(handler)

    fun removeOnMultitaskingStateChangedEventHandler(handler: CallCompositeEventHandler<CallCompositePictureInPictureChangedEvent>) =
        multitaskingStateChangedEvent.remove(handler)
}
