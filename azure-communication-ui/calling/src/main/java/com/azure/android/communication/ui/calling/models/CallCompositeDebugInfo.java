// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * A Call Composite Debug information.
 */
public final class CallCompositeDebugInfo {

    private String lastCallId;

    CallCompositeDebugInfo() { }

    /**
     * Set last call id.
     * @param lastCallId last call id.
     * @return {@link CallCompositeDebugInfo}
     */
    CallCompositeDebugInfo setLastCallId(final String lastCallId) {
        this.lastCallId = lastCallId;
        return this;
    }

    /**
     * Get last call id.
     * @return {@link String}
     */
    public String getLastCallId() {
        return lastCallId;
    }
}
