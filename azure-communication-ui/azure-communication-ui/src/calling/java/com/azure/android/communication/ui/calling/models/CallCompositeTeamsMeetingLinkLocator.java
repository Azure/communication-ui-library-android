// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallComposite;

/**
 * Teams meeting locator to start group call experience using {@link CallComposite}.
 */
public final class CallCompositeTeamsMeetingLinkLocator extends CallCompositeJoinMeetingLocator {

    private final String meetingLink;

    /**
     * Creates {@link CallCompositeTeamsMeetingLinkLocator}
     *
     * @param meetingLink Teams meeting link, for more information please check Quickstart Doc
     */
    public CallCompositeTeamsMeetingLinkLocator(final String meetingLink) {
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
