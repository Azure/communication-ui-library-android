// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.handlers

import com.azure.android.communication.ui.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.configuration.events.CommunicationUIRemoteParticipantJoinedEvent
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.redux.state.RemoteParticipantsState
import com.azure.android.communication.ui.service.calling.sdk.CallingSDKRemoteParticipantsCollection
import kotlinx.coroutines.flow.collect

internal class RemoteParticipantJoinedHandler(
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
        if (configuration.callCompositeEventsHandler.getOnRemoteParticipantJoinedHandler() != null &&
            remoteParticipantsState.modifiedTimestamp != lastRemoteParticipantsState?.modifiedTimestamp
        ) {
            if (lastRemoteParticipantsState != null) {
                val joinedParticipant =
                    remoteParticipantsState.participantMap.keys.filter { it !in lastRemoteParticipantsState!!.participantMap.keys }
                sendRemoteParticipantJoinedEvent(joinedParticipant)
            } else {
                sendRemoteParticipantJoinedEvent(remoteParticipantsState.participantMap.keys.toList())
            }

            lastRemoteParticipantsState = remoteParticipantsState
        }
    }

    private fun sendRemoteParticipantJoinedEvent(joinedParticipant: List<String>) {
        try {
            joinedParticipant.forEach {
                val eventArgs =
                    CommunicationUIRemoteParticipantJoinedEvent(
                        remoteParticipantsCollection.getRemoteParticipantsMap()
                            .getValue(it).identifier
                    )
                configuration.callCompositeEventsHandler.getOnRemoteParticipantJoinedHandler()
                    ?.handle(eventArgs)
            }
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }
}
