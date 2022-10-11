package com.azure.android.communication.ui.chat.redux.reducer

import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.redux.action.ParticipantAction
import com.azure.android.communication.ui.chat.redux.state.ParticipantsState
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ParticipantsReducerUnitTest {
    private val userOne = RemoteParticipantInfoModel(userIdentifier = CommunicationIdentifier.UnknownIdentifier("7A13DD2C-B49F-4521-9364-975F12F6E333"), "One")
    private val userTwo = RemoteParticipantInfoModel(userIdentifier = CommunicationIdentifier.UnknownIdentifier("931804B1-D72E-4E70-BFEA-7813C7761BD2"), "Two")
    private val userThree = RemoteParticipantInfoModel(userIdentifier = CommunicationIdentifier.UnknownIdentifier("152D5D76-3DDC-44BE-873F-A4575F8C91DF"), "Three")
    private val userFour = RemoteParticipantInfoModel(userIdentifier = CommunicationIdentifier.UnknownIdentifier("85FF2697-2ABB-480E-ACCA-09EBE3D6F5EC"), "Four")

    @Test
    fun participantsReducer_reduce_when_actionAddParticipants_then_changeParticipantStateParticipants() {
        // arrange
        val reducer = ParticipantsReducerImpl()
        val previousState = ParticipantsState(
            participants = listOf(
                userOne,
                userTwo
            )
        )
        val action = ParticipantAction.ParticipantsAdded(participants = listOf(userThree, userFour))

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(newState.participants, listOf(userOne, userTwo, userThree, userFour))
    }

    @Test
    fun participantsReducer_reduce_when_actionAddParticipants_PartiallyExisting_then_changeParticipantStateParticipants() {
        // arrange
        val reducer = ParticipantsReducerImpl()
        val previousState = ParticipantsState(
            participants = listOf(
                userOne,
                userTwo
            )
        )
        val userTwo_duplicate = RemoteParticipantInfoModel(userIdentifier = CommunicationIdentifier.UnknownIdentifier("931804B1-D72E-4E70-BFEA-7813C7761BD2"), "Two")
        val action = ParticipantAction.ParticipantsAdded(participants = listOf(userTwo_duplicate, userThree, userFour))

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(newState.participants, listOf(userOne, userTwo, userThree, userFour))
    }

    @Test
    fun participantsReducer_reduce_when_actionAddParticipants_AllExisting_then_DontChangeParticipantStateParticipants() {
        // arrange
        val reducer = ParticipantsReducerImpl()
        val previousState = ParticipantsState(
            participants = listOf(
                userOne,
                userTwo
            )
        )
        val userOne_duplicate = RemoteParticipantInfoModel(userIdentifier = CommunicationIdentifier.UnknownIdentifier("7A13DD2C-B49F-4521-9364-975F12F6E333"), "One")
        val userTwo_duplicate = RemoteParticipantInfoModel(userIdentifier = CommunicationIdentifier.UnknownIdentifier("931804B1-D72E-4E70-BFEA-7813C7761BD2"), "Two")
        val action = ParticipantAction.ParticipantsAdded(participants = listOf(userOne_duplicate, userTwo_duplicate))

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(newState.participants, previousState.participants)
    }

    @Test
    fun participantsReducer_reduce_when_actionRemoveParticipants_then_changeParticipantStateParticipants() {
        // arrange
        val reducer = ParticipantsReducerImpl()
        val previousState = ParticipantsState(participants = listOf(userOne, userTwo, userThree, userFour))
        val action = ParticipantAction.ParticipantsRemoved(participants = listOf(userThree, userFour))

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(newState.participants, listOf(userOne, userTwo))
    }
}
