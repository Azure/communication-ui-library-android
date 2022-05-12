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
                PermissionState(action.permissionState, state.cameraPermissionState, state.phonePermissionState)
            }
            is PermissionAction.CameraPermissionIsSet -> {
                PermissionState(state.micPermissionState, action.permissionState, state.phonePermissionState)
            }
            is PermissionAction.PhonePermissionIsSet -> {
                PermissionState(state.micPermissionState, state.cameraPermissionState, action.permissionState)
            }
            is PermissionAction.AudioPermissionRequested -> {
                PermissionState(PermissionStatus.REQUESTING, state.cameraPermissionState, state.phonePermissionState)
            }
            is PermissionAction.CameraPermissionRequested -> {
                PermissionState(state.micPermissionState, PermissionStatus.REQUESTING, state.phonePermissionState)
            }
            is PermissionAction.PhonePermissionRequested -> {
                PermissionState(state.micPermissionState, state.cameraPermissionState, PermissionStatus.REQUESTING)
            }
            else -> state
        }
    }
}
