// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * CallCompositeTelecomOptions {@link CallCompositeTelecomOptions}.
 */
public class CallCompositeTelecomOptions {
    private final CallCompositeTelecomIntegration telecomIntegration;

    /**
     * Create {@link CallCompositeTelecomOptions}.
     *
     * @param telecomIntegration telecom integration.
     */
    public CallCompositeTelecomOptions(final CallCompositeTelecomIntegration telecomIntegration) {
        this.telecomIntegration = telecomIntegration;
    }

    /**
     * Get telecom integration.
     *
     * @return {@link CallCompositeTelecomIntegration}
     */
    public CallCompositeTelecomIntegration getTelecomIntegration() {
        return telecomIntegration;
    }
}
