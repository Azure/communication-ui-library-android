// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallCompositeEventHandler;

public class CallCompositeButtonOptions {
    private final int drawableId;
    private final String title;
    private final CallCompositeEventHandler<CallCompositeButtonClickEvent> onClickHandler;
    private Boolean isVisible = true;
    private Boolean isEnabled = true;

    public CallCompositeButtonOptions(final int drawableId,
                                      final String title,
                                      final CallCompositeEventHandler<CallCompositeButtonClickEvent> onClickHandler) {

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

    public CallCompositeEventHandler<CallCompositeButtonClickEvent> getOnClickHandler() {
        return onClickHandler;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public CallCompositeButtonOptions setVisibility(final boolean isVisible) {
        this.isVisible = isVisible;
        return this;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public CallCompositeButtonOptions setEnable(final boolean isEnabled) {
        this.isEnabled = isEnabled;
        return this;
    }
}
