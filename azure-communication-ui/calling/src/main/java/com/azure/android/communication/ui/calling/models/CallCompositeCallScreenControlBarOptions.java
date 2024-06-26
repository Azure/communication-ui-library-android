// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Options for the CallCompositeCallScreenControlBarOptions.
 */
public final class CallCompositeCallScreenControlBarOptions {
    private CallCompositeLeaveCallConfirmationMode leaveCallConfirmation =
            CallCompositeLeaveCallConfirmationMode.ALWAYS_ENABLED;
    private CallCompositeCaptionsVisibilityMode captionsVisibilityMode =
            CallCompositeCaptionsVisibilityMode.HIDE;

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

    /**
     * Set captions visibility mode.
     *
     * @param captionsVisibilityMode The captions visibility mode.
     * @return The {@link CallCompositeCallScreenControlBarOptions} object itself.
     */
    public CallCompositeCallScreenControlBarOptions setCaptionsVisibilityMode(
            final CallCompositeCaptionsVisibilityMode captionsVisibilityMode) {
        this.captionsVisibilityMode = captionsVisibilityMode;
        return this;
    }

    /**
     * Get captions visibility mode.
     *
     * @return {@link CallCompositeCaptionsVisibilityMode} The captions visibility mode.
     */
    public CallCompositeCaptionsVisibilityMode getCaptionsVisibilityMode() {
        return captionsVisibilityMode;
    }
}
