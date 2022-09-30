// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.models.MessagesPageModel
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.service.sdk.wrapper.SendChatMessageResult
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

internal interface ChatSDK {
    fun getChatStatusStateFlow(): StateFlow<ChatStatus>
    fun getMessageSharedFlow(): SharedFlow<MessagesPageModel>

    fun initialization()
    fun getPreviousPage()

    fun sendMessage(
        messageInfoModel: MessageInfoModel,
    ): CompletableFuture<SendChatMessageResult>
}
