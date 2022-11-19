// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.error

import com.azure.android.core.util.ExpandableStringEnum

internal class ChatCompositeErrorCode : ExpandableStringEnum<ChatCompositeErrorCode?>() {
    companion object {
        val CHAT_JOIN_FAILED = fromString("chatJoinFailed")
        val CHAT_SEND_MESSAGE_FAILED = fromString("chatSendMessageFailed")
        val CHAT_START_EVENT_NOTIFICATIONS_FAILED = fromString("chatStartEventNotificationsFailed")
        val CHAT_FETCH_MESSAGES_FAILED = fromString("chatFetchMessagesFailed")
        val CHAT_REQUEST_PARTICIPANTS_FETCH_FAILED =
            fromString("chatRequestParticipantsFetchFailed")
        val CHAT_SEND_EDIT_MESSAGE_FAILED = fromString("chatSendEditMessageFailed")
        val CHAT_SEND_READ_RECEIPT_FAILED = fromString("chatSendReadReceiptFailed")
        val CHAT_SEND_TYPING_INDICATOR_FAILED = fromString("chatSendTypingIndicatorFailed")

        private fun fromString(name: String): ChatCompositeErrorCode {
            return fromString(name, ChatCompositeErrorCode::class.java)
        }
    }
}
