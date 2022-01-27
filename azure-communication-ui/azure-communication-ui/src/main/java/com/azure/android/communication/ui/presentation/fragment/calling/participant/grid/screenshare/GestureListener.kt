// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare

import android.content.Context
import android.graphics.PointF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector

internal class GestureListener(
    context: Context,
    private val gestureListenerEvents: GestureListenerEvents,
) : ScaleGestureDetector.OnScaleGestureListener,
    GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {

    private var pointerStartPosition = PointF(0f, 0f)
    private var pointerCurrentPosition = PointF(0f, 0f)
    private var scaleFactor = 1.0f
    private var scaleGestureDetector = ScaleGestureDetector(context, this)
    private val clickGestureDetector = GestureDetector(context, this)

    val scale get() = scaleFactor
    val startX get() = pointerStartPosition.x
    val startY get() = pointerStartPosition.y
    val translationX get() = pointerCurrentPosition.x - pointerStartPosition.x
    val translationY get() = pointerCurrentPosition.y - pointerStartPosition.y

    fun onTouchEvent(event: MotionEvent): Boolean {
        clickGestureDetector.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                pointerCurrentPosition.x = event.getX(0)
                pointerCurrentPosition.y = event.getY(0)
                gestureListenerEvents.updateTransformation()
            }
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_UP,
            -> {
                pointerStartPosition.x = event.getX(0)
                pointerStartPosition.y = event.getY(0)
                gestureListenerEvents.initTransformation()
            }
        }
        scaleGestureDetector.onTouchEvent(event)
        return true
    }

    fun resetPointers() {
        pointerStartPosition.x = pointerCurrentPosition.x
        pointerStartPosition.y = pointerCurrentPosition.y
        gestureListenerEvents.initTransformation()
    }

    override fun onScale(scaleDetector: ScaleGestureDetector): Boolean {
        scaleFactor = scaleDetector.scaleFactor
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {}

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return true
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float,
    ): Boolean {
        return true
    }

    override fun onLongPress(e: MotionEvent?) {}

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float,
    ): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        gestureListenerEvents.onSingleClick()
        return true
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        e?.let { gestureListenerEvents.onDoubleClick(it) }
        return true
    }
}
