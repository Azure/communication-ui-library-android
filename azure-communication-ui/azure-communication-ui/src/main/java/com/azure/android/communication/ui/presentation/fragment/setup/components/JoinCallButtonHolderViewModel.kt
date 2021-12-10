package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.NavigationAction
import com.azure.android.communication.ui.redux.state.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class JoinCallButtonHolderViewModel(private val dispatch: (Action) -> Unit) {

    private lateinit var joinCallButtonEnabledFlow: MutableStateFlow<Boolean>

    fun getJoinCallButtonEnabledFlow(): StateFlow<Boolean> {
        return joinCallButtonEnabledFlow
    }

    fun launchCallScreen() {
        dispatch(NavigationAction.CallLaunched())
    }

    fun init(audioPermissionState: PermissionStatus) {
        joinCallButtonEnabledFlow =
            MutableStateFlow(audioPermissionState == PermissionStatus.GRANTED)
    }

    fun update(audioPermissionState: PermissionStatus) {
        joinCallButtonEnabledFlow.value = audioPermissionState == PermissionStatus.GRANTED
    }
}
