// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces;

import androidx.annotation.IntRange;

/**
 * interface that describes the interactions possible with a container that supports zooming
 */
public interface ZoomableContentListener {
    void setScaleType(@ZoomScaleType int scaleType);

    void onContentReady(@IntRange(from = 0) int width, @IntRange(from = 0) int height,
                        int contentViewWidth, int contentViewHeight, boolean forceReset);

    void onContentReady(@IntRange(from = 0) int width, @IntRange(from = 0) int height, boolean forceReset);

    void resetTransformations();

    void disableInteractions();

    void enableInteractions();
}
