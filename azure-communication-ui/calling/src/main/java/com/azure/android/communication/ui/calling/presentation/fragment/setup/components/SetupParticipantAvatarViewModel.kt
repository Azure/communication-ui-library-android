// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class SetupParticipantAvatarViewModel {
    private lateinit var displayName: String
    private lateinit var shouldDisplayAvatarViewStateFlow: MutableStateFlow<Boolean>

    fun getDisplayName() = displayName

    fun getShouldDisplayAvatarViewStateFlow(): StateFlow<Boolean> {
        return shouldDisplayAvatarViewStateFlow
    }

    fun update(
        videoStreamID: String?,
        permissionState: PermissionState,
    ) {
        shouldDisplayAvatarViewStateFlow.value =
            shouldDisplayAvatarView(videoStreamID, permissionState)
    }

    fun init(
        displayName: String?,
        videoStreamID: String?,
        permissionState: PermissionState,
    ) {
        this.displayName = displayName ?: ""
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
