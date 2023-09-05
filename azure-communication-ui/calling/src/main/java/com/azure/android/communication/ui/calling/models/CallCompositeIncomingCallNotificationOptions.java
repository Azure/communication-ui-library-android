// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationTokenCredential;

public final class CallCompositeIncomingCallNotificationOptions {
    private final CommunicationTokenCredential credential;
    private final String deviceToken;

    public CallCompositeIncomingCallNotificationOptions(
            final CommunicationTokenCredential credential,
            final String deviceToken) {
        this.credential = credential;
        this.deviceToken = deviceToken;
    }

    public CommunicationTokenCredential getCredential() {
        return credential;
    }

    public String getDeviceToken() {
        return deviceToken;
    }
}
