// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * CallCompositeTelecomManagerOptions {@link CallCompositeTelecomManagerOptions}.
 */
public final class CallCompositeTelecomManagerOptions {
    private final CallCompositeTelecomManagerIntegration telecomIntegration;
    private String phoneAccountId;

    /**
     * Creates a CallCompositeTelecomManagerOptions.
     *
     * @param telecomIntegration The telecom integration.
     * @param phoneAccountId A string identifier that is unique across PhoneAccountHandles with the
     *                       same component name. Apps registering PhoneAccountHandles should ensure
     *                       that the ID provided does not expose personally identifying information.
     *                       A ConnectionService should use an opaque token as the PhoneAccountHandle identifier.
     *                       Note: Each String field is limited to 256 characters.
     */
    public CallCompositeTelecomManagerOptions(final CallCompositeTelecomManagerIntegration telecomIntegration,
                                              final String phoneAccountId) {
        this.telecomIntegration = telecomIntegration;
        this.phoneAccountId = phoneAccountId;
    }

    /**
     * Get telecom integration.
     *
     * @return {@link CallCompositeTelecomManagerIntegration}
     */
    public CallCompositeTelecomManagerIntegration getTelecomManagerIntegration() {
        return telecomIntegration;
    }

    /**
     * Get phone account id.
     *
     * @return phone account id.
     */
    public String getPhoneAccountId() {
        return phoneAccountId;
    }
}
