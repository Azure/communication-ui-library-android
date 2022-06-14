// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.ParticipantAction
import com.azure.android.communication.ui.calling.redux.state.RemoteParticipantsState
import org.reduxkotlin.Reducer


internal class ParticipantStateReducer : Reducer<RemoteParticipantsState> {
    override fun invoke(state: RemoteParticipantsState, action: Any): RemoteParticipantsState {
        return when (action) {
            is ParticipantAction.ListUpdated -> {
                RemoteParticipantsState(action.participantMap, System.currentTimeMillis())
            }
            else -> state
        }
    }
}
