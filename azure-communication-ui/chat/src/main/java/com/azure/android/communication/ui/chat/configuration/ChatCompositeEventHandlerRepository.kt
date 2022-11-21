// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.configuration

import com.azure.android.communication.ui.chat.ChatCompositeEventHandler
import com.azure.android.communication.ui.chat.models.ChatCompositeEvent
import java.util.Collections

internal class ChatCompositeEventHandlerRepository {
    private val eventHandlers: MutableList<ChatCompositeEventHandler<ChatCompositeEvent>> = mutableListOf()

    fun getLocalParticipantRemovedHandlers(): List<ChatCompositeEventHandler<ChatCompositeEvent>> {
        return Collections.unmodifiableList(eventHandlers)
    }

    fun addLocalParticipantRemovedEventHandler(handler: ChatCompositeEventHandler<ChatCompositeEvent>) {
        eventHandlers.add(handler)
    }

    fun removeLocalParticipantRemovedEventHandler(handler: ChatCompositeEventHandler<ChatCompositeEvent>) {
        eventHandlers.remove(handler)
    }
}
