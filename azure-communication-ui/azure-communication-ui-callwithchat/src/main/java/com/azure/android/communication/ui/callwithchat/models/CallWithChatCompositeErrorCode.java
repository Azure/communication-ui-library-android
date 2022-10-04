// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.models;


import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for CallWithChatCompositeErrorCode.
 */
public final class CallWithChatCompositeErrorCode extends ExpandableStringEnum<CallWithChatCompositeErrorCode> {

    /**
     * Dispatched when there is a failure to join a call
     */
    public static final CallWithChatCompositeErrorCode CALL_JOIN_FAILED = fromString("callJoinFailed");

    /**
     * Dispatched when there is a failure to end a call
     */
    public static final CallWithChatCompositeErrorCode CALL_END_FAILED = fromString("callEndFailed");

    /**
     * Dispatched when the ACS Token supplied is no longer valid (expired)
     */
    public static final CallWithChatCompositeErrorCode TOKEN_EXPIRED = fromString("tokenExpired");

    //TODO: add chat related error codes

    /**
     * Creates or finds a {@link CallWithChatCompositeErrorCode} from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding {@link CallWithChatCompositeErrorCode}.
     */
    private static CallWithChatCompositeErrorCode fromString(final String name) {
        return fromString(name, CallWithChatCompositeErrorCode.class);
    }

    /**
     * @return known {@link CallWithChatCompositeErrorCode} values.
     */
    public static Collection<CallWithChatCompositeErrorCode> values() {
        return values(CallWithChatCompositeErrorCode.class);
    }

}
