// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.reducer

import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.action.ParticipantAction
import com.azure.android.communication.ui.chat.redux.state.ParticipantsState

internal interface ParticipantsReducer : Reducer<ParticipantsState>

internal class ParticipantsReducerImpl : ParticipantsReducer {
    override fun reduce(state: ParticipantsState, action: Action): ParticipantsState =
        when (action) {
            is ParticipantAction.ParticipantsAdded -> {
                state.copy(
                    participants = state.participants + action.participants.associateBy { it.userIdentifier.id },
                    participantsReadReceiptMap = state.participantsReadReceiptMap +
                        action.participants.map {
                            Pair(
                                it.userIdentifier.id,
                                state.latestReadMessageTimestamp
                            )
                        }
                )
            }
            is ParticipantAction.ParticipantsRemoved -> {
                val participantTypingKeys = state.participantTyping.keys
                val removedParticipants = action.participants.map { it.userIdentifier.id }
                var participantTyping = state.participantTyping
                // TODO: improve this logic
                removedParticipants.forEach { id ->
                    participantTyping =
                        participantTyping - participantTypingKeys.filter { it.contains(id) }
                }
                state.copy(
                    participants = state.participants - removedParticipants,
                    participantTyping = participantTyping,
                    participantsReadReceiptMap =
                    state.participantsReadReceiptMap - action.participants.map { it.userIdentifier.id }
                )
            }
            is ParticipantAction.AddParticipantTyping -> {
                val id = action.infoModel.userIdentifier.id
                val displayName = state.participants[id]?.displayName
                if (displayName.isNullOrEmpty()) {
                    state
                } else {
                    // if participant is already typing, remove and add with new timestamp
                    state.copy(
                        participantTyping = state.participantTyping -
                            state.participantTyping.keys.filter { it.contains(id) } +
                            Pair(id + action.infoModel.receivedOn, displayName)
                    )
                }
            }
            is ParticipantAction.RemoveParticipantTyping -> {
                state.copy(participantTyping = state.participantTyping - (action.infoModel.userIdentifier.id + action.infoModel.receivedOn))
            }
            is ChatAction.MessageReceived -> {
                val id = action.message.senderCommunicationIdentifier?.id
                // as the participant is added with timestamp
                // on new message remove if id exists
                // no need to worry about timestamp
                if (id != null) {
                    val participantsTyping =
                        state.participantTyping - state.participantTyping.keys.filter {
                            it.contains(id)
                        }
                    state.copy(participantTyping = participantsTyping)
                } else {
                    state
                }
            }
            is ParticipantAction.ReadReceiptReceived -> {
                val participantsReadReceiptMap = state.participantsReadReceiptMap.toMutableMap()
                participantsReadReceiptMap[action.infoModel.userIdentifier.id] =
                    action.infoModel.receivedOn
                val latestReadMessageTimestamp = participantsReadReceiptMap.values.min()
                state.copy(
                    participantsReadReceiptMap = participantsReadReceiptMap,
                    latestReadMessageTimestamp = latestReadMessageTimestamp
                )
            }
            else -> state
        }
}
