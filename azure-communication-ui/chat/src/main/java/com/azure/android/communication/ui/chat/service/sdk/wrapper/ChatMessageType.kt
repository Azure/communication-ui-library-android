// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.wrapper

internal enum class ChatMessageType {
    TEXT,
    HTML,
    TOPIC_UPDATED,
    PARTICIPANT_ADDED,
    PARTICIPANT_REMOVED,
}

internal fun ChatMessageType.into(): com.azure.android.communication.chat.models.ChatMessageType {
    return when (this) {
        ChatMessageType.TEXT -> com.azure.android.communication.chat.models.ChatMessageType.TEXT
        ChatMessageType.HTML -> com.azure.android.communication.chat.models.ChatMessageType.HTML
        ChatMessageType.TOPIC_UPDATED -> com.azure.android.communication.chat.models.ChatMessageType.TOPIC_UPDATED
        ChatMessageType.PARTICIPANT_ADDED -> com.azure.android.communication.chat.models.ChatMessageType.PARTICIPANT_ADDED
        ChatMessageType.PARTICIPANT_REMOVED -> com.azure.android.communication.chat.models.ChatMessageType.PARTICIPANT_REMOVED
    }
}

internal fun com.azure.android.communication.chat.models.ChatMessageType.into(): ChatMessageType {
    return when (this) {
        com.azure.android.communication.chat.models.ChatMessageType.TEXT -> ChatMessageType.TEXT
        com.azure.android.communication.chat.models.ChatMessageType.HTML -> ChatMessageType.HTML
        com.azure.android.communication.chat.models.ChatMessageType.TOPIC_UPDATED -> ChatMessageType.TOPIC_UPDATED
        com.azure.android.communication.chat.models.ChatMessageType.PARTICIPANT_ADDED -> ChatMessageType.PARTICIPANT_ADDED
        com.azure.android.communication.chat.models.ChatMessageType.PARTICIPANT_REMOVED -> ChatMessageType.PARTICIPANT_REMOVED
        else -> {
            throw IllegalStateException("Unknown type of ChatMessageType: $this")
        }
    }
}
