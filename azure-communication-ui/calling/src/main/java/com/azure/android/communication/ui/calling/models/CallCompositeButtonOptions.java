// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallCompositeEventHandler;

/**
 * Call composite provided button options.
 */
public class CallCompositeButtonOptions {
    private CallCompositeEventHandler<CallCompositeButtonClickEvent> onClickHandler;
    private Boolean isVisible = true;
    private Boolean isEnabled = true;

    /**
     * Get button on click handler.
     */
    public CallCompositeEventHandler<CallCompositeButtonClickEvent> getOnClickHandler() {
        return onClickHandler;
    }

    /**
     * Set button on click handler.
     * @param handler {@link CallCompositeEventHandler}
     */
    public CallCompositeButtonOptions setOnClickHandler(
            final CallCompositeEventHandler<CallCompositeButtonClickEvent> handler) {
        this.onClickHandler = handler;
        return this;
    }

    /**
     * Get is visible.
     */
    public boolean isVisible() {
        return this.isVisible;
    }

    /**
     * Set is visible.
     */
    public CallCompositeButtonOptions setVisible(final boolean isVisible) {
        this.isVisible = isVisible;
        return this;
    }

    /**
     * Get is enabled.
     */
    public boolean isEnabled() {
        return this.isEnabled;
    }

    /**
     * Set is enabled.
     */
    public CallCompositeButtonOptions setEnabled(final boolean isEnabled) {
        this.isEnabled = isEnabled;
        return this;
    }
}
