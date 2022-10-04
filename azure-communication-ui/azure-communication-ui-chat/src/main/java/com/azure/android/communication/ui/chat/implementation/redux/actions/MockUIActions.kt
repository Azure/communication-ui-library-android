package com.azure.android.communication.ui.chat.implementation.redux.actions

import com.azure.android.communication.ui.chat.implementation.redux.states.MockParticipant

internal class MockUIActions {
    internal data class PostMessage(val message: String, val mockParticipant: MockParticipant)
    internal data class StartParticipantTyping(val mockParticipant: MockParticipant)
    internal data class StopParticipantTyping(val mockParticipant: MockParticipant)

    internal data class AddParticipant(val mockParticipant: MockParticipant)
    internal data class RemoveParticipant(val mockParticipant: MockParticipant)
}
