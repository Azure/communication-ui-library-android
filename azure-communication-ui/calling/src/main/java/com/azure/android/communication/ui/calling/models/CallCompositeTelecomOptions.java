// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * CallCompositeTelecomOptions {@link CallCompositeTelecomOptions}.
 */
class CallCompositeTelecomOptions {
    private CallCompositeTelecomIntegration telecomIntegration;

    /**
     * Create {@link CallCompositeTelecomOptions}.
     *
     * @param telecomIntegration telecom integration.
     */
    CallCompositeTelecomOptions(final CallCompositeTelecomIntegration telecomIntegration) {
        this.telecomIntegration = telecomIntegration;
    }

    /**
     * Get telecom integration.
     *
     * @return {@link CallCompositeTelecomIntegration}
     */
    CallCompositeTelecomIntegration getTelecomIntegration() {
        return telecomIntegration;
    }
}
