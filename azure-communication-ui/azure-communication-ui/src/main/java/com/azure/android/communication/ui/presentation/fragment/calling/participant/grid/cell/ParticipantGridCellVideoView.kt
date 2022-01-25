// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.cell

import android.content.Context
import android.content.res.Configuration
import android.view.Gravity
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.View.GONE
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.calling.RendererListener
import com.azure.android.communication.calling.StreamSize
import com.azure.android.communication.calling.VideoStreamRenderer
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.model.StreamType
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.ParticipantGridCellViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.VideoViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.ZoomableFrameLayout
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ParticipantGridCellVideoView(
    lifecycleScope: LifecycleCoroutineScope,
    private val participantVideoContainerFrameLayout: FrameLayout,
    private val videoContainer: ConstraintLayout,
    private val displayNameOnVideoTextView: TextView,
    private val micIndicatorOnVideoImageView: ImageView,
    private val participantViewModel: ParticipantGridCellViewModel,
    private val getVideoStream: (String, String) -> View?,
    private val context: Context,
    private val showFloatingHeaderCallBack: () -> Unit,
    private val getScreenShareVideoStreamRenderer: () -> VideoStreamRenderer?,
) {
    private var videoStream: View? = null
    private lateinit var rendererViewTransformationWrapper: LinearLayout
    private lateinit var zoomFrameLayoutView: ZoomableFrameLayout

    init {
        lifecycleScope.launch {
            participantViewModel.getDisplayNameStateFlow().collect {
                setDisplayName(it)
            }
        }

        lifecycleScope.launch {
            participantViewModel.getIsMutedStateFlow().collect {
                setMicButtonVisibility(it)
            }
        }
        lifecycleScope.launch {
            participantViewModel.getIsSpeakingStateFlow().collect {
                setSpeakingIndicator(it)
            }
        }
        lifecycleScope.launch {
            participantViewModel.getVideoViewModelStateFlow().collect {
                updateVideoStream(it)
                if (it != null) {
                    videoContainer.visibility = VISIBLE
                } else {
                    videoContainer.visibility = INVISIBLE
                }
            }
        }
    }

    private fun updateVideoStream(
        videoViewModel: VideoViewModel?,
    ) {
        if (videoStream != null) {
            detachFromParentView(videoStream)
            videoStream = null
        }

        if (videoViewModel != null) {
            getVideoStream(
                participantViewModel.getParticipantUserIdentifier(),
                videoViewModel.videoStreamID
            )?.let { view ->
                videoStream = view
                setRendererView(view, videoViewModel.streamType)
            }
        }
    }

    private fun setSpeakingIndicator(
        isSpeaking: Boolean,
    ) {
        if (isSpeaking) {
            participantVideoContainerFrameLayout.visibility = VISIBLE
        } else {
            participantVideoContainerFrameLayout.visibility = GONE
        }
    }

    private fun setRendererView(rendererView: View, streamType: StreamType) {
        detachFromParentView(rendererView)
        val background = ContextCompat.getDrawable(
            context,
            R.drawable.azure_communication_ui_corner_radius_rectangle_4dp
        )

        if (streamType == StreamType.SCREEN_SHARING) {
            val streamSize = getScreenShareVideoStreamRenderer()?.size
            // if the stream size is null, it indicates frame is not rendered yet
            if (streamSize == null) {
                getScreenShareVideoStreamRenderer()?.addRendererListener(rendererListener())
            }
            rendererViewTransformationWrapper = LinearLayout(this.context)
            rendererViewTransformationWrapper.addView(rendererView)
            zoomFrameLayoutView = ZoomableFrameLayout(this.context)
            zoomFrameLayoutView.layoutParams =
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            zoomFrameLayoutView.addView(rendererViewTransformationWrapper)
            zoomFrameLayoutView.addHeaderNotification(showFloatingHeaderCallBack)
            videoContainer.addView(zoomFrameLayoutView, 0)

            videoContainer.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    videoContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    // update view size only after child is added successfully
                    applyScreenShareSize()
                }
            })
        } else {
            rendererView.background = background
            videoContainer.addView(rendererView, 0)
        }
    }

    private fun applyScreenShareSize() {
        val streamSize = getScreenShareVideoStreamRenderer()?.size
        streamSize?.let {
            videoContainer.post {
                setScreenShareLayoutSize(it)
            }
        }
    }

    private fun rendererListener() = object : RendererListener {
        override fun onFirstFrameRendered() {
            applyScreenShareSize()
            getScreenShareVideoStreamRenderer()?.removeRendererListener(this)
        }
        override fun onRendererFailedToStart() {
        }
    }

    private fun setScreenShareLayoutSize(streamSize: StreamSize) {
        // below logic is from SDK team code to find width and height of video view excluding grey portion
        val viewWidth = videoContainer.width.toFloat()
        val viewHeight = videoContainer.height.toFloat()
        val videoWidth = streamSize.width
        val videoHeight = streamSize.height

        val scaleWidth = viewWidth / videoWidth
        val scaleHeight = viewHeight / videoHeight
        val scale = scaleWidth.coerceAtMost(scaleHeight)

        val layoutParams =
            rendererViewTransformationWrapper.layoutParams as FrameLayout.LayoutParams
        if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutParams.height = (scale * videoHeight).toInt()
            layoutParams.gravity = Gravity.CENTER_VERTICAL
        } else {
            layoutParams.width = (scale * videoWidth).toInt()
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL
        }
        rendererViewTransformationWrapper.layoutParams = layoutParams
        zoomFrameLayoutView.enableInteractions()
    }

    private fun setDisplayName(displayName: String) {
        displayNameOnVideoTextView.text = displayName
    }

    private fun setMicButtonVisibility(isMicButtonVisible: Boolean) {
        if (!isMicButtonVisible) {
            micIndicatorOnVideoImageView.visibility = GONE
        } else {
            micIndicatorOnVideoImageView.visibility = VISIBLE
        }
    }

    private fun detachFromParentView(view: View?) {
        if (view != null && view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
    }
}
