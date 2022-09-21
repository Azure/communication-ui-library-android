// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service

import com.azure.android.communication.ui.chat.service.sdk.ChatSDK
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType

internal class ChatService(private val chatSDK: ChatSDK) {
    fun init() = chatSDK.init()
    fun sendMessage(
        type: ChatMessageType,
        content: String,
    ) = chatSDK.sendMessage(type = type, content = content)
}
