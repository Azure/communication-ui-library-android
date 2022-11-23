package com.azure.android.communication.ui.chat

import com.azure.android.communication.ui.chat.error.ChatCompositeErrorEvent

internal class ChatCompositeEventsHandler {
    private val errorHandlers = mutableSetOf<ChatCompositeEventHandler<ChatCompositeErrorEvent>>()

    fun getOnErrorHandlers() = errorHandlers.asIterable()

    fun addOnErrorEventHandler(errorHandler: ChatCompositeEventHandler<ChatCompositeErrorEvent>) =
        errorHandlers.add(errorHandler)

    fun removeOnErrorEventHandler(errorHandler: ChatCompositeEventHandler<ChatCompositeErrorEvent>) =
        errorHandlers.remove(errorHandler)
}
