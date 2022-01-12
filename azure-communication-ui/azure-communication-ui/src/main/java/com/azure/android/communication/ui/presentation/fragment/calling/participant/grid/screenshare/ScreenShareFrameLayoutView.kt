// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.marginTop
import com.azure.android.communication.ui.presentation.VideoViewManager
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

    private var layoutSet = false

    init {
        setOnTouchListener { _, motionEvent ->
            scaleGestureDetectorOnTouch(motionEvent)
            true
        }
    }

    fun start(showFloatingHeaderCallBack: () -> Unit) {
        this.showFloatingHeaderCallBack = showFloatingHeaderCallBack
        clickOnGestureDetector.setShowFloatingHeaderCallBack(this::onSingleClick, this::onDoubleClick)
    }

    override fun onScaleBegin(scaleDetector: ScaleGestureDetector?): Boolean {
        applyChildHeight()
        return true
    }

    private fun applyChildHeight() {
        if(!layoutSet) {
            layoutSet = true
            val child = child()
            val dimensions =
                VideoViewManager.remoteParticipantVideoRendererMap.toList()[0].second.videoStreamRenderer?.size
            val viewWidth = this.width.toFloat()
            val viewHeight = this.height.toFloat()
            val videoWidth = dimensions!!.width
            val videoHeight = dimensions!!.height

            val scaleWidth = viewWidth / videoWidth
            val scaleHeight = viewHeight / videoHeight
            val scale = scaleWidth.coerceAtMost(scaleHeight)
            val sw = (scale * videoWidth)
            val sh = (scale * videoHeight)

            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                val layoutParamsLinearLayout = child.layoutParams
                layoutParamsLinearLayout.height = sh.toInt()
               // layoutParamsLinearLayout.width = sw.toInt()

                child.layoutParams = layoutParamsLinearLayout

                val layoutParamsLinearLayout2 = child.layoutParams as ViewGroup.MarginLayoutParams
                layoutParamsLinearLayout2.topMargin = 800
               // layoutParamsLinearLayout2.leftMargin = 200
                child.layoutParams = layoutParamsLinearLayout2
            } else {
                val layoutParamsLinearLayout = child.layoutParams
                //layoutParamsLinearLayout.height = sh.toInt()
                layoutParamsLinearLayout.width = sw.toInt()

                child.layoutParams = layoutParamsLinearLayout

                val layoutParamsLinearLayout2 = child.layoutParams as ViewGroup.MarginLayoutParams
                //layoutParamsLinearLayout2.topMargin = 800
                layoutParamsLinearLayout2.leftMargin = 200
                child.layoutParams = layoutParamsLinearLayout2
            }




            child.invalidate()
            child.refreshDrawableState()
        }

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

    private fun child() = getChildAt(0)

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
