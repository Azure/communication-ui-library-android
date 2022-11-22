// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * A Call Composite Debug information.
 */
public final class CallCompositeDebugInfo {

    private String lastKnownCallId;

    CallCompositeDebugInfo() { }

    /**
     * Set last known call id.
     * @param lastKnownCallId last known call id.
     * @return {@link CallCompositeDebugInfo}
     */
    CallCompositeDebugInfo setLastKnownCallId(final String lastKnownCallId) {
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
