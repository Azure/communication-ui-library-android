// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.reducer

import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ParticipantAction
import com.azure.android.communication.ui.chat.redux.state.ParticipantsState

internal interface ParticipantsReducer : Reducer<ParticipantsState>

internal class ParticipantsReducerImpl : ParticipantsReducer {
    override fun reduce(state: ParticipantsState, action: Action): ParticipantsState =
        when (action) {
            is ParticipantAction.ParticipantsAdded -> {
                state.copy(participants = state.participants + action.participants.associateBy { it.userIdentifier.id })
            }
            is ParticipantAction.ParticipantsRemoved -> {
                state.copy(
                    participants = state.participants - action.participants.map { it.userIdentifier.id },
                    participantTyping = state.participantTyping - action.participants.map { it.userIdentifier.id }
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
            else -> state
        }
}
