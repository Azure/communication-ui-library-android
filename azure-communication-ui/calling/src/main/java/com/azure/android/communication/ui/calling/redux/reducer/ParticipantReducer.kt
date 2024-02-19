// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.ParticipantAction
import com.azure.android.communication.ui.calling.redux.state.RemoteParticipantsState

internal interface ParticipantStateReducer : Reducer<RemoteParticipantsState>

internal class ParticipantStateReducerImpl :
    ParticipantStateReducer {
    override fun reduce(
        state: RemoteParticipantsState,
        action: Action,
    ): RemoteParticipantsState {
        return when (action) {
            is ParticipantAction.ListUpdated -> {
                state.copy(participantMap = action.participantMap, participantMapModifiedTimestamp = System.currentTimeMillis())
            }
            is ParticipantAction.DominantSpeakersUpdated -> {
                state.copy(
                    dominantSpeakersInfo = action.dominantSpeakersInfo,
                    dominantSpeakersModifiedTimestamp = System.currentTimeMillis(),
                )
            }
            is ParticipantAction.LobbyError -> {
                state.copy(lobbyErrorCode = action.code)
            }
            is ParticipantAction.ClearLobbyError -> {
                state.copy(lobbyErrorCode = null)
            }
            else -> state
        }
    }
}
