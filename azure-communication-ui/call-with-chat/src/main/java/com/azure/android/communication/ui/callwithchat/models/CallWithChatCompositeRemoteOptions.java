// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.models;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.common.CommunicationTokenCredential;

public final class CallWithChatCompositeRemoteOptions {
    private final CommunicationIdentifier communicationIdentifier;
    // Mandatory
    private final CommunicationTokenCredential credential;
    private final CallWithChatCompositeJoinLocator locator;

    // Optional
    private final String displayName;

    /**
     * Create {@link CallWithChatCompositeRemoteOptions}.
     *
     * @param locator {@link CallWithChatCompositeJoinLocator}
     * @param credential {@link CommunicationTokenCredential}.
     */
    public CallWithChatCompositeRemoteOptions(
            final CallWithChatCompositeJoinLocator locator,
            final CommunicationIdentifier communicationIdentifier,
            final CommunicationTokenCredential credential) {
        this(locator, communicationIdentifier, credential, "");
    }

    /**
     * Create {@link CallWithChatCompositeRemoteOptions}.
     *
     * @param locator {@link CallWithChatCompositeJoinLocator}
     * @param credential {@link CommunicationTokenCredential}
     * @param displayName                  User display name other call participants will see.
     */
    public CallWithChatCompositeRemoteOptions(
            final CallWithChatCompositeJoinLocator locator,
            final CommunicationIdentifier communicationIdentifier,
            final CommunicationTokenCredential credential,
            final String displayName) {
        this.communicationIdentifier = communicationIdentifier;
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
     * @return {@link CallWithChatCompositeJoinLocator}.
     */
    public CallWithChatCompositeJoinLocator getLocator() {
        return locator;
    }

    public CommunicationIdentifier getCommunicationIdentifier() {
        return communicationIdentifier;
    }
}
