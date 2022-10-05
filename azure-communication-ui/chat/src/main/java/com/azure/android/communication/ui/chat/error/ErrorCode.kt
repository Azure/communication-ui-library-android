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
        val CHAT_REQUEST_PARTICIPANTS_FAILED = fromString("chatRequestParticipantsFailed")

        private fun fromString(name: String): ErrorCode {
            return fromString(name, ErrorCode::class.java)
        }
    }
}
