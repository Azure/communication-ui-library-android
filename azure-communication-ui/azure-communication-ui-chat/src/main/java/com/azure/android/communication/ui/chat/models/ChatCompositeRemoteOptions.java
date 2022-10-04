// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.common.CommunicationTokenCredential;

public final class ChatCompositeRemoteOptions {

    // Mandatory
    private final CommunicationTokenCredential credential;
    private final ChatCompositeJoinLocator locator;
    private final CommunicationIdentifier communicationIdentifier;

    // Optional
    private final String displayName;
    private final String applicationID;
    private final String sdkName;
    private final String sdkVersion;

    /**
     * Create {@link ChatCompositeRemoteOptions}.
     *
     * @param locator {@link ChatCompositeRemoteOptions}
     * @param credential {@link CommunicationTokenCredential}.
     */
    public ChatCompositeRemoteOptions(
            final ChatCompositeJoinLocator locator,
            final CommunicationIdentifier communicationIdentifier,
            final CommunicationTokenCredential credential) {
        this(locator, communicationIdentifier, credential, "", "", "", "");
    }

    /**
     * Create {@link ChatCompositeRemoteOptions}.
     * @param locator {@link ChatCompositeJoinLocator}
     * @param credential {@link CommunicationTokenCredential}
     * @param displayName                  User display name other call participants will see.
     * @param applicationID
     * @param sdkName
     * @param sdkVersion
     */
    public ChatCompositeRemoteOptions(
            final ChatCompositeJoinLocator locator,
            final CommunicationIdentifier communicationIdentifier,
            final CommunicationTokenCredential credential,
            final String displayName,
            final String applicationID,
            final String sdkName,
            final String sdkVersion) {
        this.communicationIdentifier = communicationIdentifier;
        this.credential = credential;
        this.displayName = displayName;
        this.locator = locator;
        this.applicationID = applicationID;
        this.sdkName = sdkName;
        this.sdkVersion = sdkVersion;
    }

    /**
     * Get {@link CommunicationTokenCredential}.
     *
     * @return {@link String}.
     */
    public CommunicationTokenCredential getCredential() {
        return credential;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public String getSdkName() {
        return sdkName;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    /**
     * Get call locator.
     *
     * @return {@link ChatCompositeJoinLocator}.
     */
    public ChatCompositeJoinLocator getLocator() {
        return locator;
    }

    public CommunicationIdentifier getCommunicationIdentifier() {
        return communicationIdentifier;
    }
}
