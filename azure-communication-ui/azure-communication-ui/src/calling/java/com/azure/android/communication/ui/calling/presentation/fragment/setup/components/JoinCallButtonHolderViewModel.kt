// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class JoinCallButtonHolderViewModel(private val dispatch: (Action) -> Unit) {

    private lateinit var joinCallButtonEnabledFlow: MutableStateFlow<Boolean>
    private var disableJoinCallButtonFlow = MutableStateFlow(false)

    fun getJoinCallButtonEnabledFlow(): StateFlow<Boolean> = joinCallButtonEnabledFlow

    fun getDisableJoinCallButtonFlow(): StateFlow<Boolean> = disableJoinCallButtonFlow

    fun launchCallScreen() {
        dispatch(CallingAction.CallStartRequested())
        // disableJoinCallButtonFlow.value = true
    }

    fun init(audioPermissionState: PermissionStatus) {
        joinCallButtonEnabledFlow =
            MutableStateFlow(audioPermissionState == PermissionStatus.GRANTED)
        disableJoinCallButtonFlow.value = false
    }

    fun update(audioPermissionState: PermissionStatus, callingState: CallingState) {
        disableJoinCallButtonFlow.value = callingState.callingStatus != CallingStatus.NONE
        joinCallButtonEnabledFlow.value = audioPermissionState == PermissionStatus.GRANTED
        if (callingState.callingStatus == CallingStatus.CALL_EVICTED) {
            disableJoinCallButtonFlow.value = false
        } else {
            disableJoinCallButtonFlow.value =
                callingState.callingStatus != CallingStatus.NONE || callingState.joinCallIsRequested
        }
    }
}
