// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.error

import com.azure.android.core.util.ExpandableStringEnum

internal class ErrorCode : ExpandableStringEnum<ErrorCode?>() {
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

        private fun fromString(name: String): ErrorCode {
            return fromString(name, ErrorCode::class.java)
        }
    }
}

internal class EventCode : ExpandableStringEnum<EventCode?>() {
    companion object {
        val CHAT_LOCAL_PARTICIPANT_REMOVED = fromString("chatLocalParticipantRemoved")

        private fun fromString(name: String): EventCode {
            return fromString(name, EventCode::class.java)
        }
    }
}
