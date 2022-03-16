// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid

import com.azure.android.communication.ui.model.ParticipantInfoModel
import com.azure.android.communication.ui.model.VideoStreamModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ParticipantGridCellViewModel(
    userIdentifier: String,
    displayName: String,
    cameraVideoStreamModel: VideoStreamModel?,
    screenShareVideoStreamModel: VideoStreamModel?,
    isMuted: Boolean,
    isSpeaking: Boolean,
    modifiedTimestamp: Number,
) {
    private var displayNameStateFlow = MutableStateFlow(displayName)
    private var isMutedStateFlow = MutableStateFlow(isMuted)
    private var isSpeakingStateFlow = MutableStateFlow(isSpeaking && !isMuted)
    private var isNameIndicatorVisibleStateFlow = MutableStateFlow(true)
    private var videoViewModelStateFlow = MutableStateFlow(
        getVideoStreamModel(
            createVideoViewModel(cameraVideoStreamModel),
            createVideoViewModel(screenShareVideoStreamModel)
        )
    )
    private var participantModifiedTimestamp = modifiedTimestamp
    private var participantUserIdentifier = userIdentifier

    fun getParticipantUserIdentifier(): String {
        return participantUserIdentifier
    }

    fun getDisplayNameStateFlow(): StateFlow<String> {
        return displayNameStateFlow
    }

    fun getIsMutedStateFlow(): StateFlow<Boolean> {
        return isMutedStateFlow
    }

    fun getIsNameIndicatorVisibleStateFlow(): StateFlow<Boolean> {
        return isNameIndicatorVisibleStateFlow
    }

    fun getIsSpeakingStateFlow(): StateFlow<Boolean> {
        return isSpeakingStateFlow
    }

    fun getVideoViewModelStateFlow(): StateFlow<VideoViewModel?> {
        return videoViewModelStateFlow
    }

    fun getParticipantModifiedTimestamp(): Number {
        return participantModifiedTimestamp
    }

    fun update(participant: ParticipantInfoModel) {
        this.participantUserIdentifier = participant.userIdentifier
        this.displayNameStateFlow.value = participant.displayName
        this.isMutedStateFlow.value = participant.isMuted

        this.isNameIndicatorVisibleStateFlow.value =
            !(participant.displayName.isBlank() && !participant.isMuted)

        this.videoViewModelStateFlow.value = getVideoStreamModel(
            createVideoViewModel(participant.cameraVideoStreamModel),
            createVideoViewModel(participant.screenShareVideoStreamModel)
        )

        this.isSpeakingStateFlow.value = participant.isSpeaking && !participant.isMuted
        this.participantModifiedTimestamp = participant.modifiedTimestamp
    }

    private fun createVideoViewModel(videoStreamModel: VideoStreamModel?): VideoViewModel? {
        videoStreamModel?.let {
            return VideoViewModel(videoStreamModel.videoStreamID, videoStreamModel.streamType)
        }
        return null
    }

    private fun getVideoStreamModel(
        cameraVideoStreamModel: VideoViewModel?,
        screenShareVideoStreamModel: VideoViewModel?,
    ): VideoViewModel? {
        if (screenShareVideoStreamModel != null) return screenShareVideoStreamModel
        if (cameraVideoStreamModel != null) return cameraVideoStreamModel
        return null
    }
}
