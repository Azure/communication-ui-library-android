// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.cell

import android.content.Context
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.ParticipantGridCellViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.VideoViewModel
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
        it: VideoViewModel?,
    ) {
        if (videoStream != null) {
            detachFromParentView(videoStream)
            videoStream = null
        }

        if (it != null) {
            getVideoStream(
                participantViewModel.getParticipantUserIdentifier(),
                it.videoStreamID
            )?.let { view ->
                videoStream = view
                setRendererView(view)
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

    private fun setRendererView(rendererView: View) {
        rendererView.background = ContextCompat.getDrawable(
            context,
            R.drawable.azure_communication_ui_corner_radius_rectangle_4dp
        )
        detachFromParentView(rendererView)
        videoContainer.addView(rendererView, 0)
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
