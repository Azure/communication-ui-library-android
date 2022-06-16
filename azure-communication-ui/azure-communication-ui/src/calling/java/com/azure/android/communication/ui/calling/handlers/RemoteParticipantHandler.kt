// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.handlers

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantJoinedEvent
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.RemoteParticipantsState
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKRemoteParticipantsCollection
import kotlinx.coroutines.flow.collect

internal class RemoteParticipantHandler(
    private val configuration: CallCompositeConfiguration,
    private val store: Store<ReduxState>,
    private val remoteParticipantsCollection: CallingSDKRemoteParticipantsCollection,
) {
    private var lastRemoteParticipantsState: RemoteParticipantsState? = null

    suspend fun start() {
        store.getStateFlow().collect {
            onStateChanged(it.remoteParticipantState)
        }
    }

    private fun onStateChanged(remoteParticipantsState: RemoteParticipantsState) {
        if (remoteParticipantsState.modifiedTimestamp != lastRemoteParticipantsState?.modifiedTimestamp) {
            if (configuration.callCompositeEventsHandler.getOnRemoteParticipantJoinedHandlers().any()) {
                if (lastRemoteParticipantsState != null) {
                    val joinedParticipants =
                        remoteParticipantsState.participantMap.keys.filter { it !in lastRemoteParticipantsState!!.participantMap.keys }
                    sendRemoteParticipantJoinedEvent(joinedParticipants)
                } else {
                    sendRemoteParticipantJoinedEvent(remoteParticipantsState.participantMap.keys.toList())
                }
            }

            val leftParticipants =
                lastRemoteParticipantsState?.participantMap?.keys?.filter { it !in remoteParticipantsState.participantMap.keys }
            leftParticipants?.forEach {
                configuration.remoteParticipantsConfiguration.removeParticipantViewData(it)
            }

            lastRemoteParticipantsState = remoteParticipantsState
        }
    }

    private fun sendRemoteParticipantJoinedEvent(joinedParticipant: List<String>) {
        try {
            if (joinedParticipant.isNotEmpty()) {
                val identifiers = mutableListOf<CommunicationIdentifier>()
                joinedParticipant.forEach {
                    identifiers.add(
                        remoteParticipantsCollection.getRemoteParticipantsMap()
                            .getValue(it).identifier
                    )
                }
                val eventArgs = CallCompositeRemoteParticipantJoinedEvent(identifiers)
                configuration.callCompositeEventsHandler.getOnRemoteParticipantJoinedHandlers()
                    .forEach { it.handle(eventArgs) }
            }
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }
}
