// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.cell

import android.content.Context
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.model.StreamType
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.ParticipantGridCellViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.VideoViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.ScreenShareFrameLayoutView
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
        rendererView.background = ContextCompat.getDrawable(
            context,
            R.drawable.azure_communication_ui_corner_radius_rectangle_4dp
        )
        detachFromParentView(rendererView)
        if (streamType == StreamType.SCREEN_SHARING) {

            val viewWidth = videoContainer.width.toFloat()
            val viewHeight = videoContainer.height.toFloat()
            val videoWidth = 1920f
            val videoHeight = 1080f

            val scaleWidth = viewWidth / videoWidth
            val scaleHeight = viewHeight / videoHeight
            val scale = scaleWidth.coerceAtMost(scaleHeight)
            val sw = (scale * videoWidth )
            val sh = (scale * videoHeight )


            val newWidth = viewHeight * (sw / sh)
            rendererView.layoutParams = ViewGroup.LayoutParams(newWidth.toInt(), viewHeight.toInt())

            val zoomFrameLayoutView = LinearLayout(context)
            //   zoomFrameLayoutView.start(showFloatingHeaderCallBack)
            zoomFrameLayoutView.addView(rendererView)

            val scrollView = ScrollView(context)
            scrollView.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            scrollView.addView(zoomFrameLayoutView)

            val horizontalScrollView = HorizontalScrollView(context)
            horizontalScrollView.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            horizontalScrollView.addView(scrollView)

            videoContainer.addView(horizontalScrollView, 0)
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
