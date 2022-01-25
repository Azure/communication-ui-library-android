// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable;

import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.LimitFlag;


/**
 * Tap gesture listener for double tap to zoom / unzoom and double-tap-and-drag to zoom.
 */
public class DoubleTapGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int DURATION_MS = 300;
    private static final int DOUBLE_TAP_SCROLL_THRESHOLD = 20;
    private final IZoomableControllerProvider mZoomableControllerProvider;
    private final PointF mDoubleTapViewPoint = new PointF();
    private final PointF mDoubleTapImagePoint = new PointF();
    private float mDoubleTapScale = 1;
    private boolean mDoubleTapScroll = false;

    public DoubleTapGestureListener(final IZoomableControllerProvider zoomableControllerProvider) {
        mZoomableControllerProvider = zoomableControllerProvider;
    }

    @Override
    public boolean onDoubleTapEvent(final MotionEvent e) {
        final AbstractAnimatedZoomableController zoomableController =
                (AbstractAnimatedZoomableController) mZoomableControllerProvider.getZoomableController();
        final PointF viewPoint = new PointF(e.getX(), e.getY());
        final PointF imagePoint = zoomableController.mapViewToImage(viewPoint);

        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDoubleTapViewPoint.set(viewPoint);
                mDoubleTapImagePoint.set(imagePoint);
                mDoubleTapScale = zoomableController.getScaleFactor();
                break;
            case MotionEvent.ACTION_MOVE:
                mDoubleTapScroll = mDoubleTapScroll || shouldStartDoubleTapScroll(viewPoint);
                if (mDoubleTapScroll) {
                    final float scale = calcScale(viewPoint);
                    zoomableController.zoomToPoint(scale, mDoubleTapImagePoint, mDoubleTapViewPoint);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mDoubleTapScroll) {
                    final float scale = calcScale(viewPoint);
                    zoomableController.zoomToPoint(scale, mDoubleTapImagePoint, mDoubleTapViewPoint);
                } else {
                    final float maxScale = zoomableController.getMaxScaleFactor();
                    final float minScale = zoomableController.getMinScaleFactor();
                    if (zoomableController.getScaleFactor() < (maxScale + minScale) / 2) {
                        zoomableController.zoomToPoint(
                                maxScale, imagePoint, viewPoint, LimitFlag.LIMIT_ALL, DURATION_MS, null);
                    } else {
                        zoomableController.zoomToPoint(
                                minScale, imagePoint, viewPoint, LimitFlag.LIMIT_ALL, DURATION_MS, null);
                    }
                }
                mDoubleTapScroll = false;
                break;
            default:
                return false;
        }
        return true;
    }

    private boolean shouldStartDoubleTapScroll(final PointF viewPoint) {
        final double dist =
                Math.hypot(viewPoint.x - mDoubleTapViewPoint.x, viewPoint.y - mDoubleTapViewPoint.y);
        return dist > DOUBLE_TAP_SCROLL_THRESHOLD;
    }

    private float calcScale(final PointF currentViewPoint) {
        final float dy = (currentViewPoint.y - mDoubleTapViewPoint.y);
        final float t = 1 + Math.abs(dy) * 0.001f;
        return (dy < 0) ? mDoubleTapScale / t : mDoubleTapScale * t;
    }
}
