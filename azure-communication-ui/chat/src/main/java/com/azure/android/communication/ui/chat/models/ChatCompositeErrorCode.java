// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

import com.azure.android.core.util.ExpandableStringEnum;

/**
 * Defines values for ChatCompositeErrorCode.
 */
public final class ChatCompositeErrorCode extends ExpandableStringEnum<ChatCompositeErrorCode> {
    /**
     * Dispatched when the ACS Token supplied is no longer valid (expired).
     */
    public static final ChatCompositeErrorCode TOKEN_EXPIRED = fromString("tokenExpired");
    public static final ChatCompositeErrorCode CHAT_JOIN_FAILED = fromString("chatJoinFailed");
    public static final ChatCompositeErrorCode CHAT_SEND_MESSAGE_FAILED = fromString("chatSendMessageFailed");
    public static final ChatCompositeErrorCode CHAT_START_EVENT_NOTIFICATIONS_FAILED =
            fromString("chatStartEventNotificationsFailed");
    public static final ChatCompositeErrorCode CHAT_FETCH_MESSAGES_FAILED = fromString("chatFetchMessagesFailed");
    public static final ChatCompositeErrorCode CHAT_REQUEST_PARTICIPANTS_FETCH_FAILED =
            fromString("chatRequestParticipantsFetchFailed");
    public static final ChatCompositeErrorCode CHAT_SEND_EDIT_MESSAGE_FAILED = fromString("chatSendEditMessageFailed");
    public static final ChatCompositeErrorCode CHAT_SEND_READ_RECEIPT_FAILED = fromString("chatSendReadReceiptFailed");
    public static final ChatCompositeErrorCode CHAT_SEND_TYPING_INDICATOR_FAILED =
            fromString("chatSendTypingIndicatorFailed");

    ChatCompositeErrorCode() {
    }

    private static ChatCompositeErrorCode fromString(final String name) {
        return fromString(name, ChatCompositeErrorCode.class);
    }
}

