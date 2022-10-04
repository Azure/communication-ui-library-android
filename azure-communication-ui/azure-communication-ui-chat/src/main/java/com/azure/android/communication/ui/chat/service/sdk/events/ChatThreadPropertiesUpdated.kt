// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.events

import com.azure.android.communication.chat.models.ChatEvent
import com.azure.android.communication.chat.models.ChatThreadCreatedEvent
import com.azure.android.communication.chat.models.RealTimeNotificationCallback
import com.azure.android.communication.ui.chat.service.sdk.ChatEventsHandler
import com.azure.android.communication.ui.chat.service.sdk.into

internal class ChatThreadPropertiesUpdated(private val chatEventsHandler: ChatEventsHandler) :
    RealTimeNotificationCallback {
    override fun onChatEvent(chatEvent: ChatEvent?) {
        chatEvent?.let {
            // get ChatThreadProperties from ChatThreadCreatedEvent
            val event = it as ChatThreadCreatedEvent
            chatEventsHandler.onChatThreadPropertiesUpdated(event.into().chatThreadProperties)
        }
    }
}
