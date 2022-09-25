// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.SendChatMessageResult
import java9.util.concurrent.CompletableFuture

internal interface ChatSDK {
    fun init()
    fun dispose()
    fun sendMessage(
        type: ChatMessageType,
        content: String,
    ): CompletableFuture<SendChatMessageResult>
}
