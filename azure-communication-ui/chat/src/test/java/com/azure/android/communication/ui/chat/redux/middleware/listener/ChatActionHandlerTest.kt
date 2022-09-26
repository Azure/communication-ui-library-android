package com.azure.android.communication.ui.chat.redux.middleware.listener

import com.azure.android.communication.ui.chat.MockChatSdk
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.service.ChatService
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

