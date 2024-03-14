// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Options for the CallCompositeCallScreenControlBarOptions.
 */
public final class CallCompositeCallScreenControlBarOptions {
    private boolean disableLeaveCallConfirmation = false;

    /**
     * Create a CallCompositeCallScreenControlBarOptions object.
     */
    public CallCompositeCallScreenControlBarOptions() {
    }

    /**
     * Set the disableLeaveCallConfirmation.
     *
     * @param disableLeaveCallConfirmation The disableLeaveCallConfirmation to set.
     */
    public CallCompositeCallScreenControlBarOptions setDisableLeaveCallConfirmation(
            final boolean disableLeaveCallConfirmation) {
        this.disableLeaveCallConfirmation = disableLeaveCallConfirmation;
        return this;
    }

    /**
     * Is disableLeaveCallConfirmation.
     *
     * @return The disableLeaveCallConfirmation that is currently set.
     */
    public boolean isDisableLeaveCallConfirmation() {
        return disableLeaveCallConfirmation;
    }
}
