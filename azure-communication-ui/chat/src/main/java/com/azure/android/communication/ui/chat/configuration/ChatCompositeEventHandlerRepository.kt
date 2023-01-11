// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.configuration

import com.azure.android.communication.ui.chat.ChatCompositeEventHandler
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent

internal class ChatCompositeEventHandlerRepository {
    private val eventHandlers = mutableListOf<ChatCompositeEventHandler<String>>()
    private val errorHandlers = mutableListOf<ChatCompositeEventHandler<ChatCompositeErrorEvent>>()

    fun getOnErrorHandlers(): List<ChatCompositeEventHandler<ChatCompositeErrorEvent>> = errorHandlers

    fun addOnErrorEventHandler(errorHandler: ChatCompositeEventHandler<ChatCompositeErrorEvent>) =
        errorHandlers.add(errorHandler)

    fun removeOnErrorEventHandler(errorHandler: ChatCompositeEventHandler<ChatCompositeErrorEvent>) =
        errorHandlers.remove(errorHandler)

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
