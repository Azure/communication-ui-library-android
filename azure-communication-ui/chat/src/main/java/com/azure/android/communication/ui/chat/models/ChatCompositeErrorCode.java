// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

import com.azure.android.core.util.ExpandableStringEnum;

/**
 * Defines values for ChatCompositeErrorCode.
 */
final class ChatCompositeErrorCode extends ExpandableStringEnum<ChatCompositeErrorCode> {

    /**
     * Dispatched when the ACS Token supplied is no longer valid (expired).
     */
    public static final ChatCompositeErrorCode TOKEN_EXPIRED = fromString("tokenExpired");


    ChatCompositeErrorCode() {
    }

    private static ChatCompositeErrorCode fromString(final String name) {
        return fromString(name, ChatCompositeErrorCode.class);
    }
}

