// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.SendChatMessageResult
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.flow.StateFlow

internal interface ChatSDK {
    fun initialization()
    fun getChatStatusStateFlow(): StateFlow<ChatStatus>

    fun sendMessage(
        type: ChatMessageType,
        content: String,
    ): CompletableFuture<SendChatMessageResult>

    fun startEventNotifications(errorHandler: (exception: Throwable) -> Unit) {}
    fun stopEventNotifications() {}
}
