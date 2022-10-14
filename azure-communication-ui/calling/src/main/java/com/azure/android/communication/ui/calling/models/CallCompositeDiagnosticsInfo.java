// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * A Call Composite Diagnostics information.
 */
public class CallCompositeDiagnosticsInfo {

    private String lastKnownCallId;

    public CallCompositeDiagnosticsInfo setLastKnownCallId(final String lastKnownCallId) {
        this.lastKnownCallId = lastKnownCallId;
        return this;
    }

    public String getLastKnownCallId() {
        return lastKnownCallId;
    }
}
