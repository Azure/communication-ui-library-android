// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import android.content.Context
import android.media.AudioManager
import android.os.Build
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
import com.azure.android.communication.ui.calling.telecom.TelecomConnectionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class JoinCallButtonHolderViewModel(
    private val dispatch: (Action) -> Unit,
    private val audioManager: AudioManager,
    private val instanceId: Int
) {

    private lateinit var joinCallButtonEnabledFlow: MutableStateFlow<Boolean>
    private var disableJoinCallButtonFlow = MutableStateFlow(false)
    private lateinit var networkManager: NetworkManager

    fun getJoinCallButtonEnabledFlow(): StateFlow<Boolean> = joinCallButtonEnabledFlow

    fun getDisableJoinCallButtonFlow(): StateFlow<Boolean> = disableJoinCallButtonFlow

    suspend fun launchCallScreen(context: Context) {
        val networkAvailable = isNetworkAvailable()
        // We try to check for mic availability for the current application through current audio mode
        val normalAudioMode = audioManager.mode == AudioManager.MODE_NORMAL

//        if (!networkAvailable) {
//            handleOffline()
//        } else if (!normalAudioMode) {
//            handleMicrophoneUnavailability()
//        } else {
        dispatch(CallingAction.CallStartRequested())
        disableJoinCallButtonFlow.value = true
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // phoneAccountId will be received via public API
            val telecomConnectionManager = TelecomConnectionManager(
                context,
                "9a9a0260-1c18-11ec-ba20-e761da70b03f",
                instanceId = instanceId
            )
            telecomConnectionManager.startIncomingConnection(context, "fromDisplayName", true)
//            telecomConnectionManager.startOutgoingConnection(context,"toDisplayName", true)
        }
    }

    fun init(
        audioPermissionState: PermissionStatus,
        cameraPermissionState: PermissionStatus,
        cameraOperationalStatus: CameraOperationalStatus,
        camerasCount: Int,
        networkManager: NetworkManager,
    ) {
        joinCallButtonEnabledFlow =
            MutableStateFlow(
                audioPermissionState == PermissionStatus.GRANTED &&
                    cameraPermissionState != PermissionStatus.UNKNOWN &&
                    (camerasCount == 0 || cameraOperationalStatus != CameraOperationalStatus.PENDING)
            )
        disableJoinCallButtonFlow.value = false
        this.networkManager = networkManager
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
    }

    fun handleOffline() {
        dispatch(ErrorAction.CallStateErrorOccurred(CallStateError(ErrorCode.NETWORK_NOT_AVAILABLE)))
    }

    fun handleMicrophoneUnavailability() {
        dispatch(ErrorAction.CallStateErrorOccurred(CallStateError(ErrorCode.MICROPHONE_NOT_AVAILABLE)))
    }

    fun isNetworkAvailable(): Boolean {
        return this.networkManager.isNetworkConnectionAvailable()
    }
}
