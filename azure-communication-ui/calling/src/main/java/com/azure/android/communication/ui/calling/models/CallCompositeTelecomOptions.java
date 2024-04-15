// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * CallCompositeTelecomOptions {@link CallCompositeTelecomOptions}.
 */
public class CallCompositeTelecomOptions {
    private final CallCompositeTelecomIntegration telecomIntegration;
    private String phoneAccountId;
    private Boolean resumeCallAutomatically;

    /**
     * Create {@link CallCompositeTelecomOptions}.
     *
     * @param telecomIntegration telecom integration.
     */
    public CallCompositeTelecomOptions(final CallCompositeTelecomIntegration telecomIntegration) {
        this.telecomIntegration = telecomIntegration;
    }


    /**
     *
     * @param phoneAccountId A string identifier that is unique across PhoneAccountHandles with the
     *                       same component name. Apps registering PhoneAccountHandles should ensure
     *                       that the ID provided does not expose personally identifying information.
     *                       A ConnectionService should use an opaque token as the PhoneAccountHandle identifier.
     *                       Note: Each String field is limited to 256 characters.
     * @return
     */
    public CallCompositeTelecomOptions setPhoneAccountId(final String phoneAccountId) {
        this.phoneAccountId = phoneAccountId;
        return this;
    }

    /**
     * Get telecom integration.
     *
     * @return {@link CallCompositeTelecomIntegration}
     */
    public CallCompositeTelecomIntegration getTelecomIntegration() {
        return telecomIntegration;
    }

    /**
     * Get phone account id.
     * @return {@link CallCompositeTelecomIntegration}
     */
    public String getPhoneAccountId() {
        return phoneAccountId;
    }


    public CallCompositeTelecomOptions setIsResumeCallAutomatically(final Boolean resumeCallAutomatically) {
        this.resumeCallAutomatically = resumeCallAutomatically;
        return this;
    }

    public Boolean isResumeCallAutomatically() {
        return resumeCallAutomatically;
    }
}
