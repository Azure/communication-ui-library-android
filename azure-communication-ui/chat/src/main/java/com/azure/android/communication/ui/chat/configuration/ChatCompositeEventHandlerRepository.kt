// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.configuration

import com.azure.android.communication.ui.chat.ChatCompositeEventHandler

internal class ChatCompositeEventHandlerRepository {
    private val eventHandlers: MutableList<ChatCompositeEventHandler<Any>> = mutableListOf()

    fun getLocalParticipantRemovedHandlers(): List<ChatCompositeEventHandler<Any>> {
        return eventHandlers
    }

    fun addLocalParticipantRemovedEventHandler(handler: ChatCompositeEventHandler<Any>) {
        eventHandlers.add(handler)
    }

    fun removeLocalParticipantRemovedEventHandler(handler: ChatCompositeEventHandler<Any>) {
        eventHandlers.remove(handler)
    }
}
