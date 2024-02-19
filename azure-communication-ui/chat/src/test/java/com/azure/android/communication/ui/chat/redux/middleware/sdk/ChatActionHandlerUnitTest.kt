// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.sdk

import com.azure.android.communication.ui.chat.ACSBaseTestCoroutine
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorCode
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.action.ErrorAction
import com.azure.android.communication.ui.chat.redux.action.NetworkAction
import com.azure.android.communication.ui.chat.redux.state.AppReduxState
import com.azure.android.communication.ui.chat.redux.state.NetworkState
import com.azure.android.communication.ui.chat.redux.state.NetworkStatus
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.service.ChatService
import com.azure.android.communication.ui.chat.service.sdk.ChatSDK
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.SendChatMessageResult
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.threeten.bp.OffsetDateTime

@RunWith(MockitoJUnitRunner::class)
internal class ChatActionHandlerUnitTest : ACSBaseTestCoroutine() {
    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_sendMessage_then_dispatch_ChatActionSentMessage() =
        runScopedTest {
            // arrange
            val messageInfoModel =
                MessageInfoModel(
                    id = null,
                    internalId = "54321",
                    messageType = ChatMessageType.TEXT,
                    content = "hello, world!",
                )

            val returnMessageId = "54321"

            val sendChatMessageResult = SendChatMessageResult(returnMessageId)

            val sendChatMessageCompletableFuture = CompletableFuture<SendChatMessageResult>()

            val mockChatService: ChatService =
                mock {
                    on { sendMessage(messageInfoModel) } doReturn sendChatMessageCompletableFuture
                }

            val chatHandler = ChatActionHandler(mockChatService)

            val action = ChatAction.SendMessage(messageInfoModel)

            val mockAppStore =
                mock<AppStore<ReduxState>> {
                    on { dispatch(any()) } doAnswer { }
                }
            val mockAppState = mock<ReduxState> {}

            // act
            chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)

            sendChatMessageCompletableFuture.complete(sendChatMessageResult)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ChatAction.MessageSent && action.messageInfoModel.normalizedID == returnMessageId.toLong()
                },
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_sendMessage_then_dispatch_ChatStateErrorOccurred() =
        runScopedTest {
            // arrange
            val messageInfoModel =
                MessageInfoModel(
                    id = null,
                    internalId = "54321",
                    messageType = ChatMessageType.TEXT,
                    content = "hello, world!",
                )

            val error = Exception("test")
            val sendChatMessageCompletableFuture = CompletableFuture<SendChatMessageResult>()
            val mockChatService: ChatService =
                mock {
                    on { sendMessage(messageInfoModel) } doReturn sendChatMessageCompletableFuture
                }

            val chatHandler = ChatActionHandler(mockChatService)
            val action = ChatAction.SendMessage(messageInfoModel)
            val mockAppStore =
                mock<AppStore<ReduxState>> {
                    on { dispatch(any()) } doAnswer { }
                }
            val mockAppState = mock<ReduxState> {}

            // act
            chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)
            sendChatMessageCompletableFuture.completeExceptionally(error)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ErrorAction.ChatStateErrorOccurred
                },
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_deleteMessage_then_dispatch_ChatActionDeleteMessage() =
        runScopedTest {
            // arrange
            val messageInfoModel =
                MessageInfoModel(
                    id = null,
                    internalId = "54321",
                    messageType = ChatMessageType.TEXT,
                    content = "hello, world!",
                )

            val deleteChatMessageCompletableFuture = CompletableFuture<Void>()

            val mockChatService: ChatService =
                mock {
                    on { deleteMessage(messageInfoModel.normalizedID.toString()) } doReturn deleteChatMessageCompletableFuture
                }

            val chatHandler = ChatActionHandler(mockChatService)

            val action = ChatAction.DeleteMessage(messageInfoModel)

            val mockAppStore =
                mock<AppStore<ReduxState>> {
                    on { dispatch(any()) } doAnswer { }
                }
            val mockAppState = mock<ReduxState> {}

            // act
            chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)

            deleteChatMessageCompletableFuture.complete(any())

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ChatAction.MessageDeleted
                },
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_deleteMessage_then_dispatch_ChatStateErrorOccurred() =
        runScopedTest {
            // arrange
            val messageInfoModel =
                MessageInfoModel(
                    id = null,
                    internalId = "54321",
                    messageType = ChatMessageType.TEXT,
                    content = "hello, world!",
                )

            val error = Exception("test")
            val deleteChatMessageCompletableFuture = CompletableFuture<Void>()
            val mockChatService: ChatService =
                mock {
                    on { deleteMessage(messageInfoModel.normalizedID.toString()) } doReturn deleteChatMessageCompletableFuture
                }

            val chatHandler = ChatActionHandler(mockChatService)
            val action = ChatAction.DeleteMessage(messageInfoModel)
            val mockAppStore =
                mock<AppStore<ReduxState>> {
                    on { dispatch(any()) } doAnswer { }
                }
            val mockAppState = mock<ReduxState> {}

            // act
            chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)
            deleteChatMessageCompletableFuture.completeExceptionally(error)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ErrorAction.ChatStateErrorOccurred
                },
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_editMessage_then_dispatch_ChatActionEditMessage() =
        runScopedTest {
            // arrange
            val messageInfoModel =
                MessageInfoModel(
                    id = null,
                    internalId = "54321",
                    messageType = ChatMessageType.TEXT,
                    content = "hello, world!",
                )

            val editChatMessageCompletableFuture = CompletableFuture<Void>()

            val mockChatService: ChatService =
                mock {
                    on {
                        editMessage(
                            messageInfoModel.normalizedID.toString(),
                            messageInfoModel.content.toString(),
                        )
                    } doReturn editChatMessageCompletableFuture
                }

            val chatHandler = ChatActionHandler(mockChatService)

            val action = ChatAction.EditMessage(messageInfoModel)

            val mockAppStore =
                mock<AppStore<ReduxState>> {
                    on { dispatch(any()) } doAnswer { }
                }
            val mockAppState = mock<ReduxState> {}

            // act
            chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)

            editChatMessageCompletableFuture.complete(any())

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ChatAction.MessageEdited
                },
            )
        }

    // TODO add back when we release the edit message
//    @ExperimentalCoroutinesApi
//    @Test
//    fun chatMiddlewareActionHandler_editMessage_then_dispatch_ChatStateErrorOccurred() =
//        runScopedTest {
//            // arrange
//            val messageInfoModel = MessageInfoModel(
//                id = null,
//                internalId = "54321",
//                messageType = ChatMessageType.TEXT,
//                content = "hello, world!"
//            )
//
//            val error = Exception("test")
//            val editChatMessageCompletableFuture = CompletableFuture<Void>()
//            val mockChatService: ChatService = mock {
//                on {
//                    editMessage(
//                        messageInfoModel.normalizedID.toString(),
//                        messageInfoModel.content.toString()
//                    )
//                } doReturn editChatMessageCompletableFuture
//            }
//
//            val chatHandler = ChatActionHandler(mockChatService)
//            val action = ChatAction.EditMessage(messageInfoModel)
//            val mockAppStore = mock<AppStore<ReduxState>> {
//                on { dispatch(any()) } doAnswer { }
//            }
//            val mockAppState = mock<ReduxState> {}
//
//            // act
//            chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)
//            editChatMessageCompletableFuture.completeExceptionally(error)
//
//            // assert
//            verify(mockAppStore, times(1)).dispatch(
//                argThat { action ->
//                    action is ErrorAction.ChatStateErrorOccurred
//                }
//            )
//        }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_fetchMessage_then_call_chatServiceGetPreviousPage() =
        runScopedTest {
            // arrange
            val mockChatService: ChatService =
                mock {
                    on { requestPreviousPage() } doAnswer {}
                }

            val chatHandler = ChatActionHandler(mockChatService)

            val action = ChatAction.FetchMessages()

            val mockAppStore = mock<AppStore<ReduxState>> {}
            val mockAppState = mock<ReduxState> {}

            // act
            chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)

            // assert
            verify(mockChatService, times(1)).requestPreviousPage()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_onChatInitialized_then_dispatch_ChatStartEventNotifications() =
        runScopedTest {
            // arrange
            val mockChatSDK = mock<ChatSDK>()
            val chatService = ChatService(mockChatSDK)
            val chatHandler = ChatActionHandler(chatService)
            val mockAppStore = mock<AppStore<ReduxState>>()
            val mockAppState = mock<ReduxState> {}

            // act
            chatHandler.onAction(
                ChatAction.Initialized(),
                mockAppStore::dispatch,
                mockAppState,
            )

            // assert
            verify(mockChatSDK, times(1)).startEventNotifications()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_onChatStartEventNotificationsError_then_dispatch_ChatError() =
        runScopedTest {
            // arrange
            val mockChatSDK = mock<ChatSDK>()
            val chatService = ChatService(mockChatSDK)
            val chatHandler = ChatActionHandler(chatService)
            val mockAppStore =
                mock<AppStore<ReduxState>> {
                    on { dispatch(any()) } doAnswer { }
                }
            whenever(mockChatSDK.startEventNotifications()).then { throw java.lang.RuntimeException() }
            val argumentCaptor = argumentCaptor<ErrorAction.ChatStateErrorOccurred>()
            val mockAppState = mock<ReduxState> {}

            // act
            chatHandler.onAction(
                action = ChatAction.Initialized(),
                dispatch = mockAppStore::dispatch,
                state = mockAppState,
            )

            // assert
            verify(mockAppStore, times(1)).dispatch(argumentCaptor.capture())
            assertEquals(
                argumentCaptor.firstValue.javaClass,
                ErrorAction.ChatStateErrorOccurred::class.java,
            )
            assertEquals(
                argumentCaptor.firstValue.chatCompositeErrorEvent.errorCode,
                ChatCompositeErrorCode.START_EVENT_NOTIFICATIONS_FAILED,
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_onChatInitialized_then_dispatch_ChatRequestParticipants() =
        runScopedTest {
            // arrange
            val mockChatSDK = mock<ChatSDK>()
            val chatService = ChatService(mockChatSDK)
            val chatHandler = ChatActionHandler(chatService)
            val mockAppStore = mock<AppStore<ReduxState>>()
            val mockAppState = mock<ReduxState> {}

            // act
            chatHandler.onAction(
                ChatAction.Initialized(),
                mockAppStore::dispatch,
                mockAppState,
            )

            // assert
            verify(mockChatSDK, times(1)).requestChatParticipants()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_onChatRequestParticipantsError_then_dispatch_ChatError() =
        runScopedTest {
            // arrange
            val mockChatSDK = mock<ChatSDK>()
            val chatService = ChatService(mockChatSDK)
            val chatHandler = ChatActionHandler(chatService)
            val mockAppStore =
                mock<AppStore<ReduxState>> {
                    on { dispatch(any()) } doAnswer { }
                }
            whenever(mockChatSDK.requestChatParticipants()).then { throw java.lang.RuntimeException() }
            val argumentCaptor = argumentCaptor<Action>()
            val mockAppState = mock<ReduxState> {}

            // act
            chatHandler.onAction(
                action = ChatAction.Initialized(),
                dispatch = mockAppStore::dispatch,
                state = mockAppState,
            )

            // assert
            verify(mockAppStore, times(2)).dispatch(argumentCaptor.capture())
            assertEquals(
                argumentCaptor.secondValue.javaClass,
                ErrorAction.ChatStateErrorOccurred::class.java,
            )
            val chatError = argumentCaptor.secondValue as ErrorAction.ChatStateErrorOccurred
            assertEquals(
                chatError.chatCompositeErrorEvent.errorCode,
                ChatCompositeErrorCode.REQUEST_PARTICIPANTS_FETCH_FAILED,
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_onChatRequestNetworkConnectedFromDisconnected_then_fetchMessages() =
        runScopedTest {
            // arrange
            val mockChatSDK = mock<ChatSDK>()
            val chatService = ChatService(mockChatSDK)
            val chatHandler = ChatActionHandler(chatService)
            val mockAppStore = mock<AppStore<ReduxState>> { }
            val appReduxState =
                AppReduxState(
                    threadID = "threadID",
                    localParticipantIdentifier = "identifier",
                    localParticipantDisplayName = "name",
                )

            val offsetTimeStamp = OffsetDateTime.MIN
            appReduxState.networkState = NetworkState(NetworkStatus.DISCONNECTED, offsetTimeStamp)

            // act
            chatHandler.onAction(
                action = NetworkAction.Connected(),
                dispatch = mockAppStore::dispatch,
                state = appReduxState,
            )

            // assert
            verify(mockChatSDK, times(1)).fetchMessages(offsetTimeStamp)
        }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_sendReadReceipt_then_send_ChatActionSendReadReceipt() =
        runScopedTest {
            // arrange
            val messageInfoModel =
                MessageInfoModel(
                    id = null,
                    internalId = "54321",
                    messageType = ChatMessageType.TEXT,
                    content = "hello, world!",
                )

            val testId = "54321"

            val sendReadReceiptCompletableFuture = CompletableFuture<Void>()

            val mockChatService: ChatService =
                mock {
                    on { sendReadReceipt(messageInfoModel.normalizedID.toString()) } doReturn sendReadReceiptCompletableFuture
                }
            val chatHandler = ChatActionHandler(mockChatService)

            val action = ChatAction.MessageRead(messageInfoModel.normalizedID.toString())

            val mockAppStore = mock<AppStore<ReduxState>> { }
            val mockAppState = mock<ReduxState> {}

            // act
            chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)
            sendReadReceiptCompletableFuture.complete(null)

            // assert
            verify(mockChatService, times(1)).sendReadReceipt(testId)
        }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_onChatRequestNetworkConnected_then_doNotFetchMessages() =
        runScopedTest {
            // arrange
            val mockChatSDK = mock<ChatSDK>()
            val chatService = ChatService(mockChatSDK)
            val chatHandler = ChatActionHandler(chatService)
            val mockAppStore = mock<AppStore<ReduxState>> { }
            val appReduxState =
                AppReduxState(
                    threadID = "threadID",
                    localParticipantIdentifier = "identifier",
                    localParticipantDisplayName = "name",
                )

            val offsetTimeStamp = OffsetDateTime.MIN
            appReduxState.networkState = NetworkState(NetworkStatus.CONNECTED, offsetTimeStamp)

            // act
            chatHandler.onAction(
                action = NetworkAction.Connected(),
                dispatch = mockAppStore::dispatch,
                state = appReduxState,
            )

            // assert
            verify(mockChatSDK, times(0)).fetchMessages(offsetTimeStamp)
        }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_sendReadReceipt_then_ChatStateErrorOccurred() =
        runScopedTest {
            // arrange
            val messageInfoModel =
                MessageInfoModel(
                    id = null,
                    internalId = "54321",
                    messageType = ChatMessageType.TEXT,
                    content = "hello, world!",
                )

            val error = Exception("test")

            val sendReadReceiptCompletableFuture = CompletableFuture<Void>()

            val mockChatService: ChatService =
                mock {
                    on { sendReadReceipt(messageInfoModel.normalizedID.toString()) } doReturn sendReadReceiptCompletableFuture
                }

            val chatHandler = ChatActionHandler(mockChatService)

            val action = ChatAction.MessageRead(messageInfoModel.normalizedID.toString())

            val mockAppStore =
                mock<AppStore<ReduxState>> {
                    on { dispatch(any()) } doAnswer { }
                }
            val mockAppState = mock<ReduxState> {}

            // act
            chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)
            sendReadReceiptCompletableFuture.completeExceptionally(error)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ErrorAction.ChatStateErrorOccurred
                },
            )
        }

    @Test
    fun chatMiddlewareActionHandler_on_actionTypingIndicator_then_sendTypingIndicator() {
        // arrange
        val sendTypingIndicatorCompletableFuture = CompletableFuture<Void>()

        val mockChatService: ChatService =
            mock {
                on { sendTypingIndicator() } doReturn sendTypingIndicatorCompletableFuture
            }
        val chatHandler = ChatActionHandler(mockChatService)

        val action = ChatAction.TypingIndicator()

        val mockAppStore = mock<AppStore<ReduxState>> { }
        val mockAppState = mock<ReduxState> {}

        // act
        chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)
        sendTypingIndicatorCompletableFuture.complete(null)

        // assert
        verify(mockChatService, times(1)).sendTypingIndicator()
    }

    @Test
    fun chatMiddlewareActionHandler_sendTypingIndicator_then_ChatStateErrorOccurred() {
        // arrange

        val error = Exception("test")

        val sendTypingIndicatorCompletableFuture = CompletableFuture<Void>()

        val mockChatService: ChatService =
            mock {
                on { sendTypingIndicator() } doReturn sendTypingIndicatorCompletableFuture
            }

        val chatHandler = ChatActionHandler(mockChatService)

        val action = ChatAction.TypingIndicator()

        val mockAppStore =
            mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
            }
        val mockAppState = mock<ReduxState> {}

        // act
        chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)
        sendTypingIndicatorCompletableFuture.completeExceptionally(error)

        // assert
        verify(mockChatService, times(1)).sendTypingIndicator()
    }

    @Test
    fun chatMiddlewareActionHandler_on_actionsTypingIndicator_then_sendTypingIndicatorOnce() {
        // arrange

        val sendTypingIndicatorCompletableFuture = CompletableFuture<Void>()

        val mockChatService: ChatService =
            mock {
                on { sendTypingIndicator() } doReturn sendTypingIndicatorCompletableFuture
            }

        val chatHandler = ChatActionHandler(mockChatService)

        val action = ChatAction.TypingIndicator()

        val mockAppStore = mock<AppStore<ReduxState>> { }
        val mockAppState = mock<ReduxState> {}

        // act
        chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)
        chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)
        sendTypingIndicatorCompletableFuture.complete(null)

        // assert
        verify(mockChatService, times(1)).sendTypingIndicator()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun chatMiddlewareActionHandler_on_actionsTypingIndicatorAfterInterval_then_sendTypingIndicatorTwice() =
        runScopedTest {
            runBlocking {
                // arrange

                val sendTypingIndicatorCompletableFuture = CompletableFuture<Void>()

                val mockChatService: ChatService =
                    mock {
                        on { sendTypingIndicator() } doReturn sendTypingIndicatorCompletableFuture
                    }

                val chatHandler = ChatActionHandler(mockChatService)

                val action = ChatAction.TypingIndicator()

                val mockAppStore = mock<AppStore<ReduxState>> { }
                val mockAppState = mock<ReduxState> {}

                // act
                chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)
                delay(ChatActionHandler.SEND_TYPING_INDICATOR_INTERVAL_MILLIS.toLong())
                chatHandler.onAction(action, mockAppStore::dispatch, mockAppState)
                sendTypingIndicatorCompletableFuture.complete(null)

                // assert
                verify(mockChatService, times(2)).sendTypingIndicator()
            }
        }
}
