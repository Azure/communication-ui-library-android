// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallCompositeEventHandler;

/**
 * Call composite custom button options.
 */
public final class CallCompositeCustomButtonOptions {

    private final int drawableId;
    private final String title;
    private final CallCompositeEventHandler<CallCompositeCustomButtonClickEvent> onClickHandler;
    private Boolean isVisible = true;
    private Boolean isEnabled = true;

    /**
     * Create call composite custom button options.
     * @param drawableId an icon for the button.
     * @param title a text to be displayed on the button.
     * @param onClickHandler button on click handler.
     */
    public CallCompositeCustomButtonOptions(
            final int drawableId,
            final String title,
            final CallCompositeEventHandler<CallCompositeCustomButtonClickEvent> onClickHandler) {
        this.drawableId = drawableId;
        this.title = title;
        this.onClickHandler = onClickHandler;
    }

    /**
     * Get an icon for the button.
     */
    public int getDrawableId() {
        return drawableId;
    }

    /**
     * Get an title for the button.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get an on click handler for the button.
     */
    public CallCompositeEventHandler<CallCompositeCustomButtonClickEvent> getOnClickHandler() {
        return onClickHandler;
    }

    /**
     * Get isVisible property.
     */
    public boolean isVisible() {
        return this.isVisible;
    }

    /**
     * Set isVisible property.
     */
    public CallCompositeCustomButtonOptions setVisible(final boolean isVisible) {
        this.isVisible = isVisible;
        return this;
    }

    /**
     * Get isEnabled property.
     */
    public boolean isEnabled() {
        return this.isEnabled;
    }

    /**
     * Set isEnabled property.
     */
    public CallCompositeCustomButtonOptions setEnable(final boolean isEnabled) {
        this.isEnabled = isEnabled;
        return this;
    }
}
