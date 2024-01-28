// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationTokenCredential;

/**
 * CallCompositePushNotificationOptions for registering device token to receive incoming call.
 */
public final class CallCompositePushNotificationOptions {
    private final CommunicationTokenCredential credential;
    private final String deviceRegistrationToken;
    private final String displayName;

    /**
     * Create {@link CallCompositePushNotificationOptions}.
     *
     * @param credential {@link CommunicationTokenCredential}.
     * @param deviceRegistrationToken device registration token.
     * @param displayName display name.
     */
    public CallCompositePushNotificationOptions(final CommunicationTokenCredential credential,
                                                final String deviceRegistrationToken,
                                                final String displayName) {
        this.credential = credential;
        this.deviceRegistrationToken = deviceRegistrationToken;
        this.displayName = displayName;
    }

    /**
     * Create {@link CallCompositePushNotificationOptions}.
     *
     * @param credential {@link CommunicationTokenCredential}.
     * @param deviceRegistrationToken device registration token.
     */
    public CallCompositePushNotificationOptions(final CommunicationTokenCredential credential,
                                                final String deviceRegistrationToken) {
        this.credential = credential;
        this.deviceRegistrationToken = deviceRegistrationToken;
        this.displayName = "";
    }

    /**
     * Get device registration token.
     * @return device registration token.
     */
    public String getDeviceRegistrationToken() {
        return deviceRegistrationToken;
    }

    /**
     * Get display name.
     * @return display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get token credential.
     * @return token credential.
     */
    public CommunicationTokenCredential getCredential() {
        return credential;
    }
}
