package com.azure.android.communication.ui.chat.implementation.ui.mock

import android.content.Context
import android.util.Log
import com.azure.android.communication.ui.arch.redux.GenericStore
import com.azure.android.communication.ui.chat.implementation.redux.actions.MockUIActions
import com.azure.android.communication.ui.chat.implementation.redux.states.MockParticipant
import com.azure.android.communication.ui.chat.implementation.redux.states.MockUIChatState
import java.util.Random
import java.util.Timer
import kotlin.concurrent.fixedRateTimer

class MeetingRobot(context: Context, private val store: GenericStore) {
    private var meetingRobotTimer: Timer? = null

    private val outsideMeetingMockParticipants = ArrayList<MockParticipant>()
    private val random = Random(123)

    private val participants = 10

    // Chance to drop out of a meeting if not joined
    private val dropoutChance get() = ((1 - outsideMeetingMockParticipants.size / participants.toDouble()) / 4.0)

    // Chance to join a meeting if not in it
    private val joinChance get() = (outsideMeetingMockParticipants.size / participants.toDouble()) + 0.2
    private val startTypingChance = 0.2
    private val stopTypingChance = 0.5
    private val postMessageChance = 1.0
    private val nameFaker = NameFaker(context)
    private val messageFaker = MessageFaker(context)
    init {

        for (i in 1..participants) {
            outsideMeetingMockParticipants.add(MockParticipant(nameFaker.completeName()))
        }
    }

    fun start() {
        meetingRobotTimer = fixedRateTimer("MeetingRobot", false, 1.toLong(), 500.toLong()) {
            Log.i("Meeting Robot", "Iteration Hit")
            iterate()
        }
    }

    fun stop() {
        meetingRobotTimer?.cancel()
        meetingRobotTimer = null
    }

    private fun iterate() {

        addParticipantIfPossible()
        randomlyRemoveParticipant()
        randomlyStartTyping()
        randomlyStopTypingAndRandomlyPost()
    }

    private fun addParticipantIfPossible() {
        if (random.nextDouble() < joinChance) {
            if (outsideMeetingMockParticipants.size > 0) {
                val remoteParticipant = outsideMeetingMockParticipants.removeFirst()
                store.dispatch(MockUIActions.AddParticipant(remoteParticipant))
            }
        }
    }

    private fun randomlyRemoveParticipant() {
        if (random.nextDouble() < dropoutChance) {
            val mockState = store.getCurrentState().getSubState<MockUIChatState>()
            val remoteParticipants = mockState.mockParticipants.filter { !it.isCurrentUser && !it.isTyping }
            if (remoteParticipants.isNotEmpty()) {
                val randomParticipant = remoteParticipants.random()
                store.dispatch(MockUIActions.RemoveParticipant(randomParticipant))
                this.outsideMeetingMockParticipants.add(randomParticipant)
            }
        }
    }

    private fun randomlyStartTyping() {
        if (random.nextDouble() < startTypingChance) {
            val mockState = store.getCurrentState().getSubState<MockUIChatState>()
            val remoteParticipants = mockState.mockParticipants.filter { !it.isCurrentUser && !it.isTyping }
            if (remoteParticipants.isNotEmpty()) {

                store.dispatch(MockUIActions.StartParticipantTyping(remoteParticipants.random()))
            }
        }
    }

    private fun randomlyStopTypingAndRandomlyPost() {
        if (random.nextDouble() < stopTypingChance) {
            val mockState = store.getCurrentState().getSubState<MockUIChatState>()
            val remoteParticipants = mockState.mockParticipants.filter { !it.isCurrentUser && it.isTyping }
            if (remoteParticipants.isNotEmpty()) {
                val participant = remoteParticipants.random()
                store.dispatch(MockUIActions.StopParticipantTyping(participant))
                if (random.nextDouble() < postMessageChance) {
                    store.dispatch(
                        MockUIActions.PostMessage(
                            messageFaker.yoda.speak(),
                            participant
                        )
                    )
                }
            }
        }
    }
}
