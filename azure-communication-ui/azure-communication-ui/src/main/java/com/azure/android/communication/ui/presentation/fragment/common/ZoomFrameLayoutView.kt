package com.azure.android.communication.ui.presentation.fragment.common

import android.content.Context
import android.util.AttributeSet
import android.view.ScaleGestureDetector
import android.widget.FrameLayout
import android.view.MotionEvent
import android.view.View
import kotlin.math.sign

internal class ZoomFrameLayoutView: FrameLayout, ScaleGestureDetector.OnScaleGestureListener{
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var scaleDetector: ScaleGestureDetector = ScaleGestureDetector(context, this)

    private enum class Mode {
        NONE, DRAG, ZOOM
    }

    private val MIN_ZOOM = 1.0f
    private val MAX_ZOOM = 4.0f
    private var mode = Mode.NONE
    private var scale = 1.0f
    private var lastScaleFactor = 0f
    // Where the finger first  touches the screen
    private var startX = 0f
    private var startY = 0f

    // How much to translate the canvas
    private var dx = 0f
    private var dy = 0f
    private var prevDx = 0f
    private var prevDy = 0f

     override fun onScaleBegin(scaleDetector: ScaleGestureDetector?): Boolean {
        return true
    }

    override fun onScale(scaleDetector: ScaleGestureDetector): Boolean {
        val scaleFactor = scaleDetector.scaleFactor
        if (lastScaleFactor.toInt() == 0 || sign(scaleFactor) == sign(lastScaleFactor)) {
            scale *= scaleFactor
            scale = MIN_ZOOM.coerceAtLeast(scale.coerceAtMost(MAX_ZOOM))
            lastScaleFactor = scaleFactor
        } else {
            lastScaleFactor = 0f
        }
        return true
    }

    override fun onScaleEnd(scaleDetector: ScaleGestureDetector?) {
    }

    private fun applyScaleAndTranslation() {
        child().scaleX = scale
        child().scaleY = scale
        child().translationX = dx
        child().translationY = dy
    }

    private fun child(): View {
        return getChildAt(0)
    }

    fun zoom(view: View?, motionEvent: MotionEvent) {
        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                if (scale > MIN_ZOOM) {
                    mode = Mode.DRAG
                    startX = motionEvent.x - prevDx
                    startY = motionEvent.y - prevDy
                }
            }
            MotionEvent.ACTION_MOVE -> if (mode == Mode.DRAG) {
                dx = motionEvent.x - startX
                dy = motionEvent.y - startY
            }
            MotionEvent.ACTION_POINTER_DOWN -> mode = Mode.ZOOM
            MotionEvent.ACTION_POINTER_UP -> mode = Mode.DRAG
            MotionEvent.ACTION_UP -> {
                mode = Mode.NONE
                prevDx = dx
                prevDy = dy
            }
        }
        scaleDetector.onTouchEvent(motionEvent)
        if (mode == Mode.DRAG && scale >= MIN_ZOOM || mode == Mode.ZOOM) {
            parent.requestDisallowInterceptTouchEvent(true)
            val maxDx: Float =
                (child().width - child().width / scale) / 2 * scale
            val maxDy: Float =
                (child().height - child().height / scale) / 2 * scale
            dx = dx.coerceAtLeast(-maxDx).coerceAtMost(maxDx)
            dy = dy.coerceAtLeast(-maxDy).coerceAtMost(maxDy)

            applyScaleAndTranslation()
        }
    }
}