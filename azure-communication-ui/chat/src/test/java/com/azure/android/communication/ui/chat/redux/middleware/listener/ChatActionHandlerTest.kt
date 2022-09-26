package com.azure.android.communication.ui.chat.redux.middleware.listener

import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.service.ChatService
import com.azure.android.communication.ui.chat.service.sdk.ChatSDK
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.SendChatMessageResult
import java9.util.concurrent.CompletableFuture
import junit.framework.Assert.assertEquals
import org.junit.Test


internal class ChatActionHandlerTest {

    private val dispatch : Dispatch = {}

    @Test
    fun handleSendMessage() {
        val mockChatSdk = MockChatSdk()
        val chatActionHandler = ChatActionHandler(ChatService(mockChatSdk))
        chatActionHandler.handleSendMessage(action = ChatAction.SendMessage("Test Message"), dispatch)
        assertEquals(mockChatSdk.lastMessage,"Test Message")
    }
}

internal class MockChatSdk : ChatSDK {
    var lastMessage : String? = null

    override fun init() {
        TODO("Not yet implemented")
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }

    override fun sendMessage(
        type: ChatMessageType,
        content: String
    ): CompletableFuture<SendChatMessageResult> {
        lastMessage = content
        return CompletableFuture<SendChatMessageResult>()
    }

}
