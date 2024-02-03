// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.implementation.Redux.reducer

import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantStatus
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
                isCameraDisabled = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                participantStatus = ParticipantStatus.HOLD,
            )
        val reducer = ParticipantStateReducerImpl()
        val oldState = RemoteParticipantsState(HashMap(), 0, listOf(), 0, null)

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
                isCameraDisabled = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                participantStatus = ParticipantStatus.HOLD,
            )
        val reducer = ParticipantStateReducerImpl()
        val oldState = RemoteParticipantsState(HashMap(), 0, listOf(), 0, null)
        val action = NavigationAction.CallLaunched()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        assertEquals(oldState, newState)
    }

    @Test
    fun participantListStateReducer_reduce_when_actionDominantSpeakersUpdated_then_updateState() {
        // arrange
        val participantMap: MutableMap<String, ParticipantInfoModel> = HashMap()
        participantMap["user"] =
            ParticipantInfoModel(
                "user", "id",
                isMuted = false,
                isCameraDisabled = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                participantStatus = ParticipantStatus.HOLD,
            )
        val dominantSpeakers = listOf<String>()
        val reducer = ParticipantStateReducerImpl()
        val oldState = RemoteParticipantsState(HashMap(), 0, dominantSpeakers, 0, null)

        val updatedDominantSpeakers = listOf("id")
        val action = ParticipantAction.DominantSpeakersUpdated(updatedDominantSpeakers)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        assertEquals(updatedDominantSpeakers, newState.dominantSpeakersInfo)
    }
}
