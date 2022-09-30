package com.azure.android.communication.ui.chat.redux.middleware.handler

import com.azure.android.communication.ui.chat.ACSBaseTestCoroutine
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.action.ErrorAction
import com.azure.android.communication.ui.chat.redux.middleware.ChatActionHandler
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.service.ChatService
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.SendChatMessageResult
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class ChatMiddlewareActionHandlerUnitTest : ACSBaseTestCoroutine() {

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_sendMessage_then_dispatch_ChatActionSentMessage() =
        runScopedTest {
            // arrange
            val messageInfoModel = MessageInfoModel(
                id = null,
                internalId = "54321",
                messageType = ChatMessageType.TEXT,
                content = "hello, world!"
            )

            val returnMessageId = "test"

            val sendChatMessageResult = SendChatMessageResult(returnMessageId)

            val sendChatMessageCompletableFuture = CompletableFuture<SendChatMessageResult>()

            val mockChatService: ChatService = mock {
                on { sendMessage(messageInfoModel) } doReturn sendChatMessageCompletableFuture
            }

            val chatHandler = ChatActionHandler(mockChatService)

            val action = ChatAction.SendMessage(messageInfoModel)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
            }

            // act
            chatHandler.onAction(action, mockAppStore::dispatch)

            sendChatMessageCompletableFuture.complete(sendChatMessageResult)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ChatAction.MessageSent && action.messageInfoModel.id == returnMessageId
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_sendMessage_then_dispatch_ChatStateErrorOccurred() =
        runScopedTest {
            // arrange
            val messageInfoModel = MessageInfoModel(
                id = null,
                internalId = "54321",
                messageType = ChatMessageType.TEXT,
                content = "hello, world!"
            )

            val error = Exception("test")

            val sendChatMessageCompletableFuture = CompletableFuture<SendChatMessageResult>()

            val mockChatService: ChatService = mock {
                on { sendMessage(messageInfoModel) } doReturn sendChatMessageCompletableFuture
            }

            val chatHandler = ChatActionHandler(mockChatService)

            val action = ChatAction.SendMessage(messageInfoModel)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
            }

            // act
            chatHandler.onAction(action, mockAppStore::dispatch)

            sendChatMessageCompletableFuture.completeExceptionally(error)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ErrorAction.ChatStateErrorOccurred
                }
            )
        }
}
