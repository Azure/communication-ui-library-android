// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui;

import com.azure.android.communication.common.CommunicationTokenCredential;

/**
 * Options to start Teams meeting call experience using {@link CallComposite}.
 */
public final class TeamsMeetingOptions {
    private final CommunicationTokenCredential credential;
    private final String displayName;
    private final String meetingLink;

    /**
     * Create {@link TeamsMeetingOptions}.
     *
     * @param credential {@link CommunicationTokenCredential}
     * @param meetingLink                  Teams meeting link, more information can check Quickstart Doc
     */
    public TeamsMeetingOptions(
            final CommunicationTokenCredential credential,
            final String meetingLink) {
        this(credential, meetingLink, "");
    }

    /**
     * Create {@link TeamsMeetingOptions}.
     *
     * @param credential {@link CommunicationTokenCredential}
     * @param meetingLink                  Teams meeting link, more information can check Quickstart Doc
     * @param displayName                  user display name
     */
    public TeamsMeetingOptions(final CommunicationTokenCredential credential,
                               final String meetingLink,
                               final String displayName) {
        this.credential = credential;
        this.displayName = displayName;
        this.meetingLink = meetingLink;
    }

    /**
     * Get {@link CommunicationTokenCredential}.
     *
     * @return {@link String}
     */
    public CommunicationTokenCredential getCredential() {
        return credential;
    }

    /**
     * Get user display name.
     *
     * @return {@link String}
     */
    public String getDisplayName() {
        return displayName;
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
