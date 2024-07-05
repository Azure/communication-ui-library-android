// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

public class CallCompositeButtonOptions {
    OnValueChangedListener<Integer> onDrawableIdChangedListener;
    OnValueChangedListener<String> onTitleChangedListener;
    OnValueChangedListener<Boolean> onVisibilityChangedListener;
    OnValueChangedListener<Boolean> onEnabledChangedListener;

    private int drawableId;
    private String title;
    private OnClickListener onClickListener;
    private Boolean isVisible = true;
    private Boolean isEnabled = true;

    private int order = 0;

    interface OnValueChangedListener<T> {
        void onValueChanged(T newValue);
    }

    public interface OnClickListener {
        /**
         * Called when a button has been clicked.
         */
        void onClick(CallCompositeButtonOptions buttonOptions);
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

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public CallCompositeButtonOptions setOnClickListener(final OnClickListener listener) {
        this.onClickListener = listener;
        return this;
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

    public int getOrder() {
        return order;
    }

    public CallCompositeButtonOptions setOrder(final int order) {
        this.order = order;
        return this;
    }
}
