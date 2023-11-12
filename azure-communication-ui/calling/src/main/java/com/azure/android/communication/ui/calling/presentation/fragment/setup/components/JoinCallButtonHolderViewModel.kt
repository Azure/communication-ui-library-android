// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import android.media.AudioManager
import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.ErrorAction
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.isDisconnected
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class JoinCallButtonHolderViewModel(
    private val dispatch: (Action) -> Unit,
    private val audioManager: AudioManager
) {

    private lateinit var joinCallButtonEnabledFlow: MutableStateFlow<Boolean>
    private lateinit var callingStatusStateFlow: MutableStateFlow<CallingStatus>
    private var disableJoinCallButtonFlow = MutableStateFlow(false)
    private lateinit var networkManager: NetworkManager
    private var callType: CallType? = null

    fun getJoinCallButtonEnabledFlow(): StateFlow<Boolean> = joinCallButtonEnabledFlow

    fun getDisableJoinCallButtonFlow(): StateFlow<Boolean> = disableJoinCallButtonFlow

    fun getCallingStatusStateFlow(): StateFlow<CallingStatus> = callingStatusStateFlow

    fun getCallType(): CallType? = callType

    fun launchCallScreen() {
        val networkAvailable = isNetworkAvailable()
        // We try to check for mic availability for the current application through current audio mode
        val normalAudioMode = true // audioManager.mode == AudioManager.MODE_NORMAL

        if (!networkAvailable) {
            handleOffline()
        } else if (!normalAudioMode) {
            handleMicrophoneUnavailability()
        } else {
            dispatch(CallingAction.CallStartRequested())
            disableJoinCallButtonFlow.value = true
        }
    }

    fun init(
        audioPermissionState: PermissionStatus,
        cameraPermissionState: PermissionStatus,
        cameraOperationalStatus: CameraOperationalStatus,
        camerasCount: Int,
        networkManager: NetworkManager,
        callType: CallType? = null,
    ) {
        joinCallButtonEnabledFlow =
            MutableStateFlow(
                audioPermissionState == PermissionStatus.GRANTED &&
                    cameraPermissionState != PermissionStatus.UNKNOWN &&
                    (camerasCount == 0 || cameraOperationalStatus != CameraOperationalStatus.PENDING)
            )
        disableJoinCallButtonFlow.value = false
        this.networkManager = networkManager
        this.callType = callType
        callingStatusStateFlow = MutableStateFlow(CallingStatus.NONE)
    }

    fun update(
        audioPermissionState: PermissionStatus,
        callingState: CallingState,
        cameraPermissionState: PermissionStatus,
        cameraOperationalStatus: CameraOperationalStatus,
        camerasCount: Int,
    ) {
        disableJoinCallButtonFlow.value =
            callingState.callingStatus != CallingStatus.NONE

        joinCallButtonEnabledFlow.value =
            audioPermissionState == PermissionStatus.GRANTED &&
            cameraPermissionState != PermissionStatus.UNKNOWN &&
            (camerasCount == 0 || cameraOperationalStatus != CameraOperationalStatus.PENDING)

        if (callingState.isDisconnected()) {
            disableJoinCallButtonFlow.value = false
        } else {
            disableJoinCallButtonFlow.value =
                callingState.callingStatus != CallingStatus.NONE || callingState.joinCallIsRequested
        }
        callingStatusStateFlow.value = callingState.callingStatus
    }

    private fun handleOffline() {
        dispatch(ErrorAction.CallStateErrorOccurred(CallStateError(ErrorCode.NETWORK_NOT_AVAILABLE)))
    }

    private fun handleMicrophoneUnavailability() {
        dispatch(ErrorAction.CallStateErrorOccurred(CallStateError(ErrorCode.MICROPHONE_NOT_AVAILABLE)))
    }

    private fun isNetworkAvailable(): Boolean {
        return this.networkManager.isNetworkConnectionAvailable()
    }
}
