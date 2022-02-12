package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.AvatarPersonaData
import com.azure.android.communication.ui.participant.CallCompositeParticipantHandler
import com.azure.android.communication.ui.redux.state.PermissionState
import com.azure.android.communication.ui.redux.state.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class SetupParticipantAvatarViewModel(private val callCompositeAvatarPersonaHandler: CallCompositeParticipantHandler) {
    private lateinit var displayName: String
    private lateinit var shouldDisplayAvatarViewStateFlow: MutableStateFlow<Boolean>
    private lateinit var avatarPersonaDataStateFlow: MutableStateFlow<AvatarPersonaData?>

    fun getDisplayName() = displayName

    fun getShouldDisplayAvatarViewStateFlow(): StateFlow<Boolean> {
        return shouldDisplayAvatarViewStateFlow
    }

    fun getAvatarPersonaDataStateFlow(): StateFlow<AvatarPersonaData?> {
        return avatarPersonaDataStateFlow
    }

    fun update(videoStreamID: String?, permissionState: PermissionState) {
        shouldDisplayAvatarViewStateFlow.value =
            shouldDisplayAvatarView(videoStreamID, permissionState)
    }

    fun init(displayName: String?, videoStreamID: String?, permissionState: PermissionState) {
        this.displayName = displayName ?: ""
        callCompositeAvatarPersonaHandler.getOnCallingParticipantHandler()?.let {
            avatarPersonaDataStateFlow = MutableStateFlow(it.onFetchRemoteParticipantAvatarData(""))
        }
        shouldDisplayAvatarViewStateFlow =
            MutableStateFlow(shouldDisplayAvatarView(videoStreamID, permissionState))
    }

    private fun shouldDisplayAvatarView(
        videoStreamID: String?,
        permissionState: PermissionState,
    ) = (permissionState.audioPermissionState != PermissionStatus.DENIED) &&
        (permissionState.cameraPermissionState != PermissionStatus.DENIED) &&
        videoStreamID.isNullOrEmpty()
}
