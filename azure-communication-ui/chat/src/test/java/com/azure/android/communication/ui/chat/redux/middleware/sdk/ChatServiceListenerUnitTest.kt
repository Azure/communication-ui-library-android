// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.sdk

import com.azure.android.communication.ui.chat.ACSBaseTestCoroutine
import com.azure.android.communication.ui.chat.mocking.UnconfinedTestContextProvider
import com.azure.android.communication.ui.chat.models.ChatEventModel
import com.azure.android.communication.ui.chat.models.ChatThreadInfoModel
import com.azure.android.communication.ui.chat.models.LocalParticipantInfoModel
import com.azure.android.communication.ui.chat.models.MessagesPageModel
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.state.AppReduxState
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.service.ChatService
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatEventType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.threeten.bp.OffsetDateTime

@RunWith(MockitoJUnitRunner::class)
class ChatServiceListenerUnitTest : ACSBaseTestCoroutine() {
    private val userLocal =
        LocalParticipantInfoModel(
            userIdentifier = "85FF2697-2ABB-480E-ACCA-09EBE3D6F5EC",
            displayName = "Local",
        )

    @Test
    fun chatServiceListener_subscribe_then_dispatch_chatStatusStateChange() {
        // arrange
        val chatStatusStateFlow = MutableStateFlow(ChatStatus.INITIALIZED)
        val messagesSharedFlow: MutableSharedFlow<MessagesPageModel> = MutableSharedFlow()

        val mockChatService: ChatService =
            mock {
                on { getChatStatusStateFlow() } doReturn chatStatusStateFlow
                on { getMessagesPageSharedFlow() } doReturn messagesSharedFlow
            }

        val handler = ChatServiceListener(mockChatService, UnconfinedTestContextProvider())

        val mockAppStore = mock<AppStore<ReduxState>> {}

        // act
        handler.subscribe(mockAppStore)

        // assert
        verify(
            mockAppStore,
            times(1),
        ).dispatch(argThat { action -> action is ChatAction.Initialized })
    }

    @ExperimentalCoroutinesApi
    @Test
    fun chatServiceListener_subscribe_then_dispatch_ChatThreadUpdated() {
        runScopedTest {
            // arrange
            val chatEventSharedFlow: MutableSharedFlow<ChatEventModel> = MutableSharedFlow()
            val initialState: AppReduxState =
                AppReduxState(
                    threadID = "abc:123",
                    localParticipantDisplayName = "you",
                    localParticipantIdentifier = "123",
                )

            val mockChatService: ChatService =
                mock {
                    on { getChatEventSharedFlow() } doReturn chatEventSharedFlow
                }

            val handler = ChatServiceListener(mockChatService, UnconfinedTestContextProvider())

            val mockAppStore =
                mock<AppStore<ReduxState>> {
                    on { dispatch(any()) } doAnswer { }
                    on { getCurrentState() } doAnswer { MutableStateFlow(initialState).value }
                }
            mockAppStore.getCurrentState().participantState.localParticipantInfoModel
            // act
            handler.subscribe(mockAppStore)

            chatEventSharedFlow.emit(
                ChatEventModel(
                    ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED,
                    ChatThreadInfoModel("Topic", OffsetDateTime.MIN),
                ),
            )

            // assert
            verify(
                mockAppStore,
                times(1),
            ).dispatch(argThat { action -> action is ChatAction.TopicUpdated && action.topic == "Topic" })
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun chatServiceListener_subscribe_then_dispatch_chatThreadUpdated_on_threadDeleted() {
        runScopedTest {
            // arrange
            val chatEventSharedFlow: MutableSharedFlow<ChatEventModel> = MutableSharedFlow()
            val initialState: AppReduxState =
                AppReduxState(
                    threadID = "abc:123",
                    localParticipantDisplayName = "you",
                    localParticipantIdentifier = "123",
                )

            val mockChatService: ChatService =
                mock {
                    on { getChatEventSharedFlow() } doReturn chatEventSharedFlow
                }

            val handler = ChatServiceListener(mockChatService, UnconfinedTestContextProvider())
            val mockAppStore =
                mock<AppStore<ReduxState>> {
                    on { getCurrentState() } doAnswer { MutableStateFlow(initialState).value }
                }

            // act
            handler.subscribe(mockAppStore)

            chatEventSharedFlow.emit(
                ChatEventModel(
                    ChatEventType.CHAT_THREAD_DELETED,
                    ChatThreadInfoModel("Topic", OffsetDateTime.MIN),
                ),
            )

            // assert
            verify(
                mockAppStore,
                times(1),
            ).dispatch(argThat { action -> action is ChatAction.ThreadDeleted })
        }
    }
}
