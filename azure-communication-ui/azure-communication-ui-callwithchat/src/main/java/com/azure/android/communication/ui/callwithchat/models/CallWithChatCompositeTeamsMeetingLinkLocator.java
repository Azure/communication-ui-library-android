// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.models;

import com.azure.android.communication.ui.callwithchat.CallWithChatComposite;

/**
 * Teams meeting locator to start group call with chat experience using {@link CallWithChatComposite}.
 */
public final class CallWithChatCompositeTeamsMeetingLinkLocator extends CallWithChatCompositeJoinLocator {
    private final String meetingLink;

    /**
     * Creates {@link CallWithChatCompositeTeamsMeetingLinkLocator}
     *
     * @param meetingLink Teams meeting link, for more information please check Quickstart Doc.
     * @param endpoint  ACS resource endpoint
     */
    public CallWithChatCompositeTeamsMeetingLinkLocator(final String endpoint,
                                                        final String meetingLink) {
        super(endpoint);
        this.meetingLink = meetingLink;
    }

    /**
     * Get Teams meeting link.
     *
     * @return {@link String}
     */
    public String getMeetingLink() {
        return meetingLink;
    }

}
