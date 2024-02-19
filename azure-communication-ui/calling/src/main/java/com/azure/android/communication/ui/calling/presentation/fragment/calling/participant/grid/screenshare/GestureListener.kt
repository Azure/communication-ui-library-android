// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.screenshare

import android.content.Context
import android.graphics.PointF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector

/**
 * The GestureListener detects click as well as scale and notify zoom frame layout to apply transformation
 * Used some parts of https://github.com/facebook/fresco zoomable to implement pinch and zoom
 */
internal class GestureListener(
    context: Context,
    private val gestureListenerEvents: GestureListenerEvents,
) : ScaleGestureDetector.OnScaleGestureListener,
    GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {
    companion object {
        private const val INVALID_POINTER = MotionEvent.INVALID_POINTER_ID
    }

    init {
        reset()
    }

    private val clickGestureDetector = GestureDetector(context, this)
    private var pointerStartPosition = PointF(0f, 0f)
    private var pointerCurrentPosition = PointF(0f, 0f)
    private var scaleFactor = 1.0f
    private var scaleGestureDetector = ScaleGestureDetector(context, this)
    private var validPointerIndex = INVALID_POINTER

    private var doubleTapZoomAnimationInProgress = false

    val scale get() = scaleFactor
    val startX get() = pointerStartPosition.x
    val startY get() = pointerStartPosition.y
    val translationX get() = pointerCurrentPosition.x - pointerStartPosition.x
    val translationY get() = pointerCurrentPosition.y - pointerStartPosition.y

    fun onTouchEvent(event: MotionEvent): Boolean {
        clickGestureDetector.onTouchEvent(event)
        scaleGestureDetector.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                updateGesture(event)
            }
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_UP,
            -> {
                numberOfPointersChanged(event)
            }
        }
        return true
    }

    fun resetPointers() {
        pointerStartPosition.x = pointerCurrentPosition.x
        pointerStartPosition.y = pointerCurrentPosition.y
        startGesture()
    }

    fun doubleTapZoomAnimationStarted() {
        doubleTapZoomAnimationInProgress = true
    }

    fun doubleTapZoomAnimationEnded() {
        doubleTapZoomAnimationInProgress = false
    }

    override fun onScale(scaleDetector: ScaleGestureDetector): Boolean {
        scaleFactor = scaleDetector.scaleFactor
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {}

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return true
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float,
    ): Boolean {
        return true
    }

    override fun onLongPress(e: MotionEvent) {}

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float,
    ): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        gestureListenerEvents.onSingleClick()
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        e?.let { gestureListenerEvents.onDoubleClick(it) }
        return true
    }

    private fun reset() {
        validPointerIndex = INVALID_POINTER
    }

    private fun updateGesture(event: MotionEvent) {
        if (doubleTapZoomAnimationInProgress) {
            return
        }
        updatePointersOnMove(event)
        if (validPointerIndex != INVALID_POINTER) {
            startGesture()
        }
        gestureListenerEvents.updateTransformation()
    }

    private fun numberOfPointersChanged(event: MotionEvent) {
        if (doubleTapZoomAnimationInProgress) {
            return
        }
        updatePointersOnTap(event)
        if (validPointerIndex != INVALID_POINTER) {
            startGesture()
        }
    }

    private fun startGesture() {
        gestureListenerEvents.initTransformation()
    }

    private fun getPressedPointerIndex(event: MotionEvent): Int {
        val count = event.pointerCount
        val action = event.actionMasked
        val index = event.actionIndex
        var temp = 0
        if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) && temp >= index) {
            temp++
        }
        return if (temp < count) temp else INVALID_POINTER
    }

    private fun updatePointersOnTap(event: MotionEvent) {
        val index = getPressedPointerIndex(event)
        if (index != INVALID_POINTER) {
            validPointerIndex = event.getPointerId(index)
            pointerStartPosition.x = event.getX(index)
            pointerStartPosition.y = event.getY(index)
        } else {
            validPointerIndex = INVALID_POINTER
        }
    }

    private fun updatePointersOnMove(event: MotionEvent) {
        val index = event.findPointerIndex(validPointerIndex)
        if (index != INVALID_POINTER) {
            pointerCurrentPosition.x = event.getX(index)
            pointerCurrentPosition.y = event.getY(index)
        }
    }
}
