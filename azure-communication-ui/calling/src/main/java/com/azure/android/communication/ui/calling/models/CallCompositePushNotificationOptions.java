// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationTokenCredential;

/**
 * Call composite push notification options to register token.
 */
public final class CallCompositePushNotificationOptions {

    private final CommunicationTokenCredential tokenCredential;
    private final String deviceRegistrationToken;
    private final String displayName;

    /**
     * Creates {@link CallCompositePushNotificationOptions}.
     * @param tokenCredential Token credential {@link CommunicationTokenCredential}.
     * @param deviceRegistrationToken Device registration token received from push notification server.
     * @param displayName Display name.
     */
    public CallCompositePushNotificationOptions(final CommunicationTokenCredential tokenCredential,
                                                final String deviceRegistrationToken,
                                                final String displayName) {
        this.tokenCredential = tokenCredential;
        this.deviceRegistrationToken = deviceRegistrationToken;
        this.displayName = displayName;
    }

    /**
     * Get device registration token.
     * @return Device registration token.
     */
    public String getDeviceRegistrationToken() {
        return deviceRegistrationToken;
    }

    /**
     * Get display name.
     * @return Display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get token credential.
     * @return Token credential.
     */
    public CommunicationTokenCredential getTokenCredential() {
        return tokenCredential;
    }
}
