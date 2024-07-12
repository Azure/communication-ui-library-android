// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallCompositeEventHandler;

public final class CallCompositeCustomButtonOptions {

    private int drawableId;
    private String title;
    private CallCompositeEventHandler<CallCompositeCustomButtonClickEvent> onClickHandler;
    private Boolean isVisible = true;
    private Boolean isEnabled = true;
    private CallCompositeCustomButtonPlacement placement = CallCompositeCustomButtonPlacement.OVERFLOW;

    public CallCompositeCustomButtonOptions(
            final int drawableId,
            final String title,
            final CallCompositeEventHandler<CallCompositeCustomButtonClickEvent> onClickHandler) {
        this.drawableId = drawableId;
        this.title = title;
        this.onClickHandler = onClickHandler;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public String getTitle() {
        return title;
    }

    public CallCompositeEventHandler<CallCompositeCustomButtonClickEvent> getOnClickHandler() {
        return onClickHandler;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public CallCompositeCustomButtonOptions setVisibility(final boolean isVisible) {
        this.isVisible = isVisible;
        return this;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public CallCompositeCustomButtonOptions setEnable(final boolean isEnabled) {
        this.isEnabled = isEnabled;
        return this;
    }

    public CallCompositeCustomButtonOptions setPlacement(final CallCompositeCustomButtonPlacement placement) {
        this.placement = placement;
        return this;
    }

    public CallCompositeCustomButtonPlacement getPlacement() {
        return this.placement;
    }
}
