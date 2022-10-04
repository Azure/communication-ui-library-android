package com.azure.android.communication.ui.chat.implementation.redux.states

import androidx.annotation.DrawableRes
import java.time.LocalDateTime
import kotlin.random.Random

// Since this reducer requires list modification
// if we let this list get way too big, it'll start getting really slow
private const val MaxMessages = 200

data class MockParticipant(
    val displayName: String,
    val id: Int = Random.nextInt(),
    @DrawableRes val drawableAvatar: Int = -1,
    val isCurrentUser: Boolean = false,
    val isTyping: Boolean = false
)

data class MockMessage(val mockParticipant: MockParticipant, val message: String, val receivedAt: LocalDateTime = LocalDateTime.now())

data class MockUIChatState(
    val mockParticipants: List<MockParticipant>,
    val mockMessages: List<MockMessage>
) {

    // Helper Methods to copy state and modify it

    val activeMockParticipant: MockParticipant = mockParticipants.first { it.isCurrentUser }

    // Add a New Message and return a new State
    fun addMessage(mockMessage: MockMessage): MockUIChatState {
        val mutableList = mutableListOf<MockMessage>()
        mutableList.addAll(mockMessages)
        mutableList.add(mockMessage)
        if (mutableList.size > MaxMessages) {
            mutableList.removeAt(0)
        }
        return MockUIChatState(this.mockParticipants, mutableList)
    }

    fun addParticipant(mockParticipant: MockParticipant): MockUIChatState {
        val mutableList = mutableListOf<MockParticipant>()
        mutableList.addAll(mockParticipants)
        mutableList.add(mockParticipant)
        return MockUIChatState(mutableList, mockMessages)
    }

    fun removeParticipant(mockParticipant: MockParticipant): MockUIChatState {
        return MockUIChatState(mockParticipants.filter { it.id != mockParticipant.id }, mockMessages)
    }

    fun startParticipantTyping(mockParticipant: MockParticipant): MockUIChatState {
        val mutableList = mutableListOf<MockParticipant>()
        mutableList.addAll(mockParticipants)
        val participantIdx = mutableList.indexOfFirst { it.id == mockParticipant.id }
        if (participantIdx != -1) {
            val typingParticipant = mutableList[participantIdx].copy(isTyping = true)
            mutableList[participantIdx] = typingParticipant
        }
        return MockUIChatState(mutableList, mockMessages)
    }

    fun stopParticipantTyping(mockParticipant: MockParticipant): MockUIChatState {
        val mutableList = mutableListOf<MockParticipant>()
        mutableList.addAll(mockParticipants)
        val participantIdx = mutableList.indexOfFirst { it.id == mockParticipant.id }
        if (participantIdx != -1) {
            val typingParticipant = mutableList[participantIdx].copy(isTyping = false)
            mutableList[participantIdx] = typingParticipant
        }
        return MockUIChatState(mutableList, mockMessages)
    }
}
