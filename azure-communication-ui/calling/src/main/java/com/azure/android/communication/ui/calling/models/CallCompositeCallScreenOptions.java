// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Options for the CallCompositeCallScreen.
 */
public final class CallCompositeCallScreenOptions {
    private CallCompositeCallScreenControlBarOptions controlBarOptions;
    private CallCompositeCallScreenHeaderOptions headerOptions;

    /**
     * Creates a CallCompositeCallScreenOptions object.
     */
    public CallCompositeCallScreenOptions() {
    }

    /**
     * Set the control bar options.
     * @param controlBarOptions The control bar options.
     * @return The {@link CallCompositeCallScreenOptions} object itself.
     */
    public CallCompositeCallScreenOptions setControlBarOptions(
            final CallCompositeCallScreenControlBarOptions controlBarOptions) {
        this.controlBarOptions = controlBarOptions;
        return this;
    }

    /**
     * Get the control bar options.
     * @return {@link CallCompositeCallScreenControlBarOptions}.
     */
    public CallCompositeCallScreenControlBarOptions getControlBarOptions() {
        return controlBarOptions;
    }

    /**
     * Set the header options.
     * @param headerOptions The header options.
     * @return The {@link CallCompositeCallScreenOptions} object itself.
     */
    public CallCompositeCallScreenOptions setHeaderOptions(
            final CallCompositeCallScreenHeaderOptions headerOptions) {
        this.headerOptions = headerOptions;
        return this;
    }

    /**
     * Get the header options.
     * @return {@link CallCompositeCallScreenHeaderOptions}.
     */
    public CallCompositeCallScreenHeaderOptions getHeaderOptions() {
        return headerOptions;
    }
}
