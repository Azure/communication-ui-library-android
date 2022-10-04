// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.wrapper

internal enum class ChatEventType {
    CHAT_MESSAGE_RECEIVED,
    CHAT_MESSAGE_EDITED,
    CHAT_MESSAGE_DELETED,
    TYPING_INDICATOR_RECEIVED,
    READ_RECEIPT_RECEIVED,
    CHAT_THREAD_CREATED,
    CHAT_THREAD_DELETED,
    CHAT_THREAD_PROPERTIES_UPDATED,
    PARTICIPANTS_ADDED,
    PARTICIPANTS_REMOVED,
}

internal fun ChatEventType.into(): com.azure.android.communication.chat.models.ChatEventType {
    return when (this) {
        ChatEventType.CHAT_MESSAGE_RECEIVED -> com.azure.android.communication.chat.models.ChatEventType.CHAT_MESSAGE_RECEIVED
        ChatEventType.CHAT_MESSAGE_EDITED -> com.azure.android.communication.chat.models.ChatEventType.CHAT_MESSAGE_EDITED
        ChatEventType.CHAT_MESSAGE_DELETED -> com.azure.android.communication.chat.models.ChatEventType.CHAT_MESSAGE_DELETED
        ChatEventType.TYPING_INDICATOR_RECEIVED -> com.azure.android.communication.chat.models.ChatEventType.TYPING_INDICATOR_RECEIVED
        ChatEventType.READ_RECEIPT_RECEIVED -> com.azure.android.communication.chat.models.ChatEventType.READ_RECEIPT_RECEIVED
        ChatEventType.CHAT_THREAD_CREATED -> com.azure.android.communication.chat.models.ChatEventType.CHAT_THREAD_CREATED
        ChatEventType.CHAT_THREAD_DELETED -> com.azure.android.communication.chat.models.ChatEventType.CHAT_THREAD_DELETED
        ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED -> com.azure.android.communication.chat.models.ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED
        ChatEventType.PARTICIPANTS_ADDED -> com.azure.android.communication.chat.models.ChatEventType.PARTICIPANTS_ADDED
        ChatEventType.PARTICIPANTS_REMOVED -> com.azure.android.communication.chat.models.ChatEventType.PARTICIPANTS_REMOVED
    }
}

internal fun com.azure.android.communication.chat.models.ChatEventType.into(): ChatEventType {
    return when (this) {
        com.azure.android.communication.chat.models.ChatEventType.CHAT_MESSAGE_RECEIVED -> ChatEventType.CHAT_MESSAGE_RECEIVED
        com.azure.android.communication.chat.models.ChatEventType.CHAT_MESSAGE_EDITED -> ChatEventType.CHAT_MESSAGE_EDITED
        com.azure.android.communication.chat.models.ChatEventType.CHAT_MESSAGE_DELETED -> ChatEventType.CHAT_MESSAGE_DELETED
        com.azure.android.communication.chat.models.ChatEventType.TYPING_INDICATOR_RECEIVED -> ChatEventType.TYPING_INDICATOR_RECEIVED
        com.azure.android.communication.chat.models.ChatEventType.READ_RECEIPT_RECEIVED -> ChatEventType.READ_RECEIPT_RECEIVED
        com.azure.android.communication.chat.models.ChatEventType.CHAT_THREAD_CREATED -> ChatEventType.CHAT_THREAD_CREATED
        com.azure.android.communication.chat.models.ChatEventType.CHAT_THREAD_DELETED -> ChatEventType.CHAT_THREAD_DELETED
        com.azure.android.communication.chat.models.ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED -> ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED
        com.azure.android.communication.chat.models.ChatEventType.PARTICIPANTS_ADDED -> ChatEventType.PARTICIPANTS_ADDED
        com.azure.android.communication.chat.models.ChatEventType.PARTICIPANTS_REMOVED -> ChatEventType.PARTICIPANTS_REMOVED

        else -> {
            throw IllegalStateException("Unknown type of ChatEventType: $this")
        }
    }
}
