// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class PermissionWarningViewModel(private val dispatch: (Action) -> Unit) {
    private lateinit var cameraPermissionStateFlow: MutableStateFlow<PermissionStatus>
    private lateinit var audioPermissionStateFlow: MutableStateFlow<PermissionStatus>

    fun getCameraPermissionStateFlow(): StateFlow<PermissionStatus> {
        return cameraPermissionStateFlow
    }

    fun getAudioPermissionStateFlow(): StateFlow<PermissionStatus> {
        return audioPermissionStateFlow
    }

    fun update(permissionState: PermissionState) {
        cameraPermissionStateFlow.value = permissionState.cameraPermissionState
        audioPermissionStateFlow.value = permissionState.audioPermissionState
    }

    fun init(permissionState: PermissionState) {
        cameraPermissionStateFlow = MutableStateFlow(permissionState.cameraPermissionState)
        audioPermissionStateFlow = MutableStateFlow(permissionState.audioPermissionState)
    }

    fun turnCameraOn() {
        dispatchAction(action = LocalParticipantAction.CameraPreviewOnRequested())
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }
}
