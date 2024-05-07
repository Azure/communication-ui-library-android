// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * CallCompositeTelecomManagerOptions {@link CallCompositeTelecomManagerOptions}.
 */
public final class CallCompositeTelecomManagerOptions {
    private final CallCompositeTelecomManagerIntegrationMode telecomIntegration;
    private String phoneAccountId;

    /**
     * Creates a CallCompositeTelecomManagerOptions.
     *
     * <p>
     *     The telecom integration is set to
     *     {@link CallCompositeTelecomManagerIntegrationMode#USE_SDK_PROVIDED_TELECOM_MANAGER}.
     *     The phone account id is set to the provided phoneAccountId.
     *     The telecom manager will be managed by the SDK.
     * </p>
     * @param phoneAccountId A string identifier that is unique across PhoneAccountHandles with the
     *                       same component name. Apps registering PhoneAccountHandles should ensure
     *                       that the ID provided does not expose personally identifying information.
     *                       A ConnectionService should use an opaque token as the PhoneAccountHandle identifier.
     *                       Note: Each String field is limited to 256 characters.
     */
    public CallCompositeTelecomManagerOptions(final String phoneAccountId) {
        this.telecomIntegration = CallCompositeTelecomManagerIntegrationMode.USE_SDK_PROVIDED_TELECOM_MANAGER;
        this.phoneAccountId = phoneAccountId;
    }

    /**
     * Creates a CallCompositeTelecomManagerOptions.
     * <p>
     *     The telecom integration is set to
     *     {@link CallCompositeTelecomManagerIntegrationMode#APPLICATION_IMPLEMENTED_TELECOM_MANAGER}.
     *     The phone account id is set to null.
     *     The telecom manager will be managed by the application.
     * </p>
     */
    public CallCompositeTelecomManagerOptions() {
        this.telecomIntegration = CallCompositeTelecomManagerIntegrationMode.APPLICATION_IMPLEMENTED_TELECOM_MANAGER;
        this.phoneAccountId = null;
    }

    /**
     * Get telecom integration.
     *
     * @return {@link CallCompositeTelecomManagerIntegrationMode}
     */
    public CallCompositeTelecomManagerIntegrationMode getTelecomManagerIntegration() {
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
