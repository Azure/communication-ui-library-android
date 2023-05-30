// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.ErrorAction
import com.azure.android.communication.ui.calling.redux.state.AudioFocusStatus
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.isDisconnected
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class JoinCallButtonHolderViewModel(private val dispatch: (Action) -> Unit) {

    private lateinit var joinCallButtonEnabledFlow: MutableStateFlow<Boolean>
    private var disableJoinCallButtonFlow = MutableStateFlow(false)

    fun getJoinCallButtonEnabledFlow(): StateFlow<Boolean> = joinCallButtonEnabledFlow

    fun getDisableJoinCallButtonFlow(): StateFlow<Boolean> = disableJoinCallButtonFlow

    fun launchCallScreen() {
        dispatch(CallingAction.CallStartRequested())
        disableJoinCallButtonFlow.value = true
    }

    fun init(
        audioPermissionState: PermissionStatus,
        cameraPermissionState: PermissionStatus,
        cameraOperationalStatus: CameraOperationalStatus,
        camerasCount: Int,
        audioFocusStatus: AudioFocusStatus?,
    ) {
        joinCallButtonEnabledFlow =
            MutableStateFlow(
                audioPermissionState == PermissionStatus.GRANTED &&
                    cameraPermissionState != PermissionStatus.UNKNOWN &&
                    (camerasCount == 0 || cameraOperationalStatus != CameraOperationalStatus.PENDING)
            )
        disableJoinCallButtonFlow.value = false
    }

    fun update(
        audioPermissionState: PermissionStatus,
        callingState: CallingState,
        cameraPermissionState: PermissionStatus,
        cameraOperationalStatus: CameraOperationalStatus,
        camerasCount: Int,
        audioFocusStatus: AudioFocusStatus?,
    ) {
        disableJoinCallButtonFlow.value =
            callingState.callingStatus != CallingStatus.NONE

        joinCallButtonEnabledFlow.value =
            audioPermissionState == PermissionStatus.GRANTED &&
            cameraPermissionState != PermissionStatus.UNKNOWN &&
            (camerasCount == 0 || cameraOperationalStatus != CameraOperationalStatus.PENDING) &&
            isAudioFocusLost(audioFocusStatus) == false
        if (callingState.isDisconnected()) {
            disableJoinCallButtonFlow.value = false
        } else {
            disableJoinCallButtonFlow.value =
                callingState.callingStatus != CallingStatus.NONE || callingState.joinCallIsRequested
        }
    }

    fun handleOffline() {
        dispatch(ErrorAction.CallStateErrorOccurred(CallStateError(ErrorCode.NETWORK_NOT_AVAILABLE)))
    }

    private fun isAudioFocusLost(audioFocusStatus: AudioFocusStatus?): Boolean {
        return audioFocusStatus != null && (
            audioFocusStatus == AudioFocusStatus.REJECTED ||
                audioFocusStatus == AudioFocusStatus.INTERRUPTED
            )
    }
}
