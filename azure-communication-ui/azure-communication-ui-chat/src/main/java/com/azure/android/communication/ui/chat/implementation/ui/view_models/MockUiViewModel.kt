package com.azure.android.communication.ui.chat.implementation.ui.view_models

import com.azure.android.communication.ui.chat.implementation.redux.states.MockMessage
import com.azure.android.communication.ui.chat.implementation.redux.states.MockParticipant

// / A Very Simple View Model for the Mock UI to Consume
data class MockUiViewModel(
    val mockParticipants: List<MockParticipant>,
    val mockMessages: List<MockMessage>,
    val postMessage: (String) -> Unit,
    val onUserTyping: (String) -> Unit
) {
    val participantCount get() = mockParticipants.size
    val localMockParticipant: MockParticipant get() = mockParticipants.first { it.isCurrentUser }
    val typingParticipants get() = mockParticipants.filter { it.isTyping }
}
