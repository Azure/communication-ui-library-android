// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationTokenCredential;

/**
 * Remote options for the calling composite
 *
 * <p> Options for dealing with the remote meeting. </p>
 * <p> {@link CommunicationTokenCredential} is required to access the Azure Resources</p>
 * <p> {@link CallCompositeJoinMeetingLocator} is required to locate the teams or group call you'd like to join</p>
 * <p> Additionally you can provide a displayName which is sent to the Server and shared with other clients</p>
 */
public class CallCompositeRemoteOptions {
    // Mandatory
    private final CommunicationTokenCredential credential;
    private final CallCompositeJoinMeetingLocator locator;

    // Optional
    private final String displayName;

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
