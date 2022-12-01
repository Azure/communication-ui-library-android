// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.sdk

import com.azure.android.communication.ui.chat.models.ChatCompositeErrorCode
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.action.ErrorAction
import com.azure.android.communication.ui.chat.redux.action.NetworkAction
import com.azure.android.communication.ui.chat.redux.state.NetworkStatus
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.service.ChatService

// Converts Redux Actions into SDK Calls
// Redux -> Service
internal class ChatActionHandler(private val chatService: ChatService) {

    companion object {
        const val SEND_TYPING_INDICATOR_INTERVAL_MILLIS = 8000
    }

    private var lastTypingIndicatorNotificationSent =
        System.currentTimeMillis() - SEND_TYPING_INDICATOR_INTERVAL_MILLIS

    fun onAction(action: Action, dispatch: Dispatch, state: ReduxState) {
        when (action) {
            is ChatAction.StartChat -> initialization(dispatch = dispatch)
            is ChatAction.Initialized -> onChatInitialized(
                action = action,
                dispatch = dispatch
            )
            is ChatAction.SendMessage -> sendMessage(action = action, dispatch = dispatch)
            is ChatAction.FetchMessages -> fetchMessages()
            is ChatAction.EditMessage -> editMessage(action = action, dispatch = dispatch)
            is ChatAction.DeleteMessage -> deleteMessage(action = action, dispatch = dispatch)
            is ChatAction.MessageRead -> sendReadReceipt(action = action, dispatch = dispatch)
            is ChatAction.TypingIndicator -> sendTypingIndicator(dispatch = dispatch)
            is ChatAction.EndChat -> endChat()
            is NetworkAction.Connected -> {
                // this check will help prevent false fetch messages when library starts
                // as state is updated later, once action go through middlewares
                if (state.networkState.networkStatus == NetworkStatus.DISCONNECTED) {
                    chatService.fetchMessages(from = state.networkState.disconnectOffsetDateTime)
                }
            }
        }
    }

    private fun endChat() {
        chatService.destroy()
    }

    private fun fetchMessages() {
        chatService.requestPreviousPage()
    }

    private fun deleteMessage(action: ChatAction.DeleteMessage, dispatch: Dispatch) {
        chatService.deleteMessage(action.message.normalizedID.toString()).whenComplete { _, error ->
            if (error != null) {
                // TODO: lets use only one action and state to fire error for timing
                // TODO: while working on error stories, we can create separate states for every error
                dispatch(
                    ErrorAction.ChatStateErrorOccurred(
                        chatCompositeErrorEvent = ChatCompositeErrorEvent(
                            ChatCompositeErrorCode.CHAT_SEND_MESSAGE_FAILED
                        )
                    )
                )
            } else {
                dispatch(
                    ChatAction.MessageDeleted(
                        message = action.message
                    )
                )
            }
        }
    }

    private fun sendMessage(action: ChatAction.SendMessage, dispatch: Dispatch) {
        chatService.sendMessage(action.messageInfoModel).whenComplete { result, error ->
            if (error != null) {
                // TODO: lets use only one action and state to fire error for timing
                // TODO: while working on error stories, we can create separate states for every error
                dispatch(
                    ErrorAction.ChatStateErrorOccurred(
                        chatCompositeErrorEvent = ChatCompositeErrorEvent(
                            ChatCompositeErrorCode.CHAT_SEND_MESSAGE_FAILED
                        )
                    )
                )
            } else {
                dispatch(
                    ChatAction.MessageSent(
                        messageInfoModel = action.messageInfoModel,
                        id = result.id,
                    )
                )
            }
        }
    }

    private fun editMessage(action: ChatAction.EditMessage, dispatch: Dispatch) {
        chatService.editMessage(action.message.normalizedID.toString(), action.message.content ?: "")
            .whenComplete { _, error ->
                if (error != null) {
                    dispatch(
                        ErrorAction.ChatStateErrorOccurred(
                            chatCompositeErrorEvent = ChatCompositeErrorEvent(
                                ChatCompositeErrorCode.CHAT_SEND_EDIT_MESSAGE_FAILED
                            )
                        )
                    )
                } else {
                    dispatch(ChatAction.MessageEdited(action.message))
                }
            }
    }

    private fun sendReadReceipt(action: ChatAction.MessageRead, dispatch: Dispatch) {
        chatService.sendReadReceipt(action.messageId).whenComplete { _, error ->
            if (error != null) {
                // TODO: lets use only one action and state to fire error for timing
                // TODO: while working on error stories, we can create separate states for every error
                dispatch(
                    ErrorAction.ChatStateErrorOccurred(
                        chatCompositeErrorEvent = ChatCompositeErrorEvent(
                            ChatCompositeErrorCode.CHAT_SEND_READ_RECEIPT_FAILED
                        )
                    )
                )
            }
        }
    }

    private fun sendTypingIndicator(dispatch: Dispatch) {
        if (System.currentTimeMillis() - lastTypingIndicatorNotificationSent
            < SEND_TYPING_INDICATOR_INTERVAL_MILLIS
        ) {
            return
        }
        lastTypingIndicatorNotificationSent = System.currentTimeMillis()
        chatService.sendTypingIndicator().whenComplete { _, error ->
            if (error != null) {
                // TODO: lets use only one action and state to fire error for timing
                // TODO: while working on error stories, we can create separate states for every error
                dispatch(
                    ErrorAction.ChatStateErrorOccurred(
                        chatCompositeErrorEvent = ChatCompositeErrorEvent(
                            ChatCompositeErrorCode.CHAT_SEND_TYPING_INDICATOR_FAILED
                        )
                    )
                )
            }
        }
    }

    private fun initialization(dispatch: Dispatch) {
        try {
            chatService.initialize()
        } catch (ex: Exception) {
            val error = ChatCompositeErrorEvent(ChatCompositeErrorCode.CHAT_JOIN_FAILED, ex)
            dispatch(ErrorAction.ChatStateErrorOccurred(chatCompositeErrorEvent = error))
        }
    }

    private fun onChatInitialized(action: ChatAction, dispatch: Dispatch) {
        try {
            chatService.startEventNotifications()
            dispatch.invoke(ChatAction.FetchMessages())
        } catch (ex: Exception) {
            val error = ChatCompositeErrorEvent(ChatCompositeErrorCode.CHAT_START_EVENT_NOTIFICATIONS_FAILED)
            dispatch(ErrorAction.ChatStateErrorOccurred(chatCompositeErrorEvent = error))
        }
        try {
            chatService.requestChatParticipants()
        } catch (ex: Exception) {
            val error = ChatCompositeErrorEvent(ChatCompositeErrorCode.CHAT_REQUEST_PARTICIPANTS_FETCH_FAILED)
            dispatch(ErrorAction.ChatStateErrorOccurred(chatCompositeErrorEvent = error))
        }
    }
}
