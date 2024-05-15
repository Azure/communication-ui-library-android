// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration.events

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent
import com.azure.android.communication.ui.calling.models.CallCompositePictureInPictureChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantJoinedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeUserReportedIssueEvent

internal class CallCompositeEventsHandler {
    private val errorHandlers = mutableListOf<CallCompositeEventHandler<CallCompositeErrorEvent>>()
    private val remoteParticipantJoinedHandlers =
        mutableListOf<CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>>()
    private val callStateHandlers =
        mutableListOf<CallCompositeEventHandler<CallCompositeCallStateChangedEvent>>()
    private val exitEventHandlers =
        mutableListOf<CallCompositeEventHandler<CallCompositeDismissedEvent>>()
    private val userReportHandlers =
        mutableListOf<CallCompositeEventHandler<CallCompositeUserReportedIssueEvent>>()
    private val multitaskingStateChangedEvent =
        mutableListOf<CallCompositeEventHandler<CallCompositePictureInPictureChangedEvent>>()

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

    fun getOnMultitaskingStateChangedEventHandlers() = multitaskingStateChangedEvent.asIterable()
    fun addOnMultitaskingStateChangedEventHandler(handler: CallCompositeEventHandler<CallCompositePictureInPictureChangedEvent>) =
        multitaskingStateChangedEvent.add(handler)

    fun removeOnMultitaskingStateChangedEventHandler(handler: CallCompositeEventHandler<CallCompositePictureInPictureChangedEvent>) =
        multitaskingStateChangedEvent.remove(handler)

    fun getCallStateHandler() = callStateHandlers.asIterable()

    fun removeOnCallStateEventHandler(eventHandler: CallCompositeEventHandler<CallCompositeCallStateChangedEvent>) =
        callStateHandlers.remove(eventHandler)

    fun addOnCallStateChangedEventHandler(eventHandler: CallCompositeEventHandler<CallCompositeCallStateChangedEvent>) =
        callStateHandlers.add(eventHandler)

    fun getOnExitEventHandlers() = exitEventHandlers.asIterable()

    fun addOnDismissedEventHandler(handler: CallCompositeEventHandler<CallCompositeDismissedEvent>) {
        exitEventHandlers.add(handler)
    }

    fun removeOnExitEventHandler(handler: CallCompositeEventHandler<CallCompositeDismissedEvent>) {
        exitEventHandlers.remove(handler)
    }

    fun addOnUserReportedEventHandler(errorHandler: CallCompositeEventHandler<CallCompositeUserReportedIssueEvent>) =
        userReportHandlers.add(errorHandler)

    fun removeOnUserReportedEventHandler(errorHandler: CallCompositeEventHandler<CallCompositeUserReportedIssueEvent>) =
        userReportHandlers.remove(errorHandler)

    fun getOnUserReportedHandlers() = userReportHandlers.asIterable()
}
