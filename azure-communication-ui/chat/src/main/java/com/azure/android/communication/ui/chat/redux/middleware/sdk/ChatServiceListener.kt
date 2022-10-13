// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.sdk

import com.azure.android.communication.ui.chat.error.ChatStateError
import com.azure.android.communication.ui.chat.error.ErrorCode
import com.azure.android.communication.ui.chat.models.ChatEventModel
import com.azure.android.communication.ui.chat.models.ChatThreadInfoModel
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.models.MessagesPageModel
import com.azure.android.communication.ui.chat.models.ParticipantTimestampInfoModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantsInfoModel
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.action.ErrorAction
import com.azure.android.communication.ui.chat.redux.action.ParticipantAction
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.service.ChatService
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatEventType
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

// Listens to Events from SDK and Dispatches actions
// Service -> Redux
internal class ChatServiceListener(
    private val chatService: ChatService,
    coroutineContextProvider: CoroutineContextProvider,
) {
    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    fun subscribe(dispatch: Dispatch) {
        coroutineScope.launch {
            chatService.getChatStatusStateFlow()?.collect {
                when (it) {
                    ChatStatus.INITIALIZATION -> dispatch(ChatAction.Initialization())
                    ChatStatus.INITIALIZED -> dispatch(ChatAction.Initialized())
                    else -> {}
                }
            }
        }

        coroutineScope.launch {
            chatService.getMessagesPageSharedFlow()?.collect {
                onMessagesPageModelReceived(messagesPageModel = it, dispatch = dispatch)
            }
        }

        coroutineScope.launch {
            chatService.getChatEventSharedFlow()?.collect {
                handleInfoModel(it, dispatch)
            }
        }
    }

    fun unsubscribe() {
        chatService.stopEventNotifications()
        coroutineScope.cancel()
    }

    private fun onMessagesPageModelReceived(
        messagesPageModel: MessagesPageModel,
        dispatch: Dispatch
    ) {

        messagesPageModel.throwable?.let {
            val error = ChatStateError(errorCode = ErrorCode.CHAT_FETCH_MESSAGES_FAILED)
            // TODO: lets use only one action and state to fire error for timing
            // TODO: while working on error stories, we can create separate states for every error
            dispatch(ErrorAction.ChatStateErrorOccurred(chatStateError = error))
        }

        messagesPageModel.messages?.let {
            it.filter { it != null }
            dispatch(ChatAction.MessagesPageReceived(messages = it))
        }

        if (messagesPageModel.allPagesFetched) {
            dispatch(ChatAction.AllMessagesFetched())
        }
    }

    private fun handleInfoModel(it: ChatEventModel, dispatch: Dispatch) {
        when (it.infoModel) {
            is MessageInfoModel -> {
                when (it.eventType) {
                    ChatEventType.CHAT_MESSAGE_RECEIVED -> {
                        dispatch(ChatAction.MessageReceived(message = it.infoModel))
                    }
                    ChatEventType.CHAT_MESSAGE_EDITED -> {
                        dispatch(ChatAction.MessageEdited(message = it.infoModel))
                    }
                    ChatEventType.CHAT_MESSAGE_DELETED -> {
                        dispatch(ChatAction.MessageDeleted(message = it.infoModel))
                    }
                    else -> { }
                }
            }
            is ParticipantTimestampInfoModel -> {
                when (it.eventType) {
                    ChatEventType.TYPING_INDICATOR_RECEIVED -> {
                        dispatch(ParticipantAction.TypingIndicatorReceived(message = it.infoModel))
                    }
                    ChatEventType.READ_RECEIPT_RECEIVED -> {
                        val model = it.infoModel
                    }
                    else -> {}
                }
            }
            is ChatThreadInfoModel -> {
                when (it.eventType) {
                    ChatEventType.CHAT_THREAD_DELETED -> {
                        dispatch(ChatAction.ThreadDeleted())
                    }
                    ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED -> {
                        it.infoModel.topic?.let {
                            dispatch(ChatAction.TopicUpdated(it))
                        }
                    }
                    else -> {}
                }
            }
            is RemoteParticipantsInfoModel -> {
                when (it.eventType) {
                    ChatEventType.PARTICIPANTS_ADDED -> {
                        dispatch(ParticipantAction.ParticipantsAdded(participants = it.infoModel.participants))
                    }
                    ChatEventType.PARTICIPANTS_REMOVED -> {
                        dispatch(ParticipantAction.ParticipantsRemoved(participants = it.infoModel.participants))
                    }
                    else -> {}
                }
            }
        }
    }
}
