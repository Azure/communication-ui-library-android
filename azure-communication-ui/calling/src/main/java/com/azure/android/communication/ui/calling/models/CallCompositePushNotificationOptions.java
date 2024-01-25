// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationTokenCredential;

/**
 * CallCompositePushNotificationOptions for registering device token to receive incoming call.
 */
final class CallCompositePushNotificationOptions {

    private final CommunicationTokenCredential tokenCredential;
    private final String deviceRegistrationToken;
    private final String displayName;

    /**
     * Create {@link CallCompositePushNotificationOptions}.
     *
     * @param tokenCredential          Token credential.
     * @param deviceRegistrationToken  Device registration token.
     * @param displayName              Display name.
     */
    CallCompositePushNotificationOptions(final CommunicationTokenCredential tokenCredential,
                                                final String deviceRegistrationToken,
                                                final String displayName) {
        this.tokenCredential = tokenCredential;
        this.deviceRegistrationToken = deviceRegistrationToken;
        this.displayName = displayName;
    }

    /**
     * Get device registration token.
     * @return device registration token.
     */
    String getDeviceRegistrationToken() {
        return deviceRegistrationToken;
    }

    /**
     * Get display name.
     * @return display name.
     */
    String getDisplayName() {
        return displayName;
    }

    /**
     * Get token credential.
     * @return token credential.
     */
    CommunicationTokenCredential getTokenCredential() {
        return tokenCredential;
    }
}
