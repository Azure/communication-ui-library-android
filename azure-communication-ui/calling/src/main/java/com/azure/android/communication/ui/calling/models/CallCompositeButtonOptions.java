// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallCompositeEventHandler;

public class CallCompositeButtonOptions {
    OnValueChangedListener<Integer> onDrawableIdChangedListener;
    OnValueChangedListener<String> onTitleChangedListener;
    OnValueChangedListener<Boolean> onVisibilityChangedListener;
    OnValueChangedListener<Boolean> onEnabledChangedListener;

    private int drawableId;
    private String title;
    private CallCompositeEventHandler<CallCompositeButtonClickEvent> onClickHandler;
    private Boolean isVisible = true;
    private Boolean isEnabled = true;

    private CallCompositeCustomButtonPlacement placement = CallCompositeCustomButtonPlacement.OVERFLOW;

    interface OnValueChangedListener<T> {
        void onValueChanged(T newValue);
    }

    public int getDrawableId() {
        return drawableId;
    }

    public CallCompositeButtonOptions setDrawableId(final int drawableId) {
        this.drawableId = drawableId;
        if (onDrawableIdChangedListener != null) {
            onDrawableIdChangedListener.onValueChanged(drawableId);
        }
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CallCompositeButtonOptions setTitle(final String title) {
        this.title = title;
        if (onTitleChangedListener != null) {
            onTitleChangedListener.onValueChanged(title);
        }
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

    public CallCompositeButtonOptions setPlacement(final CallCompositeCustomButtonPlacement placement) {
        this.placement = placement;
        return this;
    }

    public CallCompositeCustomButtonPlacement getPlacement() {
        return this.placement;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public CallCompositeButtonOptions setVisibility(final boolean isVisible) {
        this.isVisible = isVisible;
        if (onVisibilityChangedListener != null) {
            onVisibilityChangedListener.onValueChanged(isVisible);
        }
        return this;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public CallCompositeButtonOptions setEnable(final boolean isEnabled) {
        this.isEnabled = isEnabled;
        if (onEnabledChangedListener != null) {
            onEnabledChangedListener.onValueChanged(isVisible);
        }
        return this;
    }
}
