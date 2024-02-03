// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.implementation.Redux.reducer

import com.azure.android.communication.ui.calling.models.CallCompositeLobbyErrorCode
import com.azure.android.communication.ui.calling.redux.action.ParticipantAction
import com.azure.android.communication.ui.calling.redux.reducer.ParticipantStateReducerImpl
import com.azure.android.communication.ui.calling.redux.state.RemoteParticipantsState
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class ParticipantActionUnitTest {
    @Test
    fun participantReducer_reduce_when_lobbyError_then_changeStateToErrorCode() {

        // arrange
        val reducer = ParticipantStateReducerImpl()
        val oldState = RemoteParticipantsState(
            emptyMap(),
            0,
            emptyList(),
            0,
            null
        )
        val action = ParticipantAction.LobbyError(CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS)

        // assert
        Assert.assertEquals(
            null,
            oldState.lobbyErrorCode
        )

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(
            CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS,
            newState.lobbyErrorCode
        )
    }

    @Test
    fun participantReducer_reduce_when_clearLobbyError_then_changeStateToNull() {

        // arrange
        val reducer = ParticipantStateReducerImpl()
        val oldState = RemoteParticipantsState(
            emptyMap(),
            0,
            emptyList(),
            0,
            CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS
        )
        val action = ParticipantAction.ClearLobbyError()

        // assert
        Assert.assertEquals(
            CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS,
            oldState.lobbyErrorCode
        )

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(
            null,
            newState.lobbyErrorCode
        )
    }
}
