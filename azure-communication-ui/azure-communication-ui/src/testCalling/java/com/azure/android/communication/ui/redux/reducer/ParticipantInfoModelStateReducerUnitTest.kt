// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.reducer

import com.azure.android.communication.ui.calling.model.ParticipantInfoModel
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.action.ParticipantAction
import com.azure.android.communication.ui.calling.redux.reducer.ParticipantStateReducerImpl
import com.azure.android.communication.ui.calling.redux.state.RemoteParticipantsState
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class ParticipantInfoModelStateReducerUnitTest {
    @Test
    fun participantListStateReducer_reduce_when_actionParticipantListActionUpdate_then_updateState() {
        // arrange
        val participantMap: MutableMap<String, ParticipantInfoModel> = HashMap()
        participantMap["user"] =
            ParticipantInfoModel(
                "user", "id",
                isMuted = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                speakingTimestamp = 0
            )
        val reducer = ParticipantStateReducerImpl()
        val oldState = RemoteParticipantsState(HashMap(), 0)

        val action = ParticipantAction.ListUpdated(participantMap)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        assertEquals(participantMap, newState.participantMap)
    }

    @Test
    fun participantListStateReducer_reduce_when_actionNotUsed_then_noStateUpdate() {
        // arrange
        val participantMap: MutableMap<String, ParticipantInfoModel> = HashMap()
        participantMap["user"] =
            ParticipantInfoModel(
                "user", "id",
                isMuted = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                speakingTimestamp = 0
            )
        val reducer = ParticipantStateReducerImpl()
        val oldState = RemoteParticipantsState(HashMap(), 0)
        val action = NavigationAction.CallLaunched()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        assertEquals(oldState, newState)
    }
}
