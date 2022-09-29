package com.azure.android.communication.ui.chat.redux.middleware

import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class ChatMiddlewareUnitTest {

    @Test
    fun chatMiddleware_invoke_when_invokedWithAnyAction_then_invokeNext() {
        // arrange
        val actionToDispatch = ChatAction.StartChat()
        var nextReceivedAction: ChatAction? = null

        val mockChatMiddlewareActionHandler = mock<ChatActionHandler> {}
        val mockChatServiceListener = mock<ChatServiceListener> {}

        val chatMiddlewareImplementation =
            ChatMiddlewareImpl(
                mockChatServiceListener,
                mockChatMiddlewareActionHandler
            )

        val mockAppStore = mock<AppStore<ReduxState>> {}

        // act
        chatMiddlewareImplementation.invoke(mockAppStore)(
            fun(action) {
                nextReceivedAction = action as ChatAction
            }
        )(actionToDispatch)

        // assert
        Assert.assertEquals(
            actionToDispatch,
            nextReceivedAction
        )
    }

    @Test
    fun chatMiddleware_invoke_when_invokedWithStartChat_then_invokeStartChat() {
        // arrange
        val actionToDispatch = ChatAction.StartChat()

        val mockChatMiddlewareActionHandler = mock<ChatActionHandler>()
        val mockChatServiceListener = mock<ChatServiceListener> {}

        val chatMiddlewareImplementation =
            ChatMiddlewareImpl(
                mockChatServiceListener,
                mockChatMiddlewareActionHandler
            )

        val mockAppStore = mock<AppStore<ReduxState>> {}

        // act
        chatMiddlewareImplementation.invoke(mockAppStore)(
            fun(action) {
                mockChatMiddlewareActionHandler.onAction(action, mockAppStore::dispatch)
            }
        )(actionToDispatch)

        // assert
        verify(mockChatMiddlewareActionHandler, times(1)).onAction(actionToDispatch, mockAppStore::dispatch)
    }
}
