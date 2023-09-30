// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.calling.redux.state.LocalUserState

internal interface LocalParticipantStateReducer : Reducer<LocalUserState>

internal class LocalParticipantStateReducerImpl : LocalParticipantStateReducer {

    override fun reduce(localUserState: LocalUserState, action: Action): LocalUserState {
        return when (action) {
            is LocalParticipantAction.CameraOnRequested -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(operation = CameraOperationalStatus.PENDING)
                )
            }
            is LocalParticipantAction.CameraOnTriggered -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(operation = CameraOperationalStatus.PENDING)
                )
            }
            is LocalParticipantAction.CameraOnSucceeded -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(
                        operation = CameraOperationalStatus.ON,
                        error = null
                    ),
                    videoStreamID = action.videoStreamID
                )
            }
            is LocalParticipantAction.CameraOnFailed -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(
                        operation = CameraOperationalStatus.OFF,
                        error = action.error
                    )
                )
            }

            is LocalParticipantAction.CameraOffTriggered -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(operation = CameraOperationalStatus.PENDING)
                )
            }
            is LocalParticipantAction.CameraOffSucceeded -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(operation = CameraOperationalStatus.OFF),
                    videoStreamID = null
                )
            }
            is LocalParticipantAction.CameraOffFailed -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(
                        operation = CameraOperationalStatus.ON,
                        error = action.error
                    )
                )
            }

            is LocalParticipantAction.CameraSwitchTriggered -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(device = CameraDeviceSelectionStatus.SWITCHING)
                )
            }
            is LocalParticipantAction.CameraSwitchSucceeded -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(
                        device = action.cameraDeviceSelectionStatus,
                    )
                )
            }
            is LocalParticipantAction.CameraSwitchFailed -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(
                        device = action.previousDevice,
                        error = action.error,
                    )
                )
            }

            is LocalParticipantAction.CameraPreviewOnRequested -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(operation = CameraOperationalStatus.PENDING)
                )
            }

            is LocalParticipantAction.CameraPreviewOnTriggered -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(operation = CameraOperationalStatus.PENDING)
                )
            }
            is LocalParticipantAction.CameraPreviewOnSucceeded -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(
                        operation = CameraOperationalStatus.ON,
                        error = null
                    ),
                    videoStreamID = action.videoStreamID
                )
            }
            is LocalParticipantAction.CameraPreviewOnFailed -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(
                        operation = CameraOperationalStatus.OFF,
                        error = action.error
                    )
                )
            }

            is LocalParticipantAction.CameraPreviewOffTriggered -> {
                localUserState.copy(
                    // in this case we go straight OFF
                    cameraState = localUserState.cameraState.copy(operation = CameraOperationalStatus.OFF),
                    videoStreamID = null
                )
            }
            is LocalParticipantAction.CameraPauseFailed -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(error = action.error)
                )
            }
            is LocalParticipantAction.CameraPauseSucceeded -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(operation = CameraOperationalStatus.PAUSED),
                    videoStreamID = null
                )
            }
            is LocalParticipantAction.CamerasCountUpdated -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(camerasCount = action.count)
                )
            }

            is LocalParticipantAction.MicPreviewOffTriggered -> {
                localUserState.copy(audioState = localUserState.audioState.copy(operation = AudioOperationalStatus.OFF))
            }
            is LocalParticipantAction.MicPreviewOnTriggered -> {
                localUserState.copy(audioState = localUserState.audioState.copy(operation = AudioOperationalStatus.ON))
            }
            is LocalParticipantAction.MicOffTriggered -> {
                localUserState.copy(audioState = localUserState.audioState.copy(operation = AudioOperationalStatus.PENDING))
            }
            is LocalParticipantAction.AudioStateOperationUpdated -> {
                localUserState.copy(
                    audioState = localUserState.audioState.copy(
                        operation = action.audioOperationalStatus,
                        error = null
                    )
                )
            }
            is LocalParticipantAction.MicOffFailed -> {
                localUserState.copy(
                    audioState = localUserState.audioState.copy(
                        operation = AudioOperationalStatus.ON,
                        error = action.error
                    )
                )
            }
            is LocalParticipantAction.MicOnTriggered -> {
                localUserState.copy(audioState = localUserState.audioState.copy(operation = AudioOperationalStatus.PENDING))
            }
            is LocalParticipantAction.MicOnFailed -> {
                localUserState.copy(
                    audioState = localUserState.audioState.copy(
                        operation = AudioOperationalStatus.OFF,
                        error = action.error
                    )
                )
            }
            is LocalParticipantAction.AudioDeviceBluetoothSCOAvailable -> {
                localUserState.copy(
                    audioState = localUserState.audioState.copy(
                        bluetoothState = localUserState.audioState.bluetoothState.copy(
                            available = action.available,
                            deviceName = action.deviceName
                        )

                    )
                )
            }
            is LocalParticipantAction.AudioDeviceHeadsetAvailable -> {
                localUserState.copy(
                    audioState = localUserState.audioState.copy(
                        isHeadphonePlugged = action.available
                    )
                )
            }
            is LocalParticipantAction.AudioDeviceChangeRequested -> {

                localUserState.copy(
                    audioState = localUserState.audioState.copy(
                        device = action.requestedAudioDevice,
                        error = null
                    )
                )
            }

            is LocalParticipantAction.AudioDeviceChangeSucceeded -> {
                localUserState.copy(
                    audioState = localUserState.audioState.copy(
                        device = action.selectedAudioDevice,
                        error = null
                    )
                )
            }
            is LocalParticipantAction.AudioDeviceChangeFailed -> {
                localUserState.copy(
                    audioState = localUserState.audioState.copy(
                        device = action.previousDevice,
                        error = action.error
                    )
                )
            }
            is LocalParticipantAction.DisplayNameIsSet -> {
                localUserState.copy(
                    displayName = action.displayName
                )
            }
            is NavigationAction.CallLaunched, is NavigationAction.CallLaunchWithoutSetup -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(
                        transmission = CameraTransmissionStatus.REMOTE
                    )
                )
            }
            is NavigationAction.SetupLaunched -> {
                localUserState.copy(
                    cameraState = localUserState.cameraState.copy(
                        transmission = CameraTransmissionStatus.LOCAL
                    )
                )
            }
            is LocalParticipantAction.RoleChanged -> {
                localUserState.copy(
                    localParticipantRole = action.callCompositeParticipantRole
                )
            }
            else -> localUserState
        }
    }
}
