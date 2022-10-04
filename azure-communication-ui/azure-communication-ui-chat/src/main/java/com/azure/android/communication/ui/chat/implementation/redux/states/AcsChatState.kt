package com.azure.android.communication.ui.chat.implementation.redux.states

import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessage
import com.azure.android.communication.ui.chat.service.sdk.models.ChatParticipant

internal data class AcsChatState(
    val displayName: String,
    val participants: List<ChatParticipant>,
    val messages: List<ChatMessage>
)
