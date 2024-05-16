// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * CallCompositeTelecomManagerOptions {@link CallCompositeTelecomManagerOptions}.
 */
public final class CallCompositeTelecomManagerOptions {
    private final CallCompositeTelecomManagerIntegrationMode telecomIntegration;
    private final String phoneAccountId;

    /**
     * Creates a CallCompositeTelecomManagerOptions.
     *
     * @param mode           {@link CallCompositeTelecomManagerIntegrationMode}
     * @param phoneAccountId A string identifier that is unique across PhoneAccountHandles with the
     *                       same component name. Apps registering PhoneAccountHandles should ensure
     *                       that the ID provided does not expose personally identifying information.
     *                       A ConnectionService should use an opaque token as the PhoneAccountHandle identifier.
     *                       Note: Each String field is limited to 256 characters.
     * <p>
     *    The phoneAccountId is required for USE_SDK_PROVIDED_TELECOM_MANAGER mode.
     * </p>
     */
    public CallCompositeTelecomManagerOptions(final CallCompositeTelecomManagerIntegrationMode mode,
                                              final String phoneAccountId) {
        if (mode == CallCompositeTelecomManagerIntegrationMode.SDK_PROVIDED_TELECOM_MANAGER
                && phoneAccountId == null) {
            throw new
                    IllegalArgumentException("Phone account id is required for USE_SDK_PROVIDED_TELECOM_MANAGER mode.");
        }
        this.telecomIntegration = mode;
        this.phoneAccountId = phoneAccountId;
    }


    /**
     * Creates a CallCompositeTelecomManagerOptions.
     *
     * @param mode {@link CallCompositeTelecomManagerIntegrationMode}
     * <p>
     *   The phoneAccountId is required for USE_SDK_PROVIDED_TELECOM_MANAGER mode.
     * </p>
     */
    public CallCompositeTelecomManagerOptions(final CallCompositeTelecomManagerIntegrationMode mode) {
        this(mode, null);
    }

    /**
     * Get telecom integration.
     *
     * @return {@link CallCompositeTelecomManagerIntegrationMode}
     */
    public CallCompositeTelecomManagerIntegrationMode getTelecomManagerIntegrationMode() {
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
