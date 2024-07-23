// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallCompositeEventHandler;

public class CallCompositeButtonOptions {
    private Integer drawableId = null;
    private String title;
    private CallCompositeEventHandler<CallCompositeButtonClickEvent> onClickHandler;
    private Boolean isVisible = true;
    private Boolean isEnabled = true;

    public Integer getDrawableId() {
        return drawableId;
    }

    public CallCompositeButtonOptions setDrawableId(final Integer drawableId) {
        this.drawableId = drawableId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CallCompositeButtonOptions setTitle(final String title) {
        this.title = title;
        return this;
    }

    public CallCompositeEventHandler<CallCompositeButtonClickEvent> getOnClickHandler() {
        return onClickHandler;
    }

    public CallCompositeButtonOptions setOnClickHandler(
            final CallCompositeEventHandler<CallCompositeButtonClickEvent> handler) {
        this.onClickHandler = handler;
        return this;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public CallCompositeButtonOptions setVisible(final boolean isVisible) {
        this.isVisible = isVisible;
        return this;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public CallCompositeButtonOptions setEnabled(final boolean isEnabled) {
        this.isEnabled = isEnabled;
        return this;
    }
}
