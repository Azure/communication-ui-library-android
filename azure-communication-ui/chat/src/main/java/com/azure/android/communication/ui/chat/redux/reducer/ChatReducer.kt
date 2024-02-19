// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.reducer

import android.content.ClipData
import android.content.ClipboardManager
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MenuItemModel
import com.azure.android.communication.ui.chat.models.MessageContextMenuModel
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.state.ChatState
import com.azure.android.communication.ui.chat.redux.state.ChatStatus

internal interface ChatReducer : Reducer<ChatState>

internal class ChatReducerImpl : ChatReducer {
    override fun reduce(
        state: ChatState,
        action: Action,
    ): ChatState {
        return when (action) {
            is ChatAction.Initialization -> {
                state.copy(chatStatus = ChatStatus.INITIALIZATION)
            }
            is ChatAction.Initialized -> {
                state.copy(chatStatus = ChatStatus.INITIALIZED)
            }
            is ChatAction.TopicUpdated -> {
                state.copy(chatInfoModel = state.chatInfoModel.copy(topic = action.topic))
            }
            is ChatAction.AllMessagesFetched -> {
                state.copy(chatInfoModel = state.chatInfoModel.copy(allMessagesFetched = true))
            }
            is ChatAction.ThreadDeleted -> {
                state.copy(chatInfoModel = state.chatInfoModel.copy(isThreadDeleted = true))
            }
            is ChatAction.MessageLastReceived -> {
                state.copy(
                    lastReadMessageId =
                        if (state.lastReadMessageId > action.messageId) {
                            state.lastReadMessageId
                        } else {
                            action.messageId
                        },
                )
            }
            is ChatAction.MessageRead -> {
                state.copy(
                    lastReadMessageId =
                        if (state.lastReadMessageId > action.messageId) {
                            state.lastReadMessageId
                        } else {
                            action.messageId
                        },
                )
            }
            is ChatAction.ShowMessageContextMenu -> {
                state.copy(
                    messageContextMenu =
                        MessageContextMenuModel(
                            messageInfoModel = action.message,
                            menuItems =
                                listOf(
                                    MenuItemModel(
                                        R.string.azure_communication_ui_chat_copy,
                                        R.drawable.azure_communication_ui_chat_ic_fluent_copy_20_regular,
                                        onClickAction = { context ->
                                            val clipboardManager =
                                                context.getSystemService(ClipboardManager::class.java)
                                            clipboardManager.setPrimaryClip(
                                                ClipData.newPlainText(
                                                    context.getString(
                                                        R.string.azure_communication_ui_chat_message,
                                                    ),
                                                    action.message.content,
                                                ),
                                            )
                                        },
                                    ),
                                ),
                        ),
                )
            }
            is ChatAction.HideMessageContextMenu -> {
                state.copy(
                    messageContextMenu =
                        MessageContextMenuModel(
                            messageInfoModel = EMPTY_MESSAGE_INFO_MODEL,
                            menuItems = emptyList(),
                        ),
                )
            }
            else -> state
        }
    }
}
