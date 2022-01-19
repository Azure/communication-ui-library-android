/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.teams.zoomable;

/**
 * Use this class to represent the size of the content
 */
public class ContentSize {
    private int mWidth;
    private int mHeight;
    private float mAppliedScale;

    public ContentSize(int width, int height, float appliedScale) {
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