// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.viewmodel

import android.content.Context
import com.azure.android.communication.ui.chat.error.ChatStateError
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.redux.state.NavigationStatus
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import kotlin.math.max

// View Model for the Chat Screen
internal data class ChatScreenViewModel(
    val typingParticipants: List<String>,
    val messages: List<MessageViewModel>,
    val areMessagesLoading: Boolean,
    val chatStatus: ChatStatus,
    var buildCount: Int,
    var unreadMessagesCount: Int = 0,
    val postAction: (Action) -> Unit,
    private val error: ChatStateError? = null,
    val participants: Map<String, RemoteParticipantInfoModel>,
    val chatTopic: String? = null,
    val navigationStatus: NavigationStatus = NavigationStatus.NONE,
) {
    val showError get() = error != null
    val errorMessage get() = error?.errorCode?.toString() ?: ""
    val isLoading get() = chatStatus != ChatStatus.INITIALIZED && !showError
    val unreadMessagesIndicatorVisibility = unreadMessagesCount > 0
}

// Internal counter for early debugging
private var buildCount = 0

// Methods to Build the Chat Screen View Model from the Store
internal fun buildChatScreenViewModel(
    context: Context,
    store: AppStore<ReduxState>,
    messages: List<MessageInfoModel>,
    localUserIdentifier: String,
    dispatch: Dispatch,
): ChatScreenViewModel {

    return ChatScreenViewModel(
        messages = messages.toViewModelList(context, localUserIdentifier),
        areMessagesLoading = !store.getCurrentState().chatState.chatInfoModel.allMessagesFetched,
        chatStatus = store.getCurrentState().chatState.chatStatus,
        buildCount = buildCount++,
        unreadMessagesCount = getUnReadMessagesCount(store, messages),
        error = store.getCurrentState().errorState.chatStateError,
        postAction = dispatch,
        typingParticipants = store.getCurrentState().participantState.participantTyping.values.toList(),
        participants = store.getCurrentState().participantState.participants,
        chatTopic = store.getCurrentState().chatState.chatInfoModel.topic,
        navigationStatus = store.getCurrentState().navigationState.navigationStatus,
    )
}

private fun getUnReadMessagesCount(
    store: AppStore<ReduxState>,
    messages: List<MessageInfoModel>,
): Int {
    val lastReadId = store.getCurrentState().chatState.lastReadMessageId
    val lastSendId = store.getCurrentState().chatState.lastSendMessageId

    val internalLastReadIndex = messages.indexOf(MessageInfoModel(id = lastReadId))
    val internalLastSendIndex = messages.indexOf(MessageInfoModel(id = lastSendId))

    val internalLastReadId = max(internalLastReadIndex, internalLastSendIndex)

    return if (internalLastReadId == -1) 0 else messages.size - internalLastReadId - 1
}
