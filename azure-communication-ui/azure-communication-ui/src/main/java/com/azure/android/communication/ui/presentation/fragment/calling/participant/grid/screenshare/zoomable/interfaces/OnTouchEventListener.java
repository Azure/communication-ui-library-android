// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces;

/**
 * Listener that allows intercepting Taps or Double taps happening in a ZoomableFrameLayout
 */
public interface OnTouchEventListener {
    /***
     * @return True if it's handled.
     */
    boolean onTap();

    /***
     * @return True if it's handled.
     */
    boolean onDoubleTap();

    void onLongPress();

    /***
     * @return True if it's handled.
     */
    boolean onSwipeLeft();

    /***
     * @return True if it's handled.
     */
    boolean onSwipeRight();
}
