// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallCompositeEventHandler;

/**
 * Call composite provided button view data.
 */
public class CallCompositeButtonViewData {
    CallCompositeEventHandler<Boolean> enabledChangedEventHandler;
    CallCompositeEventHandler<Boolean> visibleChangedEventHandler;
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
    public CallCompositeButtonViewData setOnClickHandler(
            final CallCompositeEventHandler<CallCompositeButtonClickEvent> handler) {
        this.onClickHandler = handler;
        return this;
    }

    /**
     * Get is visible.
     */
    public Boolean isVisible() {
        return this.isVisible;
    }

    /**
     * Set is visible.
     */
    public CallCompositeButtonViewData setVisible(final Boolean isVisible) {
        this.isVisible = isVisible;
        if (visibleChangedEventHandler != null) {
            visibleChangedEventHandler.handle(isVisible);
        }
        return this;
    }

    /**
     * Get is enabled.
     */
    public Boolean isEnabled() {
        return this.isEnabled;
    }

    /**
     * Set is enabled.
     */
    public CallCompositeButtonViewData setEnabled(final Boolean isEnabled) {
        this.isEnabled = isEnabled;
        if (enabledChangedEventHandler != null) {
            enabledChangedEventHandler.handle(isEnabled);
        }
        return this;
    }
}
