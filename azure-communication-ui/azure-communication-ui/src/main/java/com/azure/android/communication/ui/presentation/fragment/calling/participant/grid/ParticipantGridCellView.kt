// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.calling.VideoStreamRenderer
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.cell.ParticipantGridCellAvatarView
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.cell.ParticipantGridCellVideoView
import com.microsoft.fluentui.persona.AvatarView

internal class ParticipantGridCellView(
    context: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val participantViewModel: ParticipantGridCellViewModel,
    private val showFloatingHeaderCallBack: () -> Unit,
    private val getVideoStreamCallback: (String, String) -> View?,
    private val getScreenShareVideoStreamRendererCallback: () -> VideoStreamRenderer?,
) : RelativeLayout(context) {

    init {
        inflate(context, R.layout.azure_communication_ui_participant_avatar_view, this)
        inflate(context, R.layout.azure_communication_ui_participant_video_view, this)
        createVideoView()
        createAvatarView()
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

        ParticipantGridCellAvatarView(
            avatarControl,
            participantAvatarSpeakingIndicator,
            participantAvatarContainer,
            displayNameAudioTextView,
            micIndicatorAudioImageView,
            participantViewModel,
            context,
            lifecycleScope,
        )
    }

    private fun createVideoView() {
        val participantVideoContainerFrameLayout: FrameLayout =
            findViewById(R.id.azure_communication_ui_participant_video_view_frame)

        val videoContainer: ConstraintLayout =
            findViewById(R.id.azure_communication_ui_participant_video_view_container)

        val displayNameOnVideoTextView: TextView =
            findViewById(R.id.azure_communication_ui_participant_view_on_video_display_name)

        val micIndicatorOnVideoImageView: ImageView =
            findViewById(R.id.azure_communication_ui_participant_view_on_video_mic_indicator)

        ParticipantGridCellVideoView(
            context,
            lifecycleScope,
            participantVideoContainerFrameLayout,
            videoContainer,
            displayNameOnVideoTextView,
            micIndicatorOnVideoImageView,
            participantViewModel,
            getVideoStreamCallback,
            showFloatingHeaderCallBack,
            getScreenShareVideoStreamRendererCallback
        )
    }
}
