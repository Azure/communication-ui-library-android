// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * CallCompositeTelecomManagerOptions {@link CallCompositeTelecomManagerOptions}.
 */
public final class CallCompositeTelecomManagerOptions {
    private final CallCompositeTelecomManagerIntegration telecomIntegration;

    /**
     * Create {@link CallCompositeTelecomManagerOptions}.
     *
     * @param telecomIntegration telecom integration.
     */
    public CallCompositeTelecomManagerOptions(final CallCompositeTelecomManagerIntegration telecomIntegration) {
        this.telecomIntegration = telecomIntegration;
    }

    /**
     * Get telecom integration.
     *
     * @return {@link CallCompositeTelecomManagerIntegration}
     */
    public CallCompositeTelecomManagerIntegration getTelecomManagerIntegration() {
        return telecomIntegration;
    }
}
