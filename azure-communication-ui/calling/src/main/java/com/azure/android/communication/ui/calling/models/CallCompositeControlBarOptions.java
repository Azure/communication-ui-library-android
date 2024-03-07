// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Options for the CallCompositeControlBar.
 */
public final class CallCompositeControlBarOptions {
    private boolean hideLeaveCallConfirmDialog = false;

    /**
     * Create CallCompositeControlBarOptions.
     *
     * @param hideLeaveCallConfirmDialog The hideLeaveCallConfirmDialog.
     */
    public CallCompositeControlBarOptions(final boolean hideLeaveCallConfirmDialog) {
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
