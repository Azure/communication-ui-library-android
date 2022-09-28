// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.service.sdk.ChatSDK

internal class ChatService(private val chatSDK: ChatSDK) {
    fun initialize() = chatSDK.initialization()
    fun getChatStatusStateFlow() = chatSDK.getChatStatusStateFlow()
    fun startReceivingEvents(errorHandler: (exception: Throwable) -> Unit) = chatSDK.startEventNotifications(errorHandler = errorHandler)
    fun sendMessage(
        messageInfoModel: MessageInfoModel,
    ) = chatSDK.sendMessage(messageInfoModel)
}
