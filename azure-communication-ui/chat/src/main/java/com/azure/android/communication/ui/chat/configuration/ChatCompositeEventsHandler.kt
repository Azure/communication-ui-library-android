// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.configuration

import com.azure.android.communication.ui.chat.ChatCompositeEventHandler
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent

class ChatCompositeEventsHandler {
    private val errorHandlers = mutableSetOf<ChatCompositeEventHandler<ChatCompositeErrorEvent>>()

    fun getOnErrorHandlers() = errorHandlers.asIterable()

    fun addOnErrorEventHandler(errorHandler: ChatCompositeEventHandler<ChatCompositeErrorEvent>) =
        errorHandlers.add(errorHandler)

    fun removeOnErrorEventHandler(errorHandler: ChatCompositeEventHandler<ChatCompositeErrorEvent>) =
        errorHandlers.remove(errorHandler)
}
