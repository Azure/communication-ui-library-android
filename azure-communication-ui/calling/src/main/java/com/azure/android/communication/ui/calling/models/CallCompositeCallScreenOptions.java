// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Options for the CallCompositeCallScreen.
 */
public final class CallCompositeCallScreenOptions {
    private CallCompositeCallScreenControlBarOptions controlBarOptions;
    /* <CUSTOM_CALL_HEADER> */
    private CallCompositeCallScreenHeaderViewData headerOptions;
    /* </CUSTOM_CALL_HEADER> */
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
    /* <CUSTOM_CALL_HEADER> */
    /**
     * Set the header options.
     * @param headerOptions The header options.
     * @return The {@link CallCompositeCallScreenOptions} object itself.
     */
    public CallCompositeCallScreenOptions setHeaderOptions(
            final CallCompositeCallScreenHeaderViewData headerOptions) {
        this.headerOptions = headerOptions;
        return this;
    }

    /**
     * Get the header options.
     * @return {@link CallCompositeCallScreenHeaderViewData}.
     */
    public CallCompositeCallScreenHeaderViewData getHeaderOptions() {
        return headerOptions;
    }
    /* </CUSTOM_CALL_HEADER> */
}
