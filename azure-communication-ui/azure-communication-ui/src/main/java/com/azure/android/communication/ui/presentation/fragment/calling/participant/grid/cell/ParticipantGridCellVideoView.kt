// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.cell

import android.content.Context
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.calling.VideoStreamRenderer
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.model.StreamType
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.ParticipantGridCellViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.VideoViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.ScreenShareViewManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ParticipantGridCellVideoView(
    private val context: Context,
    lifecycleScope: LifecycleCoroutineScope,
    private val participantVideoContainerFrameLayout: FrameLayout,
    private val videoContainer: ConstraintLayout,
    private val displayNameOnVideoTextView: TextView,
    private val micIndicatorOnVideoImageView: ImageView,
    private val participantViewModel: ParticipantGridCellViewModel,
    private val getVideoStreamCallback: (String, String) -> View?,
    private val showFloatingHeaderCallBack: () -> Unit,
    private val getScreenShareVideoStreamRendererCallback: () -> VideoStreamRenderer?,
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
            getVideoStreamCallback(
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
            val screenShareFactory = ScreenShareViewManager(
                context,
                videoContainer,
                getScreenShareVideoStreamRendererCallback,
                showFloatingHeaderCallBack
            )
            videoContainer.addView(screenShareFactory.getScreenShareView(rendererView), 0)
        } else {
            rendererView.background = background
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
