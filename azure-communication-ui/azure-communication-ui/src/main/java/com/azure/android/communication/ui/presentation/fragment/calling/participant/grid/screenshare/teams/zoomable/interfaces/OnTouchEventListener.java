package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.teams.zoomable.interfaces;

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