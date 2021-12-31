// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import com.azure.android.communication.ui.presentation.VideoViewManager
import kotlin.math.sign

internal class ScreenShareFrameLayoutView : FrameLayout,
    ScaleGestureDetector.OnScaleGestureListener {
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

    private lateinit var videoViewLayout: LinearLayout
    private lateinit var videoViewContainer: ConstraintLayout
    private var newMaxWidth: Float = 0f
    private var actualWidth: Float = 0f
    private var actualHeight: Float = 0f


    init {
        setOnTouchListener { _, motionEvent ->
            scaleGestureDetectorOnTouch(motionEvent)
            true
        }
    }

    fun setView(
        videoContainer: ConstraintLayout,
        viewLayout: LinearLayout,
    ) {
        videoViewLayout = viewLayout
        videoViewContainer = videoContainer
        val viewWidth = videoContainer.width.toFloat()
        val viewHeight = videoContainer.height.toFloat()
        val videoWidth = 1920f
        val videoHeight = 1080f

        val scaleWidth = viewWidth / videoWidth
        val scaleHeight = viewHeight / videoHeight
        val scale = scaleWidth.coerceAtMost(scaleHeight)
        val sw = (scale * videoWidth)
        val sh = (scale * videoHeight)

        actualWidth = sw
        actualHeight = sh
        newMaxWidth = viewHeight * (actualWidth / actualHeight)




        videoViewLayout.layoutParams = ViewGroup.LayoutParams(sw.toInt(), viewHeight.toInt())

        this.addView(videoViewLayout)

    }

    fun start(showFloatingHeaderCallBack: () -> Unit) {
        this.showFloatingHeaderCallBack = showFloatingHeaderCallBack
        clickOnGestureDetector.setShowFloatingHeaderCallBack(this::onSingleClick,
            this::onDoubleClick)
    }

    override fun onScaleBegin(scaleDetector: ScaleGestureDetector?): Boolean {

        return true
    }

    var first = true
    override fun onScale(scaleDetector: ScaleGestureDetector): Boolean {
        val scaleFactor = scaleDetector.scaleFactor

        if (scaleFactor >= 1) {
            postDelayed({
                if (videoViewLayout.layoutParams.width < newMaxWidth) {

                    if(first) {
                        first = false
                        val r = VideoViewManager.remoteParticipantVideoRendererMap.toList()
                        val k = r[0].second.videoStreamRenderer?.size

                        k?.let {


                            val viewWidth = videoViewContainer.width.toFloat()
                            val viewHeight = videoViewContainer.height.toFloat()
                            val videoWidth = it.width
                            val videoHeight = 1080f

                            val scaleWidth = viewWidth / videoWidth
                            val scaleHeight = viewHeight / videoHeight
                            val scale = scaleWidth.coerceAtMost(scaleHeight)
                            val sw = (scale * videoWidth)
                            val sh = (scale * videoHeight)

                            newMaxWidth = viewHeight * (sw / sh)
                        }
                    }



                    val oldWidth = videoViewLayout.layoutParams.width

                    Log.d("hello ", " old width " + oldWidth)
                    Log.d("hello ", " new width " + newMaxWidth)

                    val par = videoViewLayout.layoutParams
                    par.width = if (newMaxWidth - oldWidth > 150) oldWidth + 150 else {
                        oldWidth + (newMaxWidth - oldWidth).toInt()
                    }

                    Log.d("hello ", " actual width " + par.width)


                    videoViewLayout.layoutParams = par
                    videoViewLayout.invalidate()
                    videoViewLayout.refreshDrawableState()

                    this.invalidate()
                    this.refreshDrawableState()
                }
            }, 100)
        } else if (scaleFactor < 1) {
            postDelayed({
                if (videoViewLayout.layoutParams.width > actualWidth) {

                    val oldWidth = videoViewLayout.layoutParams.width

                    val par = videoViewLayout.layoutParams
                    par.width = if (oldWidth - actualWidth > 150) oldWidth - 150 else {
                        oldWidth - (oldWidth - actualWidth).toInt()
                    }

                    videoViewLayout.layoutParams = par
                    videoViewLayout.invalidate()
                    videoViewLayout.refreshDrawableState()

                    this.invalidate()
                    this.refreshDrawableState()
                }
            }, 100)
        }







        if (prevScale.toInt() == 0 || sign(scaleFactor) == sign(prevScale)) {
            scale *= scaleFactor
            val videoWidth = 1920f
            val videoHeight = 1080f

            val sw = (scale * videoWidth)
            val sh = (scale * videoHeight)
            // videoViewLayout.layoutParams.width +=1


            //if(videoViewLayout.layoutParams.width < newMaxWidth) {

            //  }


            // val newWidth = viewHeight * (sw / sh)
            //
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
            MotionEvent.ACTION_MOVE -> {

                translX = motionEvent.x - startX
                translY = motionEvent.y - startY


            }
            MotionEvent.ACTION_POINTER_DOWN -> mode = Mode.ZOOM
        }

        //clickGestureDetector.onTouchEvent(motionEvent)
        scaleGestureDetector.onTouchEvent(motionEvent)

        if (mode == Mode.DRAG && scale >= defaultMinScale || mode == Mode.ZOOM) {
            // applyViewScaling()
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
        // child.scaleX = scale
        //  child.scaleY = scale


        //child.translationX = translX
        //child.translationY = translY
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
            //  applyViewScaling()
        }
    }

}
