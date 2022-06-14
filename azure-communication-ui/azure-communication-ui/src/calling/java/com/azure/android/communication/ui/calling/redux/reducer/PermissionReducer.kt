// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.PermissionAction
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import org.reduxkotlin.Reducer


internal class PermissionStateReducer : Reducer<PermissionState> {
    override fun invoke(state: PermissionState, action: Any): PermissionState {
        return when (action) {
            is PermissionAction.AudioPermissionIsSet -> {
                PermissionState(action.permissionState, state.cameraPermissionState)
            }
            is PermissionAction.CameraPermissionIsSet -> {
                PermissionState(state.audioPermissionState, action.permissionState)
            }
            is PermissionAction.AudioPermissionRequested -> {
                PermissionState(PermissionStatus.REQUESTING, state.cameraPermissionState)
            }
            is PermissionAction.CameraPermissionRequested -> {
                PermissionState(state.audioPermissionState, PermissionStatus.REQUESTING)
            }
            else -> state
        }
    }
}
