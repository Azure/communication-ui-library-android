// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationTokenCredential;

public final class CallCompositePushNotificationOptions {

    private final CommunicationTokenCredential tokenCredential;
    private final String deviceRegistrationToken;
    private final String displayName;

    public CallCompositePushNotificationOptions(final CommunicationTokenCredential tokenCredential,
                                                final String deviceRegistrationToken,
                                                final String displayName) {
        this.tokenCredential = tokenCredential;
        this.deviceRegistrationToken = deviceRegistrationToken;
        this.displayName = displayName;
    }

    public String getDeviceRegistrationToken() {
        return deviceRegistrationToken;
    }

    public String getDisplayName() {
        return displayName;
    }

    public CommunicationTokenCredential getTokenCredential() {
        return tokenCredential;
    }
}
