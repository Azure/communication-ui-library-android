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
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.ScreenShareZoomFrameLayout
import com.azure.android.communication.ui.utilities.implementation.FeatureFlags
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ParticipantGridCellVideoView(
    private val context: Context,
    lifecycleScope: LifecycleCoroutineScope,
    private val participantVideoContainerSpeakingFrameLayout: FrameLayout,
    private val videoContainer: ConstraintLayout,
    private val displayNameAndMicIndicatorViewContainer: View,
    private val displayNameOnVideoTextView: TextView,
    private val micIndicatorOnVideoImageView: ImageView,
    private val participantViewModel: ParticipantGridCellViewModel,
    private val getVideoStreamCallback: (String, String) -> View?,
    private val showFloatingHeaderCallBack: () -> Unit,
    private val getScreenShareVideoStreamRendererCallback: () -> VideoStreamRenderer?,

) {
    private var videoStream: View? = null
    private var screenShareZoomFrameLayout: ScreenShareZoomFrameLayout? = null

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
            participantViewModel.getIsNameIndicatorVisibleStateFlow().collect {
                setNameAndMicIndicatorViewVisibility(it)
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
        } else {
            removeScreenShareZoomView()
        }
    }

    private fun removeScreenShareZoomView() {
        if (screenShareZoomFrameLayout != null) {
            // removing this code will cause issue when new user share screen (zoom will not work)
            screenShareZoomFrameLayout?.removeAllViews()
            videoContainer.removeView(screenShareZoomFrameLayout)
            screenShareZoomFrameLayout = null
            videoContainer.invalidate()
        }
    }

    private fun setSpeakingIndicator(
        isSpeaking: Boolean,
    ) {
        if (isSpeaking) {
            participantVideoContainerSpeakingFrameLayout.visibility = VISIBLE
        } else {
            participantVideoContainerSpeakingFrameLayout.visibility = GONE
        }
    }

    private fun setRendererView(rendererView: View, streamType: StreamType) {
        detachFromParentView(rendererView)

        if (streamType == StreamType.SCREEN_SHARING) {
            if (FeatureFlags.ScreenShareZoom.active) {
                removeScreenShareZoomView()
                val screenShareFactory = ScreenShareViewManager(
                    context,
                    videoContainer,
                    getScreenShareVideoStreamRendererCallback,
                    showFloatingHeaderCallBack
                )
                screenShareZoomFrameLayout = screenShareFactory.getScreenShareView(rendererView)
                videoContainer.addView(screenShareZoomFrameLayout, 0)
                // scaled transformed view round corners are not visible when scroll is not at end
                // to avoid content outside speaking rectangle removing round corners
                videoContainer.background = ContextCompat.getDrawable(
                    context,
                    R.color.azure_communication_ui_color_surface
                )
                participantVideoContainerSpeakingFrameLayout.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.azure_communication_ui_speaking_rectangle_indicator_no_corner
                )
                return
            }
        }

        rendererView.background = ContextCompat.getDrawable(
            context,
            R.drawable.azure_communication_ui_corner_radius_rectangle_4dp
        )
        videoContainer.background = ContextCompat.getDrawable(
            context,
            R.drawable.azure_communication_ui_corner_radius_rectangle_4dp_surface
        )
        participantVideoContainerSpeakingFrameLayout.background = ContextCompat.getDrawable(
            context,
            R.drawable.azure_communication_ui_speaking_rectangle_indicator
        )
        videoContainer.addView(rendererView, 0)
    }

    private fun setDisplayName(displayName: String) {
        if (displayName.isBlank()) {
            displayNameOnVideoTextView.visibility = GONE
        } else {
            displayNameOnVideoTextView.text = displayName
        }
    }

    private fun setMicButtonVisibility(isMicButtonVisible: Boolean) {
        if (!isMicButtonVisible) {
            micIndicatorOnVideoImageView.visibility = GONE
        } else {
            micIndicatorOnVideoImageView.visibility = VISIBLE
        }
    }

    private fun setNameAndMicIndicatorViewVisibility(isNameIndicatorVisible: Boolean) {
        if (!isNameIndicatorVisible) {
            displayNameAndMicIndicatorViewContainer.visibility = GONE
        } else {
            displayNameAndMicIndicatorViewContainer.visibility = VISIBLE
        }
    }

    private fun detachFromParentView(view: View?) {
        if (view != null && view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
    }
}
