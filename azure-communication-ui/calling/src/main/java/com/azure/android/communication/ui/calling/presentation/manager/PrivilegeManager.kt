// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.models.CallCompositeRoomRole
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.PrivilegeAction
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class PrivilegeManager(
    private val store: Store<ReduxState>,
) {

    private var roomRoleStateFlow = MutableStateFlow<CallCompositeRoomRole?>(null)

    fun start(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            store.getStateFlow().collect {
                roomRoleStateFlow.value = it.callState.roomRole
            }
        }

        coroutineScope.launch {
            roomRoleStateFlow.collect {
                it?.let { onRoleChanged(it) }
            }
        }
    }

    private fun onRoleChanged(roomRole: CallCompositeRoomRole) {
        val canUseCameraAndMic = roomRole == CallCompositeRoomRole.ATTENDEE ||
            roomRole == CallCompositeRoomRole.PRESENTER
        store.dispatch(PrivilegeAction.CanUseMicIsSet(canUseCameraAndMic))
        store.dispatch(PrivilegeAction.CanUseCameraIsSet(canUseCameraAndMic))
    }
}
