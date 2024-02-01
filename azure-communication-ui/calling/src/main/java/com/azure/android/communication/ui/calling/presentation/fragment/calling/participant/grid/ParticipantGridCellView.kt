// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.ui.calling.impl.R
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.cell.ParticipantGridCellAvatarView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.cell.ParticipantGridCellVideoView
import com.azure.android.communication.ui.calling.service.sdk.VideoStreamRenderer
import com.microsoft.fluentui.persona.AvatarView

@SuppressLint("ViewConstructor")
internal class ParticipantGridCellView(
    context: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val participantViewModel: ParticipantGridCellViewModel,
    private val showFloatingHeaderCallBack: () -> Unit,
    private val getVideoStreamCallback: (String, String) -> View?,
    private val getScreenShareVideoStreamRendererCallback: () -> VideoStreamRenderer?,
    private val getParticipantViewDataCallback: (participantID: String) -> CallCompositeParticipantViewData?,
) : RelativeLayout(context) {

    private lateinit var avatarView: ParticipantGridCellAvatarView
    private lateinit var videoView: ParticipantGridCellVideoView

    init {
        inflate(context, R.layout.azure_communication_ui_calling_participant_avatar_view, this)
        inflate(context, R.layout.azure_communication_ui_calling_participant_video_view, this)
        createVideoView()
        createAvatarView()
    }

    fun getParticipantIdentifier() = participantViewModel.getParticipantUserIdentifier()

    fun updateParticipantViewData() {
        if (::avatarView.isInitialized) {
            avatarView.updateParticipantViewData()
        }
        if (::videoView.isInitialized) {
            videoView.updateParticipantViewData()
        }
    }

    private fun createAvatarView() {
        val avatarControl: AvatarView =
            findViewById(R.id.azure_communication_ui_participant_view_avatar)

        val participantAvatarSpeakingIndicator: FrameLayout =
            findViewById(R.id.azure_communication_ui_participant_view_avatar_is_speaking_frame)

        val participantAvatarContainer: ConstraintLayout =
            findViewById(R.id.azure_communication_ui_participant_avatar_view_container)

        val displayNameAudioTextView: TextView =
            findViewById(R.id.azure_communication_ui_participant_audio_view_display_name)

        val micIndicatorAudioImageView: ImageView =
            findViewById(R.id.azure_communication_ui_participant_audio_view_mic_indicator)

        val onHoldTextView: TextView =
            findViewById(R.id.azure_communication_ui_calling_participant_audio_view_on_hold)

        avatarView = ParticipantGridCellAvatarView(
            avatarControl,
            participantAvatarSpeakingIndicator,
            participantAvatarContainer,
            displayNameAudioTextView,
            micIndicatorAudioImageView,
            getParticipantViewDataCallback,
            participantViewModel,
            onHoldTextView,
            context,
            lifecycleScope,
        )
    }

    private fun createVideoView() {
        val participantVideoContainerFrameLayout: FrameLayout =
            findViewById(R.id.azure_communication_ui_participant_video_view_frame)

        val videoContainer: ConstraintLayout =
            findViewById(R.id.azure_communication_ui_participant_video_view_container)

        val displayNameAndMicIndicatorViewContainer: View =
            findViewById(R.id.azure_communication_ui_participant_view_on_video_information_container)

        val displayNameOnVideoTextView: TextView =
            findViewById(R.id.azure_communication_ui_participant_view_on_video_display_name)

        val micIndicatorOnVideoImageView: ImageView =
            findViewById(R.id.azure_communication_ui_participant_view_on_video_mic_indicator)

        videoView = ParticipantGridCellVideoView(
            context,
            lifecycleScope,
            participantVideoContainerFrameLayout,
            videoContainer,
            displayNameAndMicIndicatorViewContainer,
            displayNameOnVideoTextView,
            micIndicatorOnVideoImageView,
            participantViewModel,
            getVideoStreamCallback,
            showFloatingHeaderCallBack,
            getScreenShareVideoStreamRendererCallback,
            getParticipantViewDataCallback,
        )
    }
}
