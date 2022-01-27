// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * Frame layout with pinch and zoom capabilities
 * The layout detects click for displaying participant header and double click zoom
 */
internal class ScreenShareZoomFrameLayout :
    FrameLayout,
    GestureListenerEvents {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    companion object {
        private const val MIN_SCALE = 1.0f
        private const val MAX_SCALE = 4.0f
    }

    private val gestureListener = GestureListener(context, this)
    private lateinit var showFloatingHeaderCallBack: () -> Unit
    private var currentScale = 0f

    private val previousTransform = Matrix()
    private val newTransform = Matrix()
    private var currentTransform = Matrix()

    private val screenShareViewBounds = RectF()
    private val zoomFrameViewBounds = RectF()

    fun setFloatingHeaderCallback(showFloatingHeaderCallBack: () -> Unit) {
        this.showFloatingHeaderCallBack = showFloatingHeaderCallBack
    }

    override fun onSingleClick() {
        showFloatingHeaderCallBack()
    }

    override fun onDoubleClick(motionEvent: MotionEvent) {
    }

    override fun initTransformation() {
        previousTransform.set(newTransform)
    }

    override fun updateTransformation() {
        val transformCorrected = applyTransform(newTransform)
        onTransformChanged()
        if (transformCorrected) {
            gestureListener.resetPointers()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gestureListener.onTouchEvent(event)) {
            parent.requestDisallowInterceptTouchEvent(true)
            return true
        }
        if (super.onTouchEvent(event)) {
            return true
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        val saveCount = canvas.save()
        super.onDraw(canvas)
        canvas.restoreToCount(saveCount)
    }

    override fun onLayout(
        changed: Boolean, left: Int,
        top: Int, right: Int, bottom: Int,
    ) {
        super.onLayout(changed, left, top, right, bottom)
        getScreenShareViewBounds(screenShareViewBounds)
        setScreenShareViewBounds(screenShareViewBounds)
        getZoomFrameViewBounds(zoomFrameViewBounds)
        setZoomFrameViewBounds(zoomFrameViewBounds)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.concat(currentTransform)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(saveCount)
    }

    private fun getScreenShareViewBounds(outBounds: RectF) {
        val childView = getChildAt(0)
        if (childView != null) {
            val width = childView.width
            val height = childView.height
            val offsetX = getWidth() - width shr 1
            val offsetY = getHeight() - height shr 1
            outBounds[offsetX.toFloat(), offsetY.toFloat(), (offsetX + width).toFloat()] =
                (offsetY + height).toFloat()
        }
    }

    private fun getZoomFrameViewBounds(outBounds: RectF) {
        outBounds[0f, 0f, width.toFloat()] = height.toFloat()
    }

    private fun setScreenShareViewBounds(imageBounds: RectF) {
        this.screenShareViewBounds.set(imageBounds)
        onTransformChanged()
    }

    private fun setZoomFrameViewBounds(viewBounds: RectF?) {
        this.zoomFrameViewBounds.set(viewBounds!!)
    }

    private fun applyTransform(transform: Matrix): Boolean {
        transform.set(previousTransform)
        val scale = gestureListener.scale
        if (scale != currentScale) {
            currentScale = scale
            transform.postScale(scale, scale, gestureListener.startX, gestureListener.startY)
        }
        var transformCorrected = limitScale(transform, gestureListener.startX, gestureListener.startY)
        transform.postTranslate(gestureListener.translationX, gestureListener.translationY)
        transformCorrected = transformCorrected or limitTranslation(transform)
        return transformCorrected
    }

    private fun onTransformChanged() {
        currentTransform = newTransform
        invalidate()
    }

    private fun limitScale(
        transform: Matrix,
        pivotX: Float,
        pivotY: Float,
    ): Boolean {
        val mTempValues = FloatArray(9)
        transform.getValues(mTempValues)
        val currentScale = mTempValues[Matrix.MSCALE_X]
        val targetScale = MIN_SCALE.coerceAtLeast(currentScale).coerceAtMost(MAX_SCALE)
        val scale = targetScale / currentScale
        transform.postScale(scale, scale, pivotX, pivotY)
        return true
    }

    private fun limitTranslation(transform: Matrix): Boolean {
        val rect = RectF()
        rect.set(screenShareViewBounds)
        transform.mapRect(rect)
        val offsetLeft = getOffset(
            rect.left,
            rect.right,
            zoomFrameViewBounds.left,
            zoomFrameViewBounds.right,
            screenShareViewBounds.centerX()
        )
        val offsetTop = getOffset(
            rect.top,
            rect.bottom,
            zoomFrameViewBounds.top,
            zoomFrameViewBounds.bottom,
            screenShareViewBounds.centerY()
        )
        if (offsetLeft != 0f || offsetTop != 0f) {
            transform.postTranslate(offsetLeft, offsetTop)
            return true
        }
        return false
    }

    private fun getOffset(
        viewStart: Float,
        viewEnd: Float,
        limitStart: Float,
        limitEnd: Float,
        limitCenter: Float,
    ): Float {
        val viewWidth = viewEnd - viewStart
        val limitWidth = limitEnd - limitStart
        val limitInnerWidth = (limitCenter - limitStart).coerceAtMost(limitEnd - limitCenter) * 2
        // center if smaller than limitInnerWidth
        if (viewWidth < limitInnerWidth) {
            return limitCenter - (viewEnd + viewStart) / 2
        }
        // to the edge if in between and limitCenter is not (limitLeft + limitRight) / 2
        if (viewWidth < limitWidth) {
            return if (limitCenter < (limitStart + limitEnd) / 2) {
                limitStart - viewStart
            } else {
                limitEnd - viewEnd
            }
        }
        // to the edge if larger than limitWidth and empty space visible
        if (viewStart > limitStart) {
            return limitStart - viewStart
        }
        return if (viewEnd < limitEnd) {
            limitEnd - viewEnd
        } else 0f
    }
}
