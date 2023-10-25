// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationTokenCredential;

public final class CallCompositePushNotificationOptions {

    public CommunicationTokenCredential tokenCredential;
    public String deviceRegistrationToken;

    public String displayName;

    public CallCompositePushNotificationOptions(CommunicationTokenCredential tokenCredential, String deviceRegistrationToken, String displayName) {
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
