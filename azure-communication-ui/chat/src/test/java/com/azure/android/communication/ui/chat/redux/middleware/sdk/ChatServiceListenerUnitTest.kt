// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.sdk

import com.azure.android.communication.chat.models.ChatEventType
import com.azure.android.communication.ui.chat.ACSBaseTestCoroutine
import com.azure.android.communication.ui.chat.mocking.UnconfinedTestContextProvider
import com.azure.android.communication.ui.chat.models.ChatEventModel
import com.azure.android.communication.ui.chat.models.ChatThreadInfoModel
import com.azure.android.communication.ui.chat.models.MessagesPageModel
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.service.ChatService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.threeten.bp.OffsetDateTime

@RunWith(MockitoJUnitRunner::class)
class ChatServiceListenerUnitTest : ACSBaseTestCoroutine() {

    @Test
    fun chatServiceListener_subscribe_then_dispatch_chatStatusStateChange() {
        // arrange
        val chatStatusStateFlow = MutableStateFlow(ChatStatus.INITIALIZED)
        val messagesSharedFlow: MutableSharedFlow<MessagesPageModel> = MutableSharedFlow()

        val mockChatService: ChatService = mock {
            on { getChatStatusStateFlow() } doReturn chatStatusStateFlow
            on { getMessagesPageSharedFlow() } doReturn messagesSharedFlow
        }

        val handler = ChatServiceListener(mockChatService, UnconfinedTestContextProvider())

        val mockAppStore = mock<AppStore<ReduxState>> {}

        // act
        handler.subscribe(mockAppStore::dispatch)

        // assert
        verify(
            mockAppStore,
            times(1)
        ).dispatch(argThat { action -> action is ChatAction.Initialized })
    }

    @ExperimentalCoroutinesApi
    @Test
    fun chatServiceListener_subscribe_then_dispatch_ChatThreadUpdated() {
        runScopedTest {
            // arrange
            val chatStatusStateFlow = MutableStateFlow(ChatStatus.NONE)

            val chatEventSharedFlow: MutableSharedFlow<ChatEventModel> = MutableSharedFlow()
            val messagesSharedFlow: MutableSharedFlow<MessagesPageModel> = MutableSharedFlow()

            val mockChatService: ChatService = mock {
                on { getChatStatusStateFlow() } doReturn chatStatusStateFlow
                on { getChatEventSharedFlow() } doReturn chatEventSharedFlow
                on { getMessagesPageSharedFlow() } doReturn messagesSharedFlow
            }

            chatEventSharedFlow.emit(
                ChatEventModel(
                    com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED,
                    ChatThreadInfoModel("Topic", OffsetDateTime.MIN)
                )
            )

            val handler = ChatServiceListener(mockChatService, UnconfinedTestContextProvider())

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
            }
            // act
            handler.subscribe(mockAppStore::dispatch)

            // assert
            verify(
                mockAppStore,
                times(1)
            ).dispatch(argThat { action -> action is ChatAction.TopicUpdated })
        }
    }
}
