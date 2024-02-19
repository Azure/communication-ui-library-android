// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.viewmodel

import android.content.Context
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorCode
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent
import com.azure.android.communication.ui.chat.models.MessageContextMenuModel
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.redux.state.NavigationStatus
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.utilities.findMessageIdxById
import org.threeten.bp.OffsetDateTime

// Show Debug Information on the screen
internal const val includeDebugInfo = false

// View Model for the Chat Screen
internal data class ChatScreenViewModel(
    val typingParticipants: List<String>,
    val messages: List<MessageViewModel>,
    val areMessagesLoading: Boolean,
    val chatStatus: ChatStatus,
    var buildCount: Int,
    var unreadMessagesCount: Int = 0,
    val postAction: (Action) -> Unit,
    private val error: ChatCompositeErrorEvent? = null,
    val participants: Map<String, RemoteParticipantInfoModel>,
    val chatTopic: String? = null,
    val navigationStatus: NavigationStatus = NavigationStatus.NONE,
    val messageContextMenu: MessageContextMenuModel,
    val sendMessageEnabled: Boolean = false,
    val debugOverlayText: String = "Test",
) {
    val showError get() = error != null && error.errorCode == ChatCompositeErrorCode.JOIN_FAILED
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
    val latestLocalUserMessageId = messages.findLast { it.isCurrentUser }?.normalizedID
    val lastMessageIdReadByRemoteParticipants =
        getLastMessageIdReadByRemoteParticipants(
            messages,
            store.getCurrentState().participantState.latestReadMessageTimestamp,
        )

    return ChatScreenViewModel(
        messages =
            messages.toViewModelList(
                context,
                localUserIdentifier,
                latestLocalUserMessageId,
                lastMessageIdReadByRemoteParticipants,
                store.getCurrentState().participantState.hiddenParticipant,
                includeDebugInfo = includeDebugInfo,
            ),
        areMessagesLoading = !store.getCurrentState().chatState.chatInfoModel.allMessagesFetched,
        chatStatus = store.getCurrentState().chatState.chatStatus,
        buildCount = buildCount++,
        unreadMessagesCount = getUnReadMessagesCount(store, messages),
        error = store.getCurrentState().errorState.chatCompositeErrorEvent,
        postAction = dispatch,
        typingParticipants = store.getCurrentState().participantState.participantTyping.values.toList(),
        participants = store.getCurrentState().participantState.participants,
        chatTopic = store.getCurrentState().chatState.chatInfoModel.topic,
        navigationStatus = store.getCurrentState().navigationState.navigationStatus,
        messageContextMenu = store.getCurrentState().chatState.messageContextMenu,
        sendMessageEnabled =
            store.getCurrentState().participantState.localParticipantInfoModel.isActiveChatThreadParticipant &&
                store.getCurrentState().chatState.chatStatus == ChatStatus.INITIALIZED,
        debugOverlayText = getDebugOverlayText(store, messages),
    )
}

internal fun getDebugOverlayText(
    store: AppStore<ReduxState>,
    messages: List<MessageInfoModel>,
): String {
    if (!includeDebugInfo) return ""
    return "Last Read ID: ${store.getCurrentState().chatState.lastReadMessageId}\n" +
        "Last Received Message: ${ if (messages.isEmpty()) "None" else messages.last().normalizedID }"
}

private fun getLastMessageIdReadByRemoteParticipants(
    messages: List<MessageInfoModel>,
    latestReadMessageTimestamp: OffsetDateTime,
): Long {
    messages.asReversed().forEach {
        if ((it.messageType == ChatMessageType.TEXT || it.messageType == ChatMessageType.HTML) &&
            it.isCurrentUser
        ) {
            val currentMessageTime = it.editedOn ?: it.createdOn
            if (currentMessageTime != null && currentMessageTime <= latestReadMessageTimestamp) {
                return it.normalizedID
            }
        }
    }
    return 0
}

private fun getUnReadMessagesCount(
    store: AppStore<ReduxState>,
    messages: List<MessageInfoModel>,
): Int {
    val lastReadId = store.getCurrentState().chatState.lastReadMessageId

    if (lastReadId.isEmpty()) {
        return 0
    }
    var internalLastReadIndex = messages.findMessageIdxById(lastReadId.toLong())
    var selfCount = 0
    while (internalLastReadIndex >= 0 && messages[internalLastReadIndex].isCurrentUser) {
        internalLastReadIndex--
        selfCount++
    }
    return if (internalLastReadIndex == -1) 0 else messages.size - internalLastReadIndex - 1 - selfCount
}
