// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable;

/**
 * Use this class to represent the size of the content
 */
public class ContentSize {
    private final int mWidth;
    private final int mHeight;
    private final float mAppliedScale;

    public ContentSize(final int width, final int height, final float appliedScale) {
        this.mWidth = width;
        this.mHeight = height;
        this.mAppliedScale = appliedScale;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public float getScale() {
        return mAppliedScale;
    }
}
