// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.cell

import android.content.Context
import android.content.res.Configuration
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.calling.StreamSize
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.model.StreamType
import com.azure.android.communication.ui.presentation.VideoViewManager
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.ParticipantGridCellViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.VideoViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.teams.zoomable.ZoomableFrameLayout
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ParticipantGridCellVideoView(
    private val participantVideoContainerFrameLayout: FrameLayout,
    private val videoContainer: ConstraintLayout,
    private val displayNameOnVideoTextView: TextView,
    private val micIndicatorOnVideoImageView: ImageView,
    private val participantViewModel: ParticipantGridCellViewModel,
    private val getVideoStream: (String, String) -> View?,
    private val context: Context,
    lifecycleScope: LifecycleCoroutineScope,
    private val showFloatingHeaderCallBack: () -> Unit,
    private val getScreenShareStreamSize: () -> StreamSize?

) {
    private var videoStream: View? = null

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
        rendererView.background = background

        if (streamType == StreamType.SCREEN_SHARING) {
            val rendererViewTransformationWrapper = LinearLayout(this.context)
            rendererViewTransformationWrapper.addView(rendererView)
            rendererViewTransformationWrapper.background = background

            val zoomFrameLayoutView = ZoomableFrameLayout(this.context)
            zoomFrameLayoutView.layoutParams =
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT)
            zoomFrameLayoutView.addView(rendererViewTransformationWrapper)
            zoomFrameLayoutView.background = background

            rendererView.setOnTouchListener { _, _ ->
                val streamSize = getScreenShareStreamSize()
                streamSize?.let {
                    rendererView.setOnTouchListener(null)

                    val viewWidth = videoContainer.width.toFloat()
                    val viewHeight = videoContainer.height.toFloat()
                    val videoWidth = it.width
                    val videoHeight = it.height

                    val scaleWidth = viewWidth / videoWidth
                    val scaleHeight = viewHeight / videoHeight
                    val scale = scaleWidth.coerceAtMost(scaleHeight)

                    var layoutParams = rendererViewTransformationWrapper.layoutParams as FrameLayout.LayoutParams
                    if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        layoutParams.height = (scale * videoHeight).toInt()
                        layoutParams.gravity = Gravity.CENTER_VERTICAL
                    } else {
                        layoutParams.width = (scale * videoWidth).toInt()
                        layoutParams.gravity = Gravity.CENTER_HORIZONTAL
                    }

                    rendererViewTransformationWrapper.layoutParams = layoutParams
                    zoomFrameLayoutView.enableInteractions()
                    false
                }
                true
            }

            videoContainer.addView(zoomFrameLayoutView, 0)
        } else {
            videoContainer.addView(rendererView, 0)
        }
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
