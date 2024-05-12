// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.cell

import android.content.Context
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.View.INVISIBLE
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.ParticipantGridCellViewModel
import com.microsoft.fluentui.persona.AvatarView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ParticipantGridCellAvatarView(
    private val avatarView: AvatarView,
    private val participantAvatarSpeakingFrameLayout: FrameLayout,
    private val participantContainer: ConstraintLayout,
    private val displayNameAudioTextView: TextView,
    private val micIndicatorAudioImageView: ImageView,
    private val getParticipantViewDataCallback: (participantID: String) -> CallCompositeParticipantViewData?,
    private val participantViewModel: ParticipantGridCellViewModel,
    private val onHoldTextView: TextView,
    private val context: Context,
    lifecycleScope: LifecycleCoroutineScope,
) {
    private var lastParticipantViewData: CallCompositeParticipantViewData? = null

    init {
        lifecycleScope.launch {
            participantViewModel.getDisplayNameStateFlow().collect {
                lastParticipantViewData = null
                setDisplayName(it)
                updateParticipantViewData()
            }
        }

        lifecycleScope.launch {
            participantViewModel.getParticipantStatusStateFlow().collect {
                lastParticipantViewData = null
                updateParticipantViewData()
                setMicButtonVisibility(participantViewModel.getIsMutedStateFlow().value)
            }
        }

        lifecycleScope.launch {
            participantViewModel.getIsMutedStateFlow().collect {
                setMicButtonVisibility(it)
            }
        }

        lifecycleScope.launch {
            participantViewModel.getIsOnHoldStateFlow().collect {
                if (it) {
                    onHoldTextView.visibility = VISIBLE
                    micIndicatorAudioImageView.visibility = GONE
                    displayNameAudioTextView.setTextColor(ContextCompat.getColor(context, R.color.azure_communication_ui_calling_color_participant_list_mute_mic))
                } else {
                    onHoldTextView.visibility = INVISIBLE
                    setMicButtonVisibility(participantViewModel.getIsMutedStateFlow().value)
                    displayNameAudioTextView.setTextColor(ContextCompat.getColor(context, R.color.azure_communication_ui_calling_color_on_background))
                }
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

    fun updateParticipantViewData() {
        val participantViewData =
            getParticipantViewDataCallback(participantViewModel.getParticipantUserIdentifier())
        if (participantViewData == null) {
            // force bitmap update be setting resource to 0
            avatarView.setImageResource(0)
            setDisplayName(participantViewModel.getDisplayNameStateFlow().value)
        } else if (lastParticipantViewData != participantViewData) {
            lastParticipantViewData = participantViewData

            avatarView.avatarImageBitmap = participantViewData.avatarBitmap
            avatarView.adjustViewBounds = true
            participantViewData.scaleType?.let { scaleType ->
                avatarView.scaleType = scaleType
            }
            participantViewData.displayName?.let { displayName ->
                setDisplayName(displayName)
            }
        }
    }

    private fun setTextViewDisplayName(displayName: String) {
        if (participantViewModel.getParticipantStatusStateFlow().value == ParticipantStatus.CONNECTING ||
            participantViewModel.getParticipantStatusStateFlow().value == ParticipantStatus.RINGING
        ) {
            displayNameAudioTextView.visibility = VISIBLE
            displayNameAudioTextView.text = context.getString(R.string.azure_communication_ui_calling_call_view_calling)
            return
        }

        if (displayName.isBlank()) {
            displayNameAudioTextView.visibility = GONE
        } else {
            displayNameAudioTextView.visibility = VISIBLE
            displayNameAudioTextView.text = displayName
        }
    }

    private fun setSpeakingIndicator(
        isSpeaking: Boolean,
    ) {
        if (isSpeaking) {
            participantAvatarSpeakingFrameLayout.background = ContextCompat.getDrawable(
                context,
                R.drawable.azure_communication_ui_calling_speaking_round_indicator
            )
        } else {
            participantAvatarSpeakingFrameLayout.setBackgroundResource(0)
        }
    }

    private fun setDisplayName(displayName: String) {
        avatarView.name = displayName
        avatarView.invalidate()
        setTextViewDisplayName(displayName)
    }

    private fun setMicButtonVisibility(isMicButtonVisible: Boolean) {
        val status = participantViewModel.getParticipantStatusStateFlow().value
        if (!isMicButtonVisible || status == ParticipantStatus.CONNECTING || status == ParticipantStatus.RINGING) {
            micIndicatorAudioImageView.visibility = GONE
        } else {
            micIndicatorAudioImageView.visibility = VISIBLE
        }
    }
}
