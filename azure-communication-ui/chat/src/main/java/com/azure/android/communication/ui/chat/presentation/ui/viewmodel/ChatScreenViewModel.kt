// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.viewmodel

import com.azure.android.communication.ui.chat.error.ChatStateError
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.state.ChatStatus

import com.azure.android.communication.ui.chat.redux.state.ReduxState

// View Model for the Chat Screen
internal data class ChatScreenViewModel(
    val typingParticipants: List<String>,
    val messages: List<MessageViewModel>,
    val chatStatus: ChatStatus,
    var buildCount: Int,
    val postAction: (Action) -> Unit,
    private val error: ChatStateError? = null,
    val participants: Map<String, RemoteParticipantInfoModel>,
) {
    val showError get() = error != null
    val errorMessage get() = error?.errorCode?.toString() ?: ""
    val isLoading get() = chatStatus != ChatStatus.INITIALIZED && !showError
}

// Internal counter for early debugging
private var buildCount = 0

// Methods to Build the Chat Screen View Model from the Store
internal fun buildChatScreenViewModel(
    store: AppStore<ReduxState>,
    messages: List<MessageInfoModel>,
    localUserIdentifier: String,
): ChatScreenViewModel {

    if (dispatchers == null) {
        dispatchers = Dispatchers(store)
    }
    return ChatScreenViewModel(
        messages = messages.toViewModelList(localUserIdentifier),
        chatStatus = store.getCurrentState().chatState.chatStatus,
        buildCount = buildCount++,
        error = store.getCurrentState().errorState.chatStateError,
        typingParticipants = store.getCurrentState().participantState.participantTyping,
        postAction = dispatchers!!::postAction,
        participants = store.getCurrentState().participantState.participants,
    )
}

internal var dispatchers: Dispatchers? = null

internal class Dispatchers(val store: AppStore<ReduxState>) {
    fun postAction(action: Action) {
        store.dispatch(action)
    }
}
