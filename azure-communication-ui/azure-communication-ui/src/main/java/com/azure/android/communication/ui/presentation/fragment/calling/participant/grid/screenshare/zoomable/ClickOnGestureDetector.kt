// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable

import android.view.GestureDetector
import android.view.MotionEvent

internal class ClickOnGestureDetector : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private var onSingleClick: (() -> Unit)? = null
    private var onDoubleClick: ((motionEvent: MotionEvent?) -> Unit)? = null

    fun setShowFloatingHeaderCallBack(onSingleClick: () -> Unit, onDoubleClick: (motionEvent: MotionEvent?) -> Unit) {
        this.onSingleClick = onSingleClick
        this.onDoubleClick = onDoubleClick
    }

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

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float,
    ): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(motionEvent: MotionEvent?): Boolean {
        onSingleClick?.let {
            it()
        }
        return true
    }

    override fun onDoubleTap(motionEvent: MotionEvent?): Boolean {
        onDoubleClick?.let {
            it(motionEvent)
        }
        return true
    }

    override fun onDoubleTapEvent(motionEvent: MotionEvent?): Boolean {
        return true
    }
}
