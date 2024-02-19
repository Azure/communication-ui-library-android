// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import com.azure.android.communication.ui.chat.models.ChatEventModel
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.models.MessagesPageModel
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import com.azure.android.communication.ui.chat.service.sdk.wrapper.SendChatMessageResult
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.threeten.bp.OffsetDateTime

internal interface ChatSDK {
    fun initialization(): CompletableFuture<Void>

    fun destroy()

    fun getAdminUserId(): String

    fun requestPreviousPage()

    fun requestChatParticipants()

    fun startEventNotifications()

    fun stopEventNotifications()

    fun getChatStatusStateFlow(): StateFlow<ChatStatus>

    fun getMessagesPageSharedFlow(): SharedFlow<MessagesPageModel>

    fun getChatEventSharedFlow(): SharedFlow<ChatEventModel>

    fun sendMessage(messageInfoModel: MessageInfoModel): CompletableFuture<SendChatMessageResult>

    fun deleteMessage(id: String): CompletableFuture<Void>

    fun editMessage(
        id: String,
        content: String,
    ): CompletableFuture<Void>

    fun sendTypingIndicator(): CompletableFuture<Void>

    fun sendReadReceipt(id: String): CompletableFuture<Void>

    fun removeParticipant(communicationIdentifier: CommunicationIdentifier): CompletableFuture<Void>

    fun fetchMessages(from: OffsetDateTime?)
}
