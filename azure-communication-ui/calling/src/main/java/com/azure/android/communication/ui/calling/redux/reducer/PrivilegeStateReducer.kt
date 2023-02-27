// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.PrivilegeAction
import com.azure.android.communication.ui.calling.redux.state.PrivilegeState

internal interface PrivilegeStateReducer : Reducer<PrivilegeState>

internal class PrivilegeStateReducerImpl :
    PrivilegeStateReducer {
    override fun reduce(state: PrivilegeState, action: Action): PrivilegeState {
        return when (action) {
            is PrivilegeAction.CanUseCameraIsSet -> {
                state.copy(canUseCamera = action.canUseCamera)
            }
            is PrivilegeAction.CanUseMicIsSet -> {
                state.copy(canUseMicrophone = action.canUseMic)
            }
            else -> state
        }
    }
}
