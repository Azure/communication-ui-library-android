// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling;

import com.azure.android.communication.common.CommunicationTokenCredential;

import java.util.UUID;

/**
 * Options to start group call experience using {@link CallComposite}.
 */
public final class GroupCallOptions {
    private final CommunicationTokenCredential credential;
    private final String displayName;
    private final UUID groupId;

    /**
     * Create {@link GroupCallOptions}.
     *
     * @param credential {@link CommunicationTokenCredential}
     * @param groupId                      group call identifier
     */
    public GroupCallOptions(
            final CommunicationTokenCredential credential,
            final UUID groupId) {
        this(credential, groupId, "");
    }

    /**
     * Create {@link GroupCallOptions}.
     *
     * @param credential {@link CommunicationTokenCredential}
     * @param groupId                      group call identifier
     * @param displayName                  user display name
     */
    public GroupCallOptions(final CommunicationTokenCredential credential,
                            final UUID groupId,
                            final String displayName) {

        this.credential = credential;
        this.displayName = displayName;
        this.groupId = groupId;
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
     * Get group call id.
     *
     * @return {@link UUID}
     */
    public UUID getGroupId() {
        return groupId;
    }
}
