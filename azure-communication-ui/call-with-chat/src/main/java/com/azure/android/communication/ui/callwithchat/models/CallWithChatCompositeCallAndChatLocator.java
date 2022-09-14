// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.models;

import com.azure.android.communication.ui.callwithchat.CallWithChatComposite;

import java.util.UUID;

/**
 * Group Call locator to start group call with chat experience using {@link CallWithChatComposite}.
 */
public final class CallWithChatCompositeCallAndChatLocator extends CallWithChatCompositeJoinLocator {

    private final UUID groupId;
    private final String chatThreadId;

    /**
     * Creates {@link CallWithChatCompositeCallAndChatLocator}.
     * @param groupId   Group call identifier.
     * @param chatThreadId  Chat thread ID
     * @param endpoint  ACS resource endpoint
     */
    public CallWithChatCompositeCallAndChatLocator(final String endpoint,
                                                   final UUID groupId,
                                                   final String chatThreadId
    ) {
        super(endpoint);
        this.groupId = groupId;
        this.chatThreadId = chatThreadId;
    }

    /**
     * Get group call id.
     *
     * @return {@link UUID}
     */
    public UUID getGroupId() {
        return groupId;
    }

    public String getChatThreadId() {
        return chatThreadId;
    }
}
