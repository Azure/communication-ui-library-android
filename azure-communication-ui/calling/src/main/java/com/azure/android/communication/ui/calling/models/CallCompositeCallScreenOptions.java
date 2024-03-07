// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Options for the CallCompositeCallScreen.
 */
public final class CallCompositeCallScreenOptions {
    private final CallCompositeCallScreenControlBarOptions controlBarOptions;

    /**
     * Creates a CallCompositeCallScreenOptions object.
     * @param controlBarOptions The control bar options.
     */
    public CallCompositeCallScreenOptions(final CallCompositeCallScreenControlBarOptions controlBarOptions) {
        this.controlBarOptions = controlBarOptions;
    }

    /**
     * Get the control bar options.
     * @return {@link CallCompositeCallScreenControlBarOptions}.
     */
    public CallCompositeCallScreenControlBarOptions getControlBarOptions() {
        return controlBarOptions;
    }
}
