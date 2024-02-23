// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.localuser

import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CallStatus
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal enum class LocalParticipantViewMode {
    FULL_SCREEN,
    SELFIE_PIP,
}

internal class LocalParticipantViewModel(
    private val dispatch: (Action) -> Unit,
) {
    private lateinit var videoStatusFlow: MutableStateFlow<VideoModel>
    private lateinit var displayFullScreenAvatarFlow: MutableStateFlow<Boolean>
    private lateinit var displayNameStateFlow: MutableStateFlow<String?>
    private lateinit var localUserMutedStateFlow: MutableStateFlow<Boolean>
    private lateinit var displaySwitchCameraButtonFlow: MutableStateFlow<Boolean>
    private lateinit var displayPipSwitchCameraButtonFlow: MutableStateFlow<Boolean>
    private lateinit var enableCameraSwitchFlow: MutableStateFlow<Boolean>
    private lateinit var cameraDeviceSelectionFlow: MutableStateFlow<CameraDeviceSelectionStatus>
    private lateinit var isOverlayDisplayedFlow: MutableStateFlow<Boolean>
    private lateinit var numberOfRemoteParticipantsFlow: MutableStateFlow<Int>
    private lateinit var isVisibleFlow: MutableStateFlow<Boolean>

    fun getVideoStatusFlow(): StateFlow<VideoModel> = videoStatusFlow
    fun getDisplayFullScreenAvatarFlow(): StateFlow<Boolean> = displayFullScreenAvatarFlow
    fun getDisplayNameStateFlow(): StateFlow<String?> = displayNameStateFlow
    fun getLocalUserMutedStateFlow(): StateFlow<Boolean> = localUserMutedStateFlow
    fun getDisplaySwitchCameraButtonFlow(): StateFlow<Boolean> = displaySwitchCameraButtonFlow
    fun getDisplayPipSwitchCameraButtonFlow(): StateFlow<Boolean> = displayPipSwitchCameraButtonFlow
    fun getEnableCameraSwitchFlow(): StateFlow<Boolean> = enableCameraSwitchFlow
    fun getCameraDeviceSelectionFlow(): StateFlow<CameraDeviceSelectionStatus> =
        cameraDeviceSelectionFlow

    fun getIsVisibleFlow(): StateFlow<Boolean> = isVisibleFlow

    fun update(
        displayName: String?,
        audioOperationalStatus: AudioOperationalStatus,
        videoStreamID: String?,
        numberOfRemoteParticipants: Int,
        callingState: CallStatus,
        cameraDeviceSelectionStatus: CameraDeviceSelectionStatus,
        camerasCount: Int,
        pipStatus: VisibilityStatus,
        avMode: CallCompositeAudioVideoMode,
    ) {
        val viewMode = getLocalParticipantViewMode(numberOfRemoteParticipants)
        val displayVideo = shouldDisplayVideo(videoStreamID)
        val displayLobbyOverlay = shouldDisplayLobbyOverlay(callingState)
        val displayFullScreenAvatar =
            shouldDisplayFullScreenAvatar(displayVideo, displayLobbyOverlay, viewMode)

        videoStatusFlow.value = VideoModel(displayVideo, videoStreamID, viewMode)
        displayNameStateFlow.value = displayName
        localUserMutedStateFlow.value = audioOperationalStatus == AudioOperationalStatus.OFF
        displayFullScreenAvatarFlow.value = displayFullScreenAvatar
        displaySwitchCameraButtonFlow.value =
            displayVideo &&
            viewMode == LocalParticipantViewMode.FULL_SCREEN && camerasCount > 1 &&
            pipStatus == VisibilityStatus.VISIBLE
        displayPipSwitchCameraButtonFlow.value =
            displayVideo &&
            viewMode == LocalParticipantViewMode.SELFIE_PIP &&
            camerasCount > 1 &&
            pipStatus == VisibilityStatus.VISIBLE

        enableCameraSwitchFlow.value =
            cameraDeviceSelectionStatus != CameraDeviceSelectionStatus.SWITCHING &&
            callingState != CallStatus.LOCAL_HOLD
        cameraDeviceSelectionFlow.value = cameraDeviceSelectionStatus
        numberOfRemoteParticipantsFlow.value = numberOfRemoteParticipants

        isVisibleFlow.value = isVisible(displayVideo, pipStatus, displayFullScreenAvatar, avMode)
    }

    private fun isVisible(displayVideo: Boolean, pipStatus: VisibilityStatus, displayFullScreenAvatar: Boolean, avMode: CallCompositeAudioVideoMode): Boolean {
        if (avMode == CallCompositeAudioVideoMode.AUDIO_ONLY && !displayFullScreenAvatar) {
            return false
        }

        return if (pipStatus == VisibilityStatus.PIP_MODE_ENTERED) {
            displayVideo || displayFullScreenAvatar
        } else {
            true
        }
    }

    fun clear() {
        videoStatusFlow.value = VideoModel(false, null, LocalParticipantViewMode.FULL_SCREEN)
    }

    fun init(
        displayName: String?,
        audioOperationalStatus: AudioOperationalStatus,
        videoStreamID: String?,
        numberOfRemoteParticipants: Int,
        callingState: CallStatus,
        cameraDeviceSelectionStatus: CameraDeviceSelectionStatus,
        camerasCount: Int,
        pipStatus: VisibilityStatus,
        avMode: CallCompositeAudioVideoMode,
    ) {

        val viewMode = getLocalParticipantViewMode(numberOfRemoteParticipants)
        val displayVideo = shouldDisplayVideo(videoStreamID)
        val displayLobbyOverlay = shouldDisplayLobbyOverlay(callingState)
        val displayFullScreenAvatar =
            shouldDisplayFullScreenAvatar(displayVideo, displayLobbyOverlay, viewMode)

        videoStatusFlow = MutableStateFlow(VideoModel(displayVideo, videoStreamID, viewMode))
        displayNameStateFlow = MutableStateFlow(displayName)
        localUserMutedStateFlow =
            MutableStateFlow(audioOperationalStatus == AudioOperationalStatus.OFF)
        displayFullScreenAvatarFlow = MutableStateFlow(displayFullScreenAvatar)
        displaySwitchCameraButtonFlow =
            MutableStateFlow(
                displayVideo &&
                    viewMode == LocalParticipantViewMode.FULL_SCREEN && camerasCount > 1 &&
                    pipStatus == VisibilityStatus.VISIBLE
            )
        displayPipSwitchCameraButtonFlow =
            MutableStateFlow(displayVideo && viewMode == LocalParticipantViewMode.SELFIE_PIP && camerasCount > 1)
        enableCameraSwitchFlow = MutableStateFlow(
            cameraDeviceSelectionStatus != CameraDeviceSelectionStatus.SWITCHING
        )
        cameraDeviceSelectionFlow = MutableStateFlow(cameraDeviceSelectionStatus)
        isOverlayDisplayedFlow = MutableStateFlow(isOverlayDisplayed(callingState))
        numberOfRemoteParticipantsFlow = MutableStateFlow(numberOfRemoteParticipants)
        isVisibleFlow = MutableStateFlow(isVisible(displayVideo, pipStatus, displayFullScreenAvatar, avMode))
    }

    fun switchCamera() = dispatch(LocalParticipantAction.CameraSwitchTriggered())

    fun getIsOverlayDisplayedFlow(): StateFlow<Boolean> = isOverlayDisplayedFlow

    fun getNumberOfRemoteParticipantsFlow(): StateFlow<Int> = numberOfRemoteParticipantsFlow

    fun updateIsOverlayDisplayed(callStatus: CallStatus) {
        isOverlayDisplayedFlow.value = isOverlayDisplayed(callStatus)
    }

    private fun shouldDisplayVideo(videoStreamID: String?) = videoStreamID != null

    private fun shouldDisplayFullScreenAvatar(
        displayVideo: Boolean,
        displayLobbyOverlay: Boolean,
        viewMode: LocalParticipantViewMode,
    ) =
        !displayVideo && viewMode == LocalParticipantViewMode.FULL_SCREEN && !displayLobbyOverlay

    private fun shouldDisplayLobbyOverlay(callStatus: CallStatus) =
        callStatus == CallStatus.IN_LOBBY

    private fun getLocalParticipantViewMode(numberOfRemoteParticipants: Int): LocalParticipantViewMode {
        return if (numberOfRemoteParticipants > 0)
            LocalParticipantViewMode.SELFIE_PIP else LocalParticipantViewMode.FULL_SCREEN
    }

    private fun isOverlayDisplayed(callStatus: CallStatus) =
        callStatus == CallStatus.IN_LOBBY || callStatus == CallStatus.LOCAL_HOLD

    internal data class VideoModel(
        val shouldDisplayVideo: Boolean,
        val videoStreamID: String?,
        val viewMode: LocalParticipantViewMode,
    )
}
