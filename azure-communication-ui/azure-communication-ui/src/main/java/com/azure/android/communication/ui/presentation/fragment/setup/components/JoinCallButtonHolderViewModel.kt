// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.CallingAction
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class JoinCallButtonHolderViewModel(private val dispatch: (Action) -> Unit) {

    private lateinit var joinCallButtonEnabledFlow: MutableStateFlow<Boolean>
    private var disableJoinCallButtonFlow = MutableStateFlow(false)

    // due to the async nature of the CallingStatus update we need to disable Join button before we
    // receive CallingStatus.CONNECTING from the SDK.
    private var shouldBlockJoinButton = false

    fun getJoinCallButtonEnabledFlow(): StateFlow<Boolean> = joinCallButtonEnabledFlow

    fun getDisableJoinCallButtonFlow(): StateFlow<Boolean> = disableJoinCallButtonFlow

    fun launchCallScreen() {
        dispatch(CallingAction.CallStartRequested())
        disableJoinCallButtonFlow.value = true
        shouldBlockJoinButton = true
    }

    fun init(audioPermissionState: PermissionStatus) {
        joinCallButtonEnabledFlow =
            MutableStateFlow(audioPermissionState == PermissionStatus.GRANTED)
        disableJoinCallButtonFlow.value = false
        shouldBlockJoinButton = false
    }

    fun update(audioPermissionState: PermissionStatus, callingStatus: CallingStatus) {
        joinCallButtonEnabledFlow.value = audioPermissionState == PermissionStatus.GRANTED

        if (callingStatus == CallingStatus.CONNECTING) {
            // once we receive CONNECTING status we can rely on it to set value to joiningCallInProgress
            shouldBlockJoinButton = false
        }
        disableJoinCallButtonFlow.value =
            callingStatus != CallingStatus.NONE || shouldBlockJoinButton
    }
}
