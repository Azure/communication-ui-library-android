package com.azure.android.communication.ui.chat.presentation.ui.view_model

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.action.ChatAction

import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.repository.MessageRepository
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType

// View Model for the Chat Screen
internal data class ChatScreenViewModel(
    val messages: List<MessageViewModel>,
    val state: String,
    var buildCount: Int,
    val postMessage: (String) -> Unit
)

// Internal counter for early debugging
private var buildCount = 0

// Methods to Build the Chat Screen View Model from the Store
internal fun buildChatScreenViewModel(
    store: AppStore<ReduxState>,
    repository: MessageRepository
) =
    ChatScreenViewModel(
        messages = repository.toViewModelList(),
        state = store.getCurrentState().chatState.chatStatus.name,
        buildCount = buildCount++,
        postMessage = {
            store.dispatch(
                ChatAction.SendMessage(
                    MessageInfoModel(
                        id = null,
                        messageType = ChatMessageType.TEXT,
                        internalId = null,
                        content = it
                    )
                )
            )
        }
    )
