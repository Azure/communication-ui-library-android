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
    public static final ChatCompositeErrorCode JOIN_FAILED = fromString("joinFailed");
    public static final ChatCompositeErrorCode SEND_MESSAGE_FAILED = fromString("sendMessageFailed");
    public static final ChatCompositeErrorCode START_EVENT_NOTIFICATIONS_FAILED =
            fromString("startEventNotificationsFailed");
    public static final ChatCompositeErrorCode FETCH_MESSAGES_FAILED = fromString("fetchMessagesFailed");
    public static final ChatCompositeErrorCode REQUEST_PARTICIPANTS_FETCH_FAILED =
            fromString("requestParticipantsFetchFailed");
    public static final ChatCompositeErrorCode SEND_READ_RECEIPT_FAILED = fromString("sendReadReceiptFailed");
    public static final ChatCompositeErrorCode SEND_TYPING_INDICATOR_FAILED =
            fromString("sendTypingIndicatorFailed");

    private static ChatCompositeErrorCode fromString(final String name) {
        return fromString(name, ChatCompositeErrorCode.class);
    }
}

