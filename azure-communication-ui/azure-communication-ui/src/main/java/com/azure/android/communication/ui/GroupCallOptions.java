// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui;

import com.azure.android.communication.common.CommunicationTokenCredential;

import java.util.UUID;

/**
 * Options to start group call experience using {@link CallComposite}.
 */
public final class GroupCallOptions {
    private final CommunicationTokenCredential communicationTokenCredential;
    private final String displayName;
    private final UUID groupId;

    /**
     * Create {@link GroupCallOptions}.
     *
     * @param communicationTokenCredential {@link CommunicationTokenCredential}
     * @param groupId                      group call identifier
     */
    public GroupCallOptions(
            final CommunicationTokenCredential communicationTokenCredential,
            final UUID groupId) {
        this(communicationTokenCredential, groupId, "");
    }

    /**
     * Create {@link GroupCallOptions}.
     *
     * @param communicationTokenCredential {@link CommunicationTokenCredential}
     * @param groupId                      group call identifier
     * @param displayName                  user display name
     */
    public GroupCallOptions(final CommunicationTokenCredential communicationTokenCredential,
                            final UUID groupId,
                            final String displayName) {

        this.communicationTokenCredential = communicationTokenCredential;
        this.displayName = displayName;
        this.groupId = groupId;
    }

    /**
     * Get {@link CommunicationTokenCredential}.
     *
     * @return {@link String}
     */
    public CommunicationTokenCredential getCommunicationTokenCredential() {
        return communicationTokenCredential;
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
     * Get group call id.
     *
     * @return {@link UUID}
     */
    public UUID getGroupId() {
        return groupId;
    }
}
