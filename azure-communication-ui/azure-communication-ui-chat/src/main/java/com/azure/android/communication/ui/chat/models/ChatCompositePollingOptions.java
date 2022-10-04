// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

// will not be a part of API on public branch (just to test polling now)
public final class ChatCompositePollingOptions {
    // in ms
    private Long pollingInterval = 20000L;

    public ChatCompositePollingOptions(
            final Long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public Long getPollingInterval() {
        return pollingInterval;
    }
}
