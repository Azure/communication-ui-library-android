// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Options for the CallCompositeCallScreenControlBarOptions.
 */
public final class CallCompositeCallScreenControlBarOptions {
    private CallCompositeLeaveCallConfirmation leaveCallConfirmation = CallCompositeLeaveCallConfirmation.ENABLE;

    /**
     * Create a CallCompositeCallScreenControlBarOptions object.
     */
    public CallCompositeCallScreenControlBarOptions() {
    }

    /**
     * Set leave call confirmation.
     *
     * @param leaveCallConfirmation The leave call confirmation.
     * @return The {@link CallCompositeCallScreenControlBarOptions} object itself.
     */
    public CallCompositeCallScreenControlBarOptions setLeaveCallConfirmation(
            final CallCompositeLeaveCallConfirmation leaveCallConfirmation) {
        this.leaveCallConfirmation = leaveCallConfirmation;
        return this;
    }

    /**
     * Get leave call confirmation.
     *
     * @return {@link CallCompositeLeaveCallConfirmation} The leave call confirmation.
     */
    public CallCompositeLeaveCallConfirmation getLeaveCallConfirmation() {
        return leaveCallConfirmation;
    }
}
