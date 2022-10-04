// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import com.azure.android.communication.ui.chat.service.sdk.events.ChatThreadCreated
import com.azure.android.communication.ui.chat.service.sdk.events.ChatThreadDeleted
import com.azure.android.communication.ui.chat.service.sdk.events.ChatThreadPropertiesUpdated
import com.azure.android.communication.ui.chat.service.sdk.events.MessageDeleted
import com.azure.android.communication.ui.chat.service.sdk.events.MessageEdited
import com.azure.android.communication.ui.chat.service.sdk.events.MessageReceived
import com.azure.android.communication.ui.chat.service.sdk.events.ParticipantAdded
import com.azure.android.communication.ui.chat.service.sdk.events.ParticipantRemoved
import com.azure.android.communication.ui.chat.service.sdk.events.ReadReceiptReceived
import com.azure.android.communication.ui.chat.service.sdk.events.TypingIndicatorReceived

internal class ChatEventsFactory(private val chatEventsHandler: ChatEventsHandler) {
    private var messageReceived: MessageReceived? = null
    private var messageDeleted: MessageDeleted? = null
    private var messageEdited: MessageEdited? = null
    private var typingIndicatorReceived: TypingIndicatorReceived? = null
    private var readReceiptReceived: ReadReceiptReceived? = null
    private var chatThreadCreated: ChatThreadCreated? = null
    private var chatThreadDeleted: ChatThreadDeleted? = null
    private var chatThreadPropertiesUpdated: ChatThreadPropertiesUpdated? = null
    private var participantAdded: ParticipantAdded? = null
    private var participantRemoved: ParticipantRemoved? = null

    fun getMessageReceivedEvent(): MessageReceived? {
        if (messageReceived == null) {
            messageReceived = MessageReceived(chatEventsHandler)
        }
        return messageReceived
    }

    fun getMessageDeletedEvent(): MessageDeleted? {
        if (messageDeleted == null) {
            messageDeleted = MessageDeleted(chatEventsHandler)
        }
        return messageDeleted
    }

    fun getMessageEditedEvent(): MessageEdited? {
        if (messageEdited == null) {
            messageEdited = MessageEdited(chatEventsHandler)
        }

        return messageEdited
    }

    fun getTypingIndicatorEvent(): TypingIndicatorReceived? {
        if (typingIndicatorReceived == null) {
            typingIndicatorReceived = TypingIndicatorReceived(chatEventsHandler)
        }
        return typingIndicatorReceived
    }

    fun getReadReceiptReceivedEvent(): ReadReceiptReceived? {
        if (readReceiptReceived == null) {
            readReceiptReceived = ReadReceiptReceived(chatEventsHandler)
        }

        return readReceiptReceived
    }

    fun getChatThreadCreated(): ChatThreadCreated? {

        if (chatThreadCreated == null) {
            chatThreadCreated = ChatThreadCreated(chatEventsHandler)
        }

        return chatThreadCreated
    }

    fun getChatThreadDeleted(): ChatThreadDeleted? {
        if (chatThreadDeleted == null) {
            chatThreadDeleted = ChatThreadDeleted(chatEventsHandler)
        }

        return chatThreadDeleted
    }

    fun getParticipantAdded(): ParticipantAdded? {
        if (participantAdded == null) {
            participantAdded = ParticipantAdded(chatEventsHandler)
        }
        return participantAdded
    }

    fun getParticipantRemoved(): ParticipantRemoved? {
        if (participantRemoved == null) {
            participantRemoved = ParticipantRemoved(chatEventsHandler)
        }
        return participantRemoved
    }

    fun getChatThreadPropertiesUpdated(): ChatThreadPropertiesUpdated? {
        if (chatThreadPropertiesUpdated == null) {
            chatThreadPropertiesUpdated = ChatThreadPropertiesUpdated(chatEventsHandler)
        }
        return chatThreadPropertiesUpdated
    }
}
