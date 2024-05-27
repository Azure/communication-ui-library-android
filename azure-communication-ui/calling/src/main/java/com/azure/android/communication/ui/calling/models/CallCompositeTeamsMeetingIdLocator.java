// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallComposite;

/**
 * Teams meeting locator to start group call experience using {@link CallComposite}.
 */
public final class CallCompositeTeamsMeetingIdLocator extends CallCompositeJoinLocator {

    private final String meetingId;
    private final String meetingPasscode;

    /**
     * Creates {@link CallCompositeTeamsMeetingLinkLocator}.
     *
     * @param meetingId Teams meeting id, for more information please check Quickstart Doc.
     * @param meetingPasscode Teams meeting passcode
     */
    public CallCompositeTeamsMeetingIdLocator(final String meetingId, final String meetingPasscode) {
        this.meetingId = meetingId;
        this.meetingPasscode = meetingPasscode;
    }

    /**
     * Get Teams meeting id.
     *
     * @return {@link String}.
     */
    public String getMeetingId() {
        return meetingId;
    }

    public String getMeetingPasscode() {
        return meetingPasscode;
    }
}
