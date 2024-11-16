// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.DeviceConfigurationAction
import com.azure.android.communication.ui.calling.redux.state.DeviceConfigurationState

internal interface DeviceConfigurationReducer : Reducer<DeviceConfigurationState>
internal class DeviceConfigurationReducerImpl : DeviceConfigurationReducer {
    override fun reduce(state: DeviceConfigurationState, action: Action): DeviceConfigurationState {
        return when (action) {
            is DeviceConfigurationAction.KeyboardVisibilityChanged -> {
                state.copy(isSoftwareKeyboardVisible = action.isSoftwareKeyboardVisible)
            }
            is DeviceConfigurationAction.IsTableChanged -> {
                state.copy(isTablet = action.isTablet)
            }
            is DeviceConfigurationAction.IsPortraitChanged -> {
                state.copy(isPortrait = action.isPortrait)
            }
            else -> state
        }
    }
}