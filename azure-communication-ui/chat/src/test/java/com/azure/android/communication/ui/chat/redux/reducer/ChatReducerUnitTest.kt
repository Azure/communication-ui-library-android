// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.reducer

import com.azure.android.communication.ui.chat.models.ChatInfoModel
import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageContextMenuModel
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.state.ChatState
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class ChatReducerUnitTest {
    @Test
    fun chatReducer_reduce_when_actionInitialization_then_changeChatStateInitialization() {
        // arrange
        val reducer = ChatReducerImpl()
        val chatInfoModel = mock<ChatInfoModel>()
        val previousState =
            ChatState(
                ChatStatus.NONE,
                chatInfoModel,
                "",
                messageContextMenu =
                    MessageContextMenuModel(
                        EMPTY_MESSAGE_INFO_MODEL,
                        emptyList(),
                    ),
            )
        val action = ChatAction.Initialization()

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(ChatStatus.INITIALIZATION, newState.chatStatus)
    }

    @Test
    fun chatReducer_reduce_when_actionInitialized_then_changeChatStateInitialized() {
        // arrange
        val reducer = ChatReducerImpl()
        val chatInfoModel = mock<ChatInfoModel>()
        val previousState =
            ChatState(
                ChatStatus.NONE,
                chatInfoModel,
                "",
                messageContextMenu =
                    MessageContextMenuModel(
                        EMPTY_MESSAGE_INFO_MODEL,
                        emptyList(),
                    ),
            )
        val action = ChatAction.Initialized()

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(ChatStatus.INITIALIZED, newState.chatStatus)
    }

    @Test
    fun chatReducer_reduce_when_actionTopicUpdated_then_updateChatStateChatInfoTopic() {
        // arrange
        val reducer = ChatReducerImpl()
        val chatInfoModel = ChatInfoModel(threadId = "", topic = "Previous Chat topic")
        val previousState =
            ChatState(
                ChatStatus.NONE,
                chatInfoModel,
                "",
                messageContextMenu =
                    MessageContextMenuModel(
                        EMPTY_MESSAGE_INFO_MODEL,
                        emptyList(),
                    ),
            )
        val action = ChatAction.TopicUpdated("New Chat topic")
        val afterChatInfoModel = ChatInfoModel(threadId = "", topic = "New Chat topic")
        val afterState =
            ChatState(
                ChatStatus.NONE,
                afterChatInfoModel,
                "",
                messageContextMenu =
                    MessageContextMenuModel(
                        EMPTY_MESSAGE_INFO_MODEL,
                        emptyList(),
                    ),
            )

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(afterState, newState)
    }

    @Test
    fun chatReducer_reduce_when_actionAllMessagesFetched_then_updateChatStateChatInfoAllMessagesFetched() {
        // arrange
        val reducer = ChatReducerImpl()
        val chatInfoModel = ChatInfoModel(threadId = "", topic = "", allMessagesFetched = false)
        val previousState =
            ChatState(
                ChatStatus.NONE,
                chatInfoModel,
                "",
                messageContextMenu =
                    MessageContextMenuModel(
                        EMPTY_MESSAGE_INFO_MODEL,
                        emptyList(),
                    ),
            )
        val action = ChatAction.AllMessagesFetched()
        val afterChatInfoModel = ChatInfoModel(threadId = "", topic = "", allMessagesFetched = true)
        val afterState =
            ChatState(
                ChatStatus.NONE,
                afterChatInfoModel,
                "",
                messageContextMenu =
                    MessageContextMenuModel(
                        EMPTY_MESSAGE_INFO_MODEL,
                        emptyList(),
                    ),
            )

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(afterState, newState)
    }

    @Test
    fun chatReducer_reduce_when_actionThreadDeleted_then_updateChatStateChatThreadDeleted() {
        // arrange
        val reducer = ChatReducerImpl()

        val chatInfoModel =
            ChatInfoModel(
                threadId = "",
                topic = "",
                allMessagesFetched = false,
                isThreadDeleted = false,
            )
        val previousState =
            ChatState(
                ChatStatus.NONE,
                chatInfoModel,
                "",
                messageContextMenu =
                    MessageContextMenuModel(
                        EMPTY_MESSAGE_INFO_MODEL,
                        emptyList(),
                    ),
            )

        val action = ChatAction.ThreadDeleted()

        val afterChatInfoModel =
            ChatInfoModel(
                threadId = "",
                topic = "",
                allMessagesFetched = false,
                isThreadDeleted = true,
            )
        val afterState =
            ChatState(
                ChatStatus.NONE,
                afterChatInfoModel,
                "",
                messageContextMenu =
                    MessageContextMenuModel(
                        EMPTY_MESSAGE_INFO_MODEL,
                        emptyList(),
                    ),
            )

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(afterState, newState)
    }
}
