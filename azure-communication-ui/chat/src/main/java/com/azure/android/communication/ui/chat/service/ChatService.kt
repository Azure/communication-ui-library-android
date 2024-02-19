// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.service.sdk.ChatSDK
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import org.threeten.bp.OffsetDateTime

internal class ChatService(private val chatSDK: ChatSDK) {
    fun initialize() = chatSDK.initialization()

    fun destroy() = chatSDK.destroy()

    fun getAdminUserId() = chatSDK.getAdminUserId()

    fun requestPreviousPage() = chatSDK.requestPreviousPage()

    fun requestChatParticipants() = chatSDK.requestChatParticipants()

    fun startEventNotifications() = chatSDK.startEventNotifications()

    fun stopEventNotifications() = chatSDK.stopEventNotifications()

    fun getChatStatusStateFlow() = chatSDK.getChatStatusStateFlow()

    fun getMessagesPageSharedFlow() = chatSDK.getMessagesPageSharedFlow()

    fun getChatEventSharedFlow() = chatSDK.getChatEventSharedFlow()

    fun sendMessage(messageInfoModel: MessageInfoModel) = chatSDK.sendMessage(messageInfoModel = messageInfoModel)

    fun deleteMessage(id: String) = chatSDK.deleteMessage(id = id)

    fun editMessage(
        id: String,
        content: String,
    ) = chatSDK.editMessage(id = id, content = content)

    fun sendTypingIndicator() = chatSDK.sendTypingIndicator()

    fun sendReadReceipt(id: String) = chatSDK.sendReadReceipt(id = id)

    fun removeParticipant(communicationIdentifier: CommunicationIdentifier) =
        chatSDK.removeParticipant(communicationIdentifier = communicationIdentifier)

    fun fetchMessages(from: OffsetDateTime?) = chatSDK.fetchMessages(from = from)
}
