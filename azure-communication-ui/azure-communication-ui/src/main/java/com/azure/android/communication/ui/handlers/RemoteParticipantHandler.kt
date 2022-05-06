// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.handlers

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.configuration.events.CommunicationUIRemoteParticipantJoinedEvent
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.redux.state.RemoteParticipantsState
import com.azure.android.communication.ui.service.calling.sdk.CallingSDKRemoteParticipantsCollection
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
            if (configuration.callCompositeEventsHandler.getOnRemoteParticipantJoinedHandler() != null) {
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
                configuration.remoteParticipantsConfiguration.removePersonaData(it)
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
                val eventArgs = CommunicationUIRemoteParticipantJoinedEvent(identifiers)
                configuration.callCompositeEventsHandler.getOnRemoteParticipantJoinedHandler()
                    ?.handle(eventArgs)
            }
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }
}
