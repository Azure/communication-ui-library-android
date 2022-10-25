// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.reducer

import com.azure.android.communication.ui.chat.models.ParticipantTimestampInfoModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.redux.action.ParticipantAction
import com.azure.android.communication.ui.chat.redux.state.ParticipantsState
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.OffsetDateTime

@RunWith(MockitoJUnitRunner::class)
class ParticipantsReducerUnitTest {
    private val userOne = RemoteParticipantInfoModel(
        userIdentifier = CommunicationIdentifier.UnknownIdentifier("7A13DD2C-B49F-4521-9364-975F12F6E333"),
        "One"
    )
    private val userTwo = RemoteParticipantInfoModel(
        userIdentifier = CommunicationIdentifier.UnknownIdentifier("931804B1-D72E-4E70-BFEA-7813C7761BD2"),
        "Two"
    )
    private val userThree = RemoteParticipantInfoModel(
        userIdentifier = CommunicationIdentifier.UnknownIdentifier("152D5D76-3DDC-44BE-873F-A4575F8C91DF"),
        "Three"
    )
    private val userFour = RemoteParticipantInfoModel(
        userIdentifier = CommunicationIdentifier.UnknownIdentifier("85FF2697-2ABB-480E-ACCA-09EBE3D6F5EC"),
        "Four"
    )

    @Test
    fun participantsReducer_reduce_when_actionAddParticipants_then_changeParticipantStateParticipants() {
        // arrange
        val reducer = ParticipantsReducerImpl()
        val previousState = ParticipantsState(
            participants = listOf(userOne, userTwo).associateBy { it.userIdentifier.id },
            participantTyping = listOf(userOne.displayName!!, userTwo.displayName!!)
        )
        val action = ParticipantAction.ParticipantsAdded(participants = listOf(userThree, userFour))

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(
            newState.participants,
            listOf(userOne, userTwo, userThree, userFour).associateBy({ it.userIdentifier.id })
        )
    }

    @Test
    fun participantsReducer_reduce_when_actionAddParticipants_PartiallyExisting_then_changeParticipantStateParticipants() {
        // arrange
        val reducer = ParticipantsReducerImpl()
        val previousState = ParticipantsState(
            participants = listOf(userOne, userTwo).associateBy { it.userIdentifier.id },
            participantTyping = listOf(userOne.displayName!!, userTwo.displayName!!)
        )
        val userTwo_duplicate = RemoteParticipantInfoModel(
            userIdentifier = CommunicationIdentifier.UnknownIdentifier("931804B1-D72E-4E70-BFEA-7813C7761BD2"),
            "Two"
        )
        val action = ParticipantAction.ParticipantsAdded(
            participants = listOf(
                userTwo_duplicate,
                userThree,
                userFour
            )
        )

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(
            newState.participants,
            listOf(userOne, userTwo, userThree, userFour).associateBy({ it.userIdentifier.id })
        )
    }

    @Test
    fun participantsReducer_reduce_when_actionAddParticipants_AllExisting_then_DontChangeParticipantStateParticipants() {
        // arrange
        val reducer = ParticipantsReducerImpl()
        val previousState = ParticipantsState(
            participants = listOf(userOne, userTwo).associateBy { it.userIdentifier.id },
            participantTyping = listOf(userOne.displayName!!, userTwo.displayName!!)
        )
        val userOne_duplicate = RemoteParticipantInfoModel(
            userIdentifier = CommunicationIdentifier.UnknownIdentifier("7A13DD2C-B49F-4521-9364-975F12F6E333"),
            "One"
        )
        val userTwo_duplicate = RemoteParticipantInfoModel(
            userIdentifier = CommunicationIdentifier.UnknownIdentifier("931804B1-D72E-4E70-BFEA-7813C7761BD2"),
            "Two"
        )
        val action = ParticipantAction.ParticipantsAdded(
            participants = listOf(
                userOne_duplicate,
                userTwo_duplicate
            )
        )

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(newState.participants, previousState.participants)
    }

    @Test
    fun participantsReducer_reduce_when_actionRemoveParticipants_then_changeParticipantStateParticipants() {
        // arrange
        val reducer = ParticipantsReducerImpl()
        val previousState = ParticipantsState(

            participants = listOf(userOne, userTwo, userThree, userFour).associateBy { it.userIdentifier.id },
            participantTyping = listOf(userOne.displayName!!, userTwo.displayName!!)
        )
        val action =
            ParticipantAction.ParticipantsRemoved(participants = listOf(userThree, userFour))

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(
            newState.participants,
            listOf(userOne, userTwo).associateBy { it.userIdentifier.id }
        )
    }

    @Test
    fun participantsReducer_reduce_when_actionTypingIndicatorReceived_then_changeParticipantStateParticipants() {
        // arrange
        val reducer = ParticipantsReducerImpl()
        val previousState = ParticipantsState(
            participants = listOf(userOne, userTwo).associateBy { it.userIdentifier.id },
            participantTyping = listOf()
        )
        val action = ParticipantAction.TypingIndicatorReceived(
            infoModel = ParticipantTimestampInfoModel(
                userIdentifier = userOne.userIdentifier,
                receivedOn = OffsetDateTime.MIN
            )
        )

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(
            "invalid participantTyping: ${newState.participantTyping}",
            listOf(userOne.displayName!!),
            newState.participantTyping
        )
    }

    @Test
    fun participantsReducer_reduce_when_TypingIndicatorReceived_but_not_participant() {
        // arrange
        val reducer = ParticipantsReducerImpl()
        val previousState = ParticipantsState(
            participants = listOf(userOne, userTwo).associateBy { it.userIdentifier.id },
            participantTyping = listOf()
        )
        val action = ParticipantAction.TypingIndicatorReceived(
            infoModel = ParticipantTimestampInfoModel(
                userIdentifier = userFour.userIdentifier,
                receivedOn = OffsetDateTime.MIN
            )
        )

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(
            "invalid participantTyping: ${newState.participantTyping}",
            listOf<String>(),
            newState.participantTyping
        )
    }

    @Test
    fun participantsReducer_reduce_when_actionTypingIndicatorReceived_for_dupe_changeParticipantStateParticipants() {
        // arrange
        val reducer = ParticipantsReducerImpl()
        val previousState = ParticipantsState(
            participants = listOf(userOne, userTwo).associateBy { it.userIdentifier.id },
            participantTyping = listOf(userOne.displayName!!)
        )
        val action = ParticipantAction.TypingIndicatorReceived(
            infoModel = ParticipantTimestampInfoModel(
                userIdentifier = userOne.userIdentifier,
                receivedOn = OffsetDateTime.MIN
            )
        )

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(
            "invalid participantTyping: ${newState.participantTyping}",
            listOf(userOne.displayName!!, userOne.displayName),
            newState.participantTyping,
        )
    }

    @Test
    fun participantsReducer_reduce_when_actionTypingIndicatorCleared_then_changeParticipantStateParticipants() {
        // arrange
        val reducer = ParticipantsReducerImpl()
        val previousState = ParticipantsState(
            participants = listOf(userOne, userTwo).associateBy { it.userIdentifier.id },
            participantTyping = listOf(userOne.displayName!!)
        )
        val action = ParticipantAction.TypingIndicatorClear(
            infoModel = ParticipantTimestampInfoModel(
                userIdentifier = userOne.userIdentifier,
                receivedOn = OffsetDateTime.MIN
            )
        )

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(
            "invalid participantTyping: ${newState.participantTyping}",
            listOf<String>(),
            newState.participantTyping
        )
    }

    @Test
    fun participantsReducer_reduce_when_actionTypingIndicatorCleared_rcvd_but_not_participant() {
        // arrange
        val reducer = ParticipantsReducerImpl()
        val previousState = ParticipantsState(
            participants = listOf(userOne, userTwo).associateBy { it.userIdentifier.id },
            participantTyping = listOf(userOne.displayName!!)
        )
        val action = ParticipantAction.TypingIndicatorClear(
            infoModel = ParticipantTimestampInfoModel(
                userIdentifier = userFour.userIdentifier,
                receivedOn = OffsetDateTime.MIN
            )
        )

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(
            "invalid participantTyping: ${newState.participantTyping}",
            previousState.participantTyping,
            newState.participantTyping
        )
    }
}
