// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * A Call Composite Diagnostics information.
 */
public class CallCompositeDiagnosticsInfo {

    private String lastKnownCallId;

    /**
     * Set last known call id.
     * @param lastKnownCallId last known call id.
     * @return {@link CallCompositeDiagnosticsInfo}
     */
    CallCompositeDiagnosticsInfo setLastKnownCallId(final String lastKnownCallId) {
        this.lastKnownCallId = lastKnownCallId;
        return this;
    }

    /**
     * Get last known call id.
     * @return {@link String}
     */
    public String getLastKnownCallId() {
        return lastKnownCallId;
    }
}
