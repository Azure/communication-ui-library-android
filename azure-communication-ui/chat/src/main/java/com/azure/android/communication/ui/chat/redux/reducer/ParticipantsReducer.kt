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
            }
            is ParticipantAction.TypingIndicatorReceived -> {
                val participantsTyping = HashSet<String>()
                participantsTyping.addAll(state.participantTyping)
                state.participants.forEach {
                    if (it.value.userIdentifier.id == action.message.userIdentifier.id) {
                        participantsTyping.add(it.value.displayName ?: "Unknown")
                    }
                }
                state.copy(participantTyping = participantsTyping)
            }
            else -> state
        }
}
