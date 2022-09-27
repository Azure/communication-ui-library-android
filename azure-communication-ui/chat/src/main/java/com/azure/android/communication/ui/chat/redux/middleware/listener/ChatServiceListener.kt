// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.listener

import com.azure.android.communication.ui.chat.redux.middleware.ChatMiddleware
import com.azure.android.communication.ui.chat.service.ChatService

internal class ChatServiceListener(
    private val chatService: ChatService,
    private val chatMiddleware: ChatMiddleware
) {
    // subscribe to chat service and notify middleware
}
