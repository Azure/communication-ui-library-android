// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Options for the CallCompositeCallScreenHeaderOptions.
 */
public class CallCompositeCallScreenHeaderOptions {
    private CallCompositeCallDurationCustomTimer customTimer;
    private String customTitle;

    /**
     * Create a CallCompositeCallScreenHeaderOptions object.
     */
    public CallCompositeCallScreenHeaderOptions() {
    }

    /**
     * Set the custom timer.
     *
     * @param customTimer The custom timer.
     * @return The {@link CallCompositeCallScreenHeaderOptions} object itself.
     */
    public CallCompositeCallScreenHeaderOptions setCustomTimer(
            final CallCompositeCallDurationCustomTimer customTimer) {
        this.customTimer = customTimer;
        return this;
    }

    /**
     * Get the custom timer.
     *
     * @return {@link CallCompositeCallDurationCustomTimer} The custom timer.
     */
    public CallCompositeCallDurationCustomTimer getCustomTimer() {
        return customTimer;
    }

    /**
     * Set the custom title.
     *
     * @param customTitle The custom title.
     * @return The {@link CallCompositeCallScreenHeaderOptions} object itself.
     */
    public CallCompositeCallScreenHeaderOptions setCustomTitle(
            final String customTitle) {
        this.customTitle = customTitle;
        return this;
    }

    /**
     * Get the custom title.
     *
     * @return {@link String} The custom title.
     */
    public String getCustomTitle() {
        return customTitle;
    }
}
