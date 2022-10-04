// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.events

import com.azure.android.communication.chat.models.ChatEvent
import com.azure.android.communication.chat.models.ChatMessageReceivedEvent
import com.azure.android.communication.chat.models.RealTimeNotificationCallback
import com.azure.android.communication.ui.chat.service.sdk.ChatEventsHandler
import com.azure.android.communication.ui.chat.service.sdk.into

internal class MessageReceived(private val chatEventsHandler: ChatEventsHandler) :
    RealTimeNotificationCallback {
    override fun onChatEvent(chatEvent: ChatEvent?) {
        val event = chatEvent as ChatMessageReceivedEvent
        chatEventsHandler.onMessageReceived(event.into())
    }
}
