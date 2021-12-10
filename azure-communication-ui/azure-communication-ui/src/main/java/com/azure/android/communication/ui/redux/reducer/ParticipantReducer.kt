// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.reducer

import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.ParticipantAction
import com.azure.android.communication.ui.redux.state.RemoteParticipantsState

internal interface ParticipantStateReducer : Reducer<RemoteParticipantsState>

internal class ParticipantStateReducerImpl :
    ParticipantStateReducer {
    override fun reduce(state: RemoteParticipantsState, action: Action): RemoteParticipantsState {
        return when (action) {
            is ParticipantAction.ListUpdated -> {
                RemoteParticipantsState(action.participantMap, System.currentTimeMillis())
            }
            else -> state
        }
    }
}
