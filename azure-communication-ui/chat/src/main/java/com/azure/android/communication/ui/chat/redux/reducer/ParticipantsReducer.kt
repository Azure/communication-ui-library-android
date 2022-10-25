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
                state.copy(participants = state.participants - action.participants.map { it.userIdentifier.id })
                state.copy(participantTyping = state.participantTyping - action.participants.map { it.userIdentifier.id })
            }
            is ParticipantAction.TypingIndicatorReceived -> {
                val id = action.infoModel.userIdentifier.id
                // TODO: for localization create const and compare in UI to replace
                val displayName = state.participants[id]?.displayName ?: "unknown"
                state.copy(participantTyping = state.participantTyping + Pair(id, displayName))
            }
            is ParticipantAction.TypingIndicatorClear -> {
                state.copy(participantTyping = state.participantTyping - action.infoModel.userIdentifier.id)
            }
            else -> state
        }
}
