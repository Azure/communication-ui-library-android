// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.PermissionAction
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus

internal interface PermissionStateReducer : Reducer<PermissionState>

internal class PermissionStateReducerImpl :
    PermissionStateReducer {
    override fun reduce(state: PermissionState, action: Action): PermissionState {
        return when (action) {
            is PermissionAction.AudioPermissionIsSet -> {
                state.copy(audioPermissionState = action.permissionState)
            }
            is PermissionAction.CameraPermissionIsSet -> {
                state.copy(cameraPermissionState = action.permissionState)
            }
            is PermissionAction.AudioPermissionRequested -> {
                state.copy(audioPermissionState = PermissionStatus.REQUESTING)
            }
            is PermissionAction.CameraPermissionRequested -> {
                state.copy(cameraPermissionState = PermissionStatus.REQUESTING)
            }
            else -> state
        }
    }
}
