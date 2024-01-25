// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid

import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.models.VideoStreamModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ParticipantGridCellViewModel(
    userIdentifier: String,
    displayName: String,
    cameraVideoStreamModel: VideoStreamModel?,
    screenShareVideoStreamModel: VideoStreamModel?,
    isCameraDisabled: Boolean,
    isMuted: Boolean,
    isSpeaking: Boolean,
    modifiedTimestamp: Number,
    participantStatus: ParticipantStatus?,
) {
    private var isOnHoldStateFlow = MutableStateFlow(isOnHold(participantStatus))
    private var displayNameStateFlow = MutableStateFlow(displayName)
    private var isMutedStateFlow = MutableStateFlow(isMuted)
    private var isSpeakingStateFlow = MutableStateFlow(isSpeaking && !isMuted)
    private var isNameIndicatorVisibleStateFlow = MutableStateFlow(true)
    private var videoViewModelStateFlow = MutableStateFlow(
        getVideoStreamModel(
            createVideoViewModel(cameraVideoStreamModel),
            createVideoViewModel(screenShareVideoStreamModel),
            isOnHoldStateFlow.value,
            true,
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

    fun getIsOnHoldStateFlow(): StateFlow<Boolean> {
        return isOnHoldStateFlow
    }

    fun update(
        participant: ParticipantInfoModel,
    ) {
        this.participantUserIdentifier = participant.userIdentifier
        this.displayNameStateFlow.value = participant.displayName
        this.isMutedStateFlow.value = participant.isMuted
        this.isOnHoldStateFlow.value = isOnHold(participant.participantStatus)

        this.isNameIndicatorVisibleStateFlow.value =
            !(participant.displayName.isBlank() && !participant.isMuted)

        this.videoViewModelStateFlow.value = getVideoStreamModel(
            createVideoViewModel(participant.cameraVideoStreamModel),
            createVideoViewModel(participant.screenShareVideoStreamModel),
            this.isOnHoldStateFlow.value,
            participant.isCameraDisabled
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
        isOnHold: Boolean,
        isCameraDisabled: Boolean
    ): VideoViewModel? {
        if (isOnHold) return null
        if (screenShareVideoStreamModel != null) return screenShareVideoStreamModel
        if (isCameraDisabled) return null
        if (cameraVideoStreamModel != null) return cameraVideoStreamModel
        return null
    }

    private fun isOnHold(participantStatus: ParticipantStatus?) =
        participantStatus == ParticipantStatus.HOLD
}
