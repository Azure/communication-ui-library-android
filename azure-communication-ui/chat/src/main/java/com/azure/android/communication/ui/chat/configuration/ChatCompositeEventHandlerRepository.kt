// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.configuration

import com.azure.android.communication.ui.chat.ChatCompositeEventHandler

internal class ChatCompositeEventHandlerRepository {
    private val eventHandlers: MutableList<ChatCompositeEventHandler<String>> = mutableListOf()

    fun getLocalParticipantRemovedHandlers(): List<ChatCompositeEventHandler<String>> {
        return eventHandlers
    }

    fun addLocalParticipantRemovedEventHandler(handler: ChatCompositeEventHandler<String>) {
        eventHandlers.add(handler)
    }

    fun removeLocalParticipantRemovedEventHandler(handler: ChatCompositeEventHandler<String>) {
        eventHandlers.remove(handler)
    }
}
