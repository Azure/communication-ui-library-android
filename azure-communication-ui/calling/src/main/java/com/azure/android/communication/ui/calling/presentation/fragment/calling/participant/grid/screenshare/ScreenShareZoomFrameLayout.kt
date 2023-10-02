// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.screenshare

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import kotlin.math.abs
import kotlin.math.hypot

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
        private const val DOUBLE_TAP_ZOOM_ANIMATION_DURATION: Long = 300
    }

    private val gestureListener = GestureListener(context, this)
    private lateinit var showFloatingHeaderCallBack: () -> Unit
    private var currentScale = 0f
    private var isZoomEnabled = false

    private val previousTransform = Matrix()
    private val activeTransform = Matrix()
    private var currentTransform = Matrix()

    private val screenShareViewBounds = RectF()
    private val zoomFrameViewBounds = RectF()

    private val doubleTapZoomLayoutPoint = PointF()
    private val doubleTapScreenSharePoint = PointF()
    private val doubleTapZoomAnimator = ValueAnimator.ofFloat(0f, 1f)
    private var doubleTapScroll = false

    init {
        doubleTapZoomAnimator.interpolator = DecelerateInterpolator()
    }

    // zoom is enabled after the size of screen share view is set
    fun enableZoom() {
        isZoomEnabled = true
    }

    fun setFloatingHeaderCallback(showFloatingHeaderCallBack: () -> Unit) {
        this.showFloatingHeaderCallBack = showFloatingHeaderCallBack
    }

    override fun onSingleClick() {
        showFloatingHeaderCallBack()
    }

    override fun onDoubleClick(motionEvent: MotionEvent) {
        onDoubleTapDisplay(motionEvent)
    }

    override fun initTransformation() {
        previousTransform.set(activeTransform)
    }

    override fun updateTransformation() {
        val transformCorrected = applyTransform(activeTransform)
        onTransformChanged()
        if (transformCorrected) {
            gestureListener.resetPointers()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isZoomEnabled && gestureListener.onTouchEvent(event)) {
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
        //val childView = (childViewRoot as ViewGroup).getChildAt(0)

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

    // Used a part of logic from https://github.com/facebook/fresco zoomable module to apply transformation
    private fun applyTransform(transform: Matrix): Boolean {
        transform.set(previousTransform)
        val scale = gestureListener.scale
        if (scale != currentScale) {
            currentScale = scale
            transform.postScale(scale, scale, gestureListener.startX, gestureListener.startY)
        }
        var transformCorrected =
            limitScale(transform, gestureListener.startX, gestureListener.startY)
        transform.postTranslate(gestureListener.translationX, gestureListener.translationY)
        transformCorrected = transformCorrected or limitTranslation(transform)
        return transformCorrected
    }

    private fun onTransformChanged() {
        currentTransform = activeTransform
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

    private fun onDoubleTapDisplay(motionEvent: MotionEvent): Boolean {
        val zoomLayoutPoint = PointF(motionEvent.x, motionEvent.y)
        val screenSharePoint = mapViewToImage(zoomLayoutPoint)
        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                doubleTapZoomLayoutPoint.set(zoomLayoutPoint)
                doubleTapScreenSharePoint.set(screenSharePoint)
            }
            MotionEvent.ACTION_MOVE -> {
                doubleTapScroll = doubleTapScroll || shouldStartDoubleTapScroll(zoomLayoutPoint)
                if (doubleTapScroll) {
                    val scale = calcScale(zoomLayoutPoint)
                    zoomToPoint(scale, doubleTapScreenSharePoint, doubleTapZoomLayoutPoint)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (doubleTapScroll) {
                    val scale = calcScale(zoomLayoutPoint)
                    zoomToPoint(scale, doubleTapScreenSharePoint, doubleTapZoomLayoutPoint)
                } else {
                    if (shouldZoomToMax()) {
                        zoomToPoint(
                            MAX_SCALE, screenSharePoint, zoomLayoutPoint
                        )
                    } else {
                        zoomToPoint(
                            MIN_SCALE, screenSharePoint, zoomLayoutPoint
                        )
                    }
                }
                doubleTapScroll = false
            }
            else -> return false
        }
        return true
    }

    private fun shouldZoomToMax(): Boolean {
        val mTempValues = FloatArray(9)
        activeTransform.getValues(mTempValues)
        val currentScale = mTempValues[Matrix.MSCALE_X]

        if (currentScale < (MIN_SCALE + MAX_SCALE) / 2) {
            return true
        }
        return false
    }

    private fun shouldStartDoubleTapScroll(viewPoint: PointF): Boolean {
        val dist = hypot(
            (viewPoint.x - doubleTapZoomLayoutPoint.x).toDouble(),
            (viewPoint.y - doubleTapZoomLayoutPoint.y).toDouble()
        )
        return dist > 20
    }

    private fun calcScale(currentViewPoint: PointF): Float {
        val dy = currentViewPoint.y - doubleTapZoomLayoutPoint.y
        val t = 1 + abs(dy) * 0.001f
        return if (dy < 0) gestureListener.scale / t else gestureListener.scale * t
    }

    private fun mapViewToImage(viewPoint: PointF): PointF {
        val points = FloatArray(9)
        points[0] = viewPoint.x
        points[1] = viewPoint.y
        val mActiveTransformInverse = Matrix()
        activeTransform.invert(mActiveTransformInverse)
        mActiveTransformInverse.mapPoints(points, 0, points, 0, 1)
        mapAbsoluteToRelative(points, points)
        return PointF(points[0], points[1])
    }

    private fun mapAbsoluteToRelative(
        destPoints: FloatArray,
        srcPoints: FloatArray,
    ) {
        for (i in 0 until 1) {
            destPoints[i * 2 + 0] =
                (srcPoints[i * 2 + 0] - screenShareViewBounds.left) / screenShareViewBounds.width()
            destPoints[i * 2 + 1] =
                (srcPoints[i * 2 + 1] - screenShareViewBounds.top) / screenShareViewBounds.height()
        }
    }

    private fun mapRelativeToAbsolute(
        destPoints: FloatArray,
        srcPoints: FloatArray,
    ) {
        for (i in 0 until 1) {
            destPoints[i * 2 + 0] =
                srcPoints[i * 2 + 0] * screenShareViewBounds.width() + screenShareViewBounds.left
            destPoints[i * 2 + 1] =
                srcPoints[i * 2 + 1] * screenShareViewBounds.height() + screenShareViewBounds.top
        }
    }

    private fun zoomToPoint(scale: Float, imagePoint: PointF, viewPoint: PointF) {
        val matrix = Matrix()
        applyZoomToPointTransform(matrix, scale, imagePoint, viewPoint)
        setTransformAnimated(matrix)
    }

    // Used a part of logic from https://github.com/facebook/fresco zoomable module to apply zoom to point transformation
    private fun applyZoomToPointTransform(
        transform: Matrix,
        scale: Float,
        imagePoint: PointF,
        viewPoint: PointF,
    ): Boolean {
        val viewAbsolute = FloatArray(9)
        viewAbsolute[0] = imagePoint.x
        viewAbsolute[1] = imagePoint.y
        mapRelativeToAbsolute(viewAbsolute, viewAbsolute)
        val distanceX = viewPoint.x - viewAbsolute[0]
        val distanceY = viewPoint.y - viewAbsolute[1]
        transform.setScale(scale, scale, viewAbsolute[0], viewAbsolute[1])
        var transformCorrected = limitScale(transform, viewAbsolute[0], viewAbsolute[1])
        transform.postTranslate(distanceX, distanceY)
        transformCorrected = transformCorrected or limitTranslation(transform)
        return transformCorrected
    }

    // Used a part of logic from https://github.com/facebook/fresco zoomable module to set animated transformation
    private fun setTransformAnimated(newTransform: Matrix) {
        val startValues = FloatArray(9)
        val stopValues = FloatArray(9)
        stopAnimation()
        doubleTapZoomAnimator.duration = DOUBLE_TAP_ZOOM_ANIMATION_DURATION
        activeTransform.getValues(startValues)
        newTransform.getValues(stopValues)
        doubleTapZoomAnimator.addUpdateListener { valueAnimator ->
            val animatedMatrix = Matrix()
            val currentValues = FloatArray(9)
            val animatedValue = valueAnimator.animatedValue as Float

            for (i in 0..8) {
                currentValues[i] =
                    (1f - animatedValue) * startValues[i] + animatedValue * stopValues[i]
            }

            animatedMatrix.setValues(currentValues)
            activeTransform.set(animatedMatrix)
            onTransformChanged()
        }
        doubleTapZoomAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                onAnimationStopped()
            }

            override fun onAnimationEnd(animation: Animator) {
                onAnimationStopped()
            }

            private fun onAnimationStopped() {
                gestureListener.resetPointers()
                gestureListener.doubleTapZoomAnimationEnded()
            }
        })
        gestureListener.doubleTapZoomAnimationStarted()
        doubleTapZoomAnimator.start()
    }

    private fun stopAnimation() {
        doubleTapZoomAnimator.cancel()
        doubleTapZoomAnimator.removeAllUpdateListeners()
        doubleTapZoomAnimator.removeAllListeners()
        gestureListener.doubleTapZoomAnimationEnded()
    }
}
