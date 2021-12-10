// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui;

import android.content.Context;
import com.azure.android.communication.common.CommunicationTokenCredential;

/**
 * Options to start Teams meeting call experience using {@link CallComposite}.
 */
public final class TeamsMeetingOptions {
    private final Context context;
    private final CommunicationTokenCredential communicationTokenCredential;
    private final String displayName;
    private final String meetingLink;

    /**
     * Create {@link TeamsMeetingOptions}.
     * @param context {@link Context}
     * @param communicationTokenCredential {@link CommunicationTokenCredential}
     * @param meetingLink Teams meeting link
     */
    public TeamsMeetingOptions(final Context context,
                               final CommunicationTokenCredential communicationTokenCredential,
                               final String meetingLink) {
        this(context, communicationTokenCredential, meetingLink, "");
    }

    /**
     * Create {@link TeamsMeetingOptions}.
     * @param context {@link Context}
     * @param communicationTokenCredential {@link CommunicationTokenCredential}
     * @param meetingLink Teams meeting link
     * @param displayName user display name
     */
    public TeamsMeetingOptions(final Context context,
                               final CommunicationTokenCredential communicationTokenCredential,
                               final String meetingLink,
                               final String displayName) {
        this.context = context;
        this.communicationTokenCredential = communicationTokenCredential;
        this.displayName = displayName;
        this.meetingLink = meetingLink;
    }

    /**
     * Get Context of the application.
     * @return {@link Context}
     */
    public Context getContext() {
        return context;
    }

    /**
     * Get {@link CommunicationTokenCredential}.
     * @return {@link String}
     */
    public CommunicationTokenCredential getCommunicationTokenCredential() {
        return communicationTokenCredential;
    }

    /**
     * Get user display name.
     * @return {@link String}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get Teams meeting link.
     * @return {@link String}
     */
    public String getMeetingLink() {
        return meetingLink;
    }
}
