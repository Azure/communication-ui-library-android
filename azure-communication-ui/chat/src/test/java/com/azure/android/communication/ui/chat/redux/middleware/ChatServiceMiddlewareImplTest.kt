package com.azure.android.communication.ui.chat.redux.middleware

import com.azure.android.communication.ui.chat.MockChatSdk
import com.azure.android.communication.ui.chat.MockStore
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.action.LifecycleAction
import com.azure.android.communication.ui.chat.service.ChatService
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertEquals

import org.junit.Test

internal class ChatServiceMiddlewareImplTest {

    // Verify Wakeup is triggered
    @Test
    fun testWakeupAndShutdown() {
        var mockChatSdk = MockChatSdk()
        val mw = ChatServiceMiddlewareImpl(
            ChatService(mockChatSdk)
        )

        val store = MockStore()

        mw.invoke(store).invoke { }(LifecycleAction.Wakeup())

        assertTrue(mockChatSdk.started)
        assertTrue(mw.chatServiceListener.isListening)

        mw.invoke(store).invoke { }(LifecycleAction.Shutdown())

        assertFalse(mockChatSdk.started)
        assertFalse(mw.chatServiceListener.isListening)
    }

    // Verify Wakeup is triggered
    @Test
    fun testActionHandlerReceivingEvents() {
        var mockChatSdk = MockChatSdk()
        val mw = ChatServiceMiddlewareImpl(
            ChatService(mockChatSdk)
        )
        val store = MockStore()
        mw.invoke(store).invoke { }(LifecycleAction.Wakeup())
        mw.invoke(store).invoke { }(ChatAction.SendMessage("Test Message"))

        // This should have hit the ChatActionMiddleware, then ended up in the Service
        assertEquals(mockChatSdk.lastMessage, "Test Message")
    }
}
