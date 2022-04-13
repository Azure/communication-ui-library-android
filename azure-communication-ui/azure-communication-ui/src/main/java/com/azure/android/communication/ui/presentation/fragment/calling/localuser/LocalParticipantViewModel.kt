// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.localuser

import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.CameraDeviceSelectionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal enum class LocalParticipantViewMode {
    FULL_SCREEN,
    PIP,
}

internal class LocalParticipantViewModel(private val dispatch: (Action) -> Unit) {
    private lateinit var videoStatusFlow: MutableStateFlow<VideoModel>
    private lateinit var displayFullScreenAvatarFlow: MutableStateFlow<Boolean>
    private lateinit var displayNameStateFlow: MutableStateFlow<String?>
    private lateinit var localUserMutedStateFlow: MutableStateFlow<Boolean>
    private lateinit var displaySwitchCameraButtonFlow: MutableStateFlow<Boolean>
    private lateinit var displayPipSwitchCameraButtonFlow: MutableStateFlow<Boolean>
    private lateinit var enableCameraSwitchFlow: MutableStateFlow<Boolean>
    private lateinit var cameraDeviceSelectionFlow: MutableStateFlow<CameraDeviceSelectionStatus>
    private lateinit var isLobbyOverlayDisplayedFlow: MutableStateFlow<Boolean>

    fun getVideoStatusFlow(): StateFlow<VideoModel> = videoStatusFlow
    fun getDisplayFullScreenAvatarFlow(): StateFlow<Boolean> = displayFullScreenAvatarFlow
    fun getDisplayNameStateFlow(): StateFlow<String?> = displayNameStateFlow
    fun getLocalUserMutedStateFlow(): StateFlow<Boolean> = localUserMutedStateFlow
    fun getDisplaySwitchCameraButtonFlow(): StateFlow<Boolean> = displaySwitchCameraButtonFlow
    fun getDisplayPipSwitchCameraButtonFlow(): StateFlow<Boolean> = displayPipSwitchCameraButtonFlow
    fun getEnableCameraSwitchFlow(): StateFlow<Boolean> = enableCameraSwitchFlow
    fun getCameraDeviceSelectionFlow(): StateFlow<CameraDeviceSelectionStatus> = cameraDeviceSelectionFlow

    fun update(
        displayName: String?,
        audioOperationalStatus: AudioOperationalStatus,
        videoStreamID: String?,
        numberOfRemoteParticipants: Int,
        callingState: CallingStatus,
        cameraDeviceSelectionStatus: CameraDeviceSelectionStatus,
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
            displayVideo && viewMode == LocalParticipantViewMode.FULL_SCREEN
        displayPipSwitchCameraButtonFlow.value =
            displayVideo && viewMode == LocalParticipantViewMode.PIP
        enableCameraSwitchFlow.value =
            cameraDeviceSelectionStatus != CameraDeviceSelectionStatus.SWITCHING
        cameraDeviceSelectionFlow.value = cameraDeviceSelectionStatus
    }

    fun clear() {
        videoStatusFlow.value = VideoModel(false, null, LocalParticipantViewMode.FULL_SCREEN)
    }

    fun init(
        displayName: String?,
        audioOperationalStatus: AudioOperationalStatus,
        videoStreamID: String?,
        numberOfRemoteParticipants: Int,
        callingState: CallingStatus,
        cameraDeviceSelectionStatus: CameraDeviceSelectionStatus,
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
            MutableStateFlow(displayVideo && viewMode == LocalParticipantViewMode.FULL_SCREEN)
        displayPipSwitchCameraButtonFlow =
            MutableStateFlow(displayVideo && viewMode == LocalParticipantViewMode.PIP)
        enableCameraSwitchFlow = MutableStateFlow(
            cameraDeviceSelectionStatus != CameraDeviceSelectionStatus.SWITCHING
        )
        cameraDeviceSelectionFlow = MutableStateFlow(cameraDeviceSelectionStatus)
        isLobbyOverlayDisplayedFlow = MutableStateFlow(isLobbyOverlayDisplayed(callingState))
    }

    fun switchCamera() = dispatch(LocalParticipantAction.CameraSwitchTriggered())

    fun getIsLobbyOverlayDisplayedFlow(): StateFlow<Boolean> = isLobbyOverlayDisplayedFlow

    fun updateIsLobbyOverlayDisplayed(callingStatus: CallingStatus) {
        isLobbyOverlayDisplayedFlow.value = isLobbyOverlayDisplayed(callingStatus)
    }

    private fun shouldDisplayVideo(videoStreamID: String?) = videoStreamID != null

    private fun shouldDisplayFullScreenAvatar(
        displayVideo: Boolean,
        displayLobbyOverlay: Boolean,
        viewMode: LocalParticipantViewMode,
    ) =
        !displayVideo && viewMode == LocalParticipantViewMode.FULL_SCREEN && !displayLobbyOverlay

    private fun shouldDisplayLobbyOverlay(callingStatus: CallingStatus) =
        callingStatus == CallingStatus.IN_LOBBY

    private fun getLocalParticipantViewMode(numberOfRemoteParticipants: Int): LocalParticipantViewMode {
        return if (numberOfRemoteParticipants > 0)
            LocalParticipantViewMode.PIP else LocalParticipantViewMode.FULL_SCREEN
    }

    private fun isLobbyOverlayDisplayed(callingStatus: CallingStatus) =
        callingStatus == CallingStatus.IN_LOBBY

    internal data class VideoModel(
        val shouldDisplayVideo: Boolean,
        val videoStreamID: String?,
        val viewMode: LocalParticipantViewMode,
    )
}
