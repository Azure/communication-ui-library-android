// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Options for the CallCompositeCallScreenControlBarOptions.
 */
public final class CallCompositeCallScreenControlBarOptions {
    final List<CallCompositeButtonOptions> customButtons = new ArrayList<>();

    private CallCompositeLeaveCallConfirmationMode leaveCallConfirmation =
            CallCompositeLeaveCallConfirmationMode.ALWAYS_ENABLED;

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
            final CallCompositeLeaveCallConfirmationMode leaveCallConfirmation) {
        this.leaveCallConfirmation = leaveCallConfirmation;
        return this;
    }

    /**
     * Get leave call confirmation.
     *
     * @return {@link CallCompositeLeaveCallConfirmationMode} The leave call confirmation.
     */
    public CallCompositeLeaveCallConfirmationMode getLeaveCallConfirmation() {
        return leaveCallConfirmation;
    }

    public CallCompositeCallScreenControlBarOptions addCustomButton(
            final CallCompositeButtonOptions buttonOptions) {
        customButtons.add(buttonOptions);
        return this;
    }

    public CallCompositeButtonOptions getCameraButton() {
        return null;
    }

    public CallCompositeButtonOptions getMicButton() {
        return null;
    }

}
