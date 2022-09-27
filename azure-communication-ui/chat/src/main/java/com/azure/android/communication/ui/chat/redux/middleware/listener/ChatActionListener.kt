// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.listener

import com.azure.android.communication.ui.chat.service.ChatService

internal class ChatActionListener(private val chatService: ChatService) {
    fun initializeChat() {
        chatService.init()
    }
}
