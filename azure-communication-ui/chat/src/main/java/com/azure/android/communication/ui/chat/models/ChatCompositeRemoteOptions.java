// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

import com.azure.android.communication.common.CommunicationTokenCredential;

public class ChatCompositeRemoteOptions {

    private final String endpointUrl;
    private final String[] threadIds;
    private final CommunicationTokenCredential credential;
    private final String displayName;
    private final String identity;

    /**
     * Create {@link ChatCompositeRemoteOptions}.
     *
     *
     * @param credential {@link CommunicationTokenCredential}.
     * @param identity   {@link String}
     */
    public ChatCompositeRemoteOptions(
            final String endpointUrl,
            final String threadId,
            final CommunicationTokenCredential credential,
            final String identity) {
        this(endpointUrl, threadId, credential, identity, "");
    }

    /**
     * Create {@link ChatCompositeRemoteOptions}.
     *
     *
     * @param credential  {@link CommunicationTokenCredential}
     * @param identity    {@link String}
     * @param displayName User display name other participants will see.
     */
    public ChatCompositeRemoteOptions(
            final String endpointUrl,
            final String threadId,
            final CommunicationTokenCredential credential,
            final String identity,
            final String displayName) {

        this(endpointUrl, new String[]{threadId}, credential, identity, displayName);
    }

    public ChatCompositeRemoteOptions(
            final String endpointUrl,
            final String[] threadIds,
            final CommunicationTokenCredential credential,
            final String identity,
            final String displayName) {

        this.endpointUrl = endpointUrl;
        this.threadIds = threadIds;
        this.credential = credential;
        this.identity = identity;
        this.displayName = displayName;
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
     * Get {@link CommunicationTokenCredential}.
     *
     * @return {@link String}.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get user Identity.
     *
     * @return {@link String}.
     */
    public String getIdentity() {
        return identity;
    }

    // it does not correspond with current single ThreadID constructor.
    public String[] getThreadIDs() {
        return threadIds;
    }

    // it does not correspond with current multiple ThreadID constructor.
    public String getThreadId() {
        return threadIds[0];
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }


}
