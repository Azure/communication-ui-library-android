// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.azure.android.communication.ui.R
import kotlin.math.sign

internal class ScreenShareFrameLayoutView : FrameLayout, ScaleGestureDetector.OnScaleGestureListener {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private val clickOnGestureDetector = ClickOnGestureDetector()
    private var clickGestureDetector = GestureDetector(context, clickOnGestureDetector)
    private var scaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(context, this)

    private enum class Mode {
        NONE, DRAG, ZOOM
    }

    private var mode = Mode.NONE
    private val defaultMinScale = 1.0f
    private val defaultMaxScale = 4.0f
    private var scale = 1.0f
    private var prevScale = 0f
    private var startX = 0f
    private var startY = 0f
    private var translX = 0f
    private var translY = 0f
    private var prevTranslX = 0f
    private var prevTranslY = 0f

    private var showFloatingHeaderCallBack: (() -> Unit)? = null

    private lateinit var child: View

    init {
        setOnTouchListener { _, motionEvent ->
            scaleGestureDetectorOnTouch(motionEvent)
            true
        }
    }

    fun start(showFloatingHeaderCallBack: () -> Unit) {
        this.background = context.let {
            ContextCompat.getDrawable(
                it,
                R.color.azure_communication_ui_color_background_red
            )
        }
        this.showFloatingHeaderCallBack = showFloatingHeaderCallBack
        clickOnGestureDetector.setShowFloatingHeaderCallBack(this::onSingleClick, this::onDoubleClick)
    }

    override fun onScaleBegin(scaleDetector: ScaleGestureDetector?): Boolean {
        return true
    }

    override fun onScale(scaleDetector: ScaleGestureDetector): Boolean {
        val scaleFactor = scaleDetector.scaleFactor
        if (prevScale.toInt() == 0 || sign(scaleFactor) == sign(prevScale)) {
            scale *= scaleFactor
            scale = defaultMinScale.coerceAtLeast(scale.coerceAtMost(defaultMaxScale))
            prevScale = scaleFactor
        } else {
            prevScale = 0f
        }
        return true
    }

    override fun onScaleEnd(scaleDetector: ScaleGestureDetector?) {
    }

    private fun scaleGestureDetectorOnTouch(motionEvent: MotionEvent) {
        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                if (scale > defaultMinScale) {
                    mode = Mode.DRAG
                    startX = motionEvent.x - prevTranslX
                    startY = motionEvent.y - prevTranslY
                }
            }
            MotionEvent.ACTION_UP -> {
                mode = Mode.NONE
                prevTranslX = translX
                prevTranslY = translY
            }
            MotionEvent.ACTION_MOVE -> if (mode == Mode.DRAG) {
                translX = motionEvent.x - startX
                translY = motionEvent.y - startY
            }
            MotionEvent.ACTION_POINTER_DOWN -> mode = Mode.ZOOM
        }

        clickGestureDetector.onTouchEvent(motionEvent)
        scaleGestureDetector.onTouchEvent(motionEvent)

        if (mode == Mode.DRAG && scale >= defaultMinScale || mode == Mode.ZOOM) {
            applyViewScaling()
        }
    }

    private fun applyViewScaling() {
        parent.requestDisallowInterceptTouchEvent(true)
        val child = child()
        val maxDx: Float =
            (child.width - child.width / scale) / 2 * scale
        val maxDy: Float =
            (child.height - child.height / scale) / 2 * scale
        translX = translX.coerceAtLeast(-maxDx).coerceAtMost(maxDx)
        translY = translY.coerceAtLeast(-maxDy).coerceAtMost(maxDy)
        setScaleAndTranslation()
    }

    private fun setScaleAndTranslation() {
        val child = child()
        child.scaleX = scale
        child.scaleY = scale
        child.translationX = translX
        child.translationY = translY
    }

    private fun child() = child

    private fun onSingleClick() {
        showFloatingHeaderCallBack?.let {
            it()
        }
    }

    private fun onDoubleClick(motionEvent: MotionEvent?) {
        motionEvent?.let {
            scale = if (scale == defaultMaxScale) {
                defaultMinScale
            } else {
                defaultMaxScale
            }
            applyViewScaling()
        }
    }
}
