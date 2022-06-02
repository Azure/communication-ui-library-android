// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationTokenCredential;

public class CallCompositeRemoteOptions {
    private final CommunicationTokenCredential credential;
    private final String displayName;
    private final CallCompositeJoinMeetingLocator locator;

    /**
     * Create {@link CallCompositeRemoteOptions}.
     *
     * @param locator {@link CallCompositeJoinMeetingLocator}
     * @param credential {@link CommunicationTokenCredential}.
     */
    public CallCompositeRemoteOptions(
            final CallCompositeJoinMeetingLocator locator,
            final CommunicationTokenCredential credential) {
        this(locator, credential, "");
    }

    /**
     * Create {@link CallCompositeRemoteOptions}.
     *
     * @param locator {@link CallCompositeJoinMeetingLocator}
     * @param credential {@link CommunicationTokenCredential}
     * @param displayName                  User display name other call participants will see.
     */
    public CallCompositeRemoteOptions(
            final CallCompositeJoinMeetingLocator locator,
            final CommunicationTokenCredential credential,
            final String displayName) {

        this.credential = credential;
        this.displayName = displayName;
        this.locator = locator;
    }

    /**
     * Get {@link CommunicationTokenCredential}.
     *
     * @return {@link String}.
     */
    public CommunicationTokenCredential getCredential() {
        return credential;
    }

    /**
     * Get user display name.
     *
     * @return {@link String}.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get call locator.
     *
     * @return {@link CallCompositeJoinMeetingLocator}.
     */
    public CallCompositeJoinMeetingLocator getLocator() {
        return locator;
    }
}
