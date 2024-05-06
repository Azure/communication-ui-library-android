// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Options for the CallCompositeCallScreen.
 */
public final class CallCompositeCallScreenOptions {
    private CallCompositeCallScreenControlBarOptions controlBarOptions;

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
}
