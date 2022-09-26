package com.azure.android.communication.ui.chat

import com.azure.android.communication.ui.chat.service.sdk.ChatSDK
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.SendChatMessageResult
import java9.util.concurrent.CompletableFuture

internal class MockChatSdk : ChatSDK {
    var lastMessage: String? = null
    var started = false

    override fun init() {
        started = true
    }

    override fun dispose() {
        started = false
    }

    override fun sendMessage(
        type: ChatMessageType,
        content: String
    ): CompletableFuture<SendChatMessageResult> {
        lastMessage = content
        return CompletableFuture<SendChatMessageResult>()
    }
}
