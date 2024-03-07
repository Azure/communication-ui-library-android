// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Options for the CallCompositeCallScreenControlBarOptions.
 */
public final class CallCompositeCallScreenControlBarOptions {
    private boolean hideLeaveCallConfirmDialog = false;

    /**
     * Create a CallCompositeCallScreenControlBarOptions object.
     */
    public CallCompositeCallScreenControlBarOptions() {
    }

    /**
     * Set the hideLeaveCallConfirmDialog.
     *
     * @param hideLeaveCallConfirmDialog The hideLeaveCallConfirmDialog to set.
     */
    public void setHideLeaveCallConfirmDialog(final boolean hideLeaveCallConfirmDialog) {
        this.hideLeaveCallConfirmDialog = hideLeaveCallConfirmDialog;
    }

    /**
     * Get the hideLeaveCallConfirmDialog.
     *
     * @return The hideLeaveCallConfirmDialog that is currently set.
     */
    public boolean getHideLeaveCallConfirmDialog() {
        return hideLeaveCallConfirmDialog;
    }
}
