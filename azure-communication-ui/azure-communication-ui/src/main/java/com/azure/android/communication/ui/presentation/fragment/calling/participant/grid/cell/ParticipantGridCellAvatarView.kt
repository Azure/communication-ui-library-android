// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.cell

import android.content.Context
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.ParticipantGridCellViewModel
import com.microsoft.fluentui.persona.AvatarView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ParticipantGridCellAvatarView(
    private val avatarView: AvatarView,
    private val participantAvatarSpeakingFrameLayout: FrameLayout,
    private val participantContainer: ConstraintLayout,
    private val displayNameAudioTextView: TextView,
    private val micIndicatorAudioImageView: ImageView,
    private val participantViewModel: ParticipantGridCellViewModel,
    private val context: Context,
    lifecycleScope: LifecycleCoroutineScope,
) {

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
                if (it != null) {
                    participantContainer.visibility = INVISIBLE
                } else {
                    participantContainer.visibility = VISIBLE
                }
            }
        }
    }

    private fun setSpeakingIndicator(
        isSpeaking: Boolean,
    ) {
        if (isSpeaking) {
            participantAvatarSpeakingFrameLayout.background = ContextCompat.getDrawable(
                context,
                R.drawable.azure_communication_ui_speaking_round_indicator
            )
        } else {
            participantAvatarSpeakingFrameLayout.setBackgroundResource(0)
        }
    }

    private fun setDisplayName(displayName: String) {
        avatarView.name = displayName
        avatarView.invalidate()

        if (displayName.isBlank()) {
            displayNameAudioTextView.visibility = GONE
        } else {
            displayNameAudioTextView.text = displayName
        }
    }

    private fun setMicButtonVisibility(isMicButtonVisible: Boolean) {
        if (!isMicButtonVisible) {
            micIndicatorAudioImageView.visibility = GONE
        } else {
            micIndicatorAudioImageView.visibility = VISIBLE
        }
    }
}
