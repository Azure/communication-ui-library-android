// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallCompositeEventHandler;

public final class CallCompositeCustomButtonOptions {

    private final int drawableId;
    private final String title;
    private final CallCompositeEventHandler<CallCompositeCustomButtonClickEvent> onClickHandler;
    private Boolean isVisible = true;
    private Boolean isEnabled = true;

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

    public CallCompositeCustomButtonOptions setVisible(final boolean isVisible) {
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
}
