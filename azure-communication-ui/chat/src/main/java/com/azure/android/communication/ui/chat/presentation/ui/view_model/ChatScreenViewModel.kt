package com.azure.android.communication.ui.chat.presentation.ui.view_model

import com.azure.android.communication.chat.models.ChatMessage

import com.azure.android.communication.ui.chat.redux.state.ReduxState

// View Model for the Chat Screen
data class ChatScreenViewModel(val messages:List<ChatMessage>, val state: String, var buildCount: Int)

// Internal counter for early debugging
private var buildCount = 0

// Methods to Build the Chat Screen View Model from the Store
internal fun buildChatScreenViewModel(
    state: ReduxState,
    messages: List<ChatMessage>) =
        ChatScreenViewModel(
            messages = messages,
            state = state.chatState.chatStatus.name,
            buildCount = buildCount++
        )