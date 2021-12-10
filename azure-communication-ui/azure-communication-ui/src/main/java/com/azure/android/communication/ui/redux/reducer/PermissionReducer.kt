// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.reducer

import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.PermissionAction
import com.azure.android.communication.ui.redux.state.PermissionState
import com.azure.android.communication.ui.redux.state.PermissionStatus

internal interface PermissionStateReducer : Reducer<PermissionState>

internal class PermissionStateReducerImpl :
    PermissionStateReducer {
    override fun reduce(state: PermissionState, action: Action): PermissionState {
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
