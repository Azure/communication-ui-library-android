// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.reducer

import com.azure.android.communication.ui.calling.error.CallCompositeError
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantRole
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.reducer.LocalParticipantStateReducerImpl
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.BluetoothState
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.calling.redux.state.LocalUserState
import org.junit.Assert
import org.junit.Test

internal class LocalParticipantReduxStateReducerUnitTest {
    @Test
    fun deviceStateReducer_reduce_when_CameraOnRequested_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val action = LocalParticipantAction.CameraOnRequested()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CameraOperationalStatus.PENDING, newState.cameraState.operation)
    }

    @Test
    fun deviceStateReducer_reduce_when_CameraOnTriggered_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val action = LocalParticipantAction.CameraOnTriggered()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CameraOperationalStatus.PENDING, newState.cameraState.operation)
    }

    @Test
    fun deviceStateReducer_reduce_when_CameraOnSucceeded_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PENDING,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val videoStreamId = "some_videoStreamId"
        val action = LocalParticipantAction.CameraOnSucceeded(videoStreamId)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CameraOperationalStatus.ON, newState.cameraState.operation)
        Assert.assertEquals(null, newState.cameraState.error)
        Assert.assertEquals(videoStreamId, newState.videoStreamID)
    }

    @Test
    fun deviceStateReducer_reduce_when_CameraOnFailed_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PENDING,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val error =
            CallCompositeError(
                ErrorCode.TURN_CAMERA_ON_FAILED,
                Throwable("CameraOn has failed")
            )
        val action = LocalParticipantAction.CameraOnFailed(error)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CameraOperationalStatus.OFF, newState.cameraState.operation)
        Assert.assertEquals(error, newState.cameraState.error)
    }

    @Test
    fun deviceStateReducer_reduce_when_CameraOffTriggered_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val action = LocalParticipantAction.CameraOffTriggered()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CameraOperationalStatus.PENDING, newState.cameraState.operation)
    }

    @Test
    fun deviceStateReducer_reduce_when_CamerasCountTriggered_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL,
                4,
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val action = LocalParticipantAction.CamerasCountUpdated(8)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(8, newState.cameraState.camerasCount)
    }

    @Test
    fun deviceStateReducer_reduce_when_CameraOffSucceeded_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PENDING,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = "some video streamId",
            displayName = null,
            localParticipantRole = null
        )
        val action = LocalParticipantAction.CameraOffSucceeded()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CameraOperationalStatus.OFF, newState.cameraState.operation)
        Assert.assertEquals(null, newState.cameraState.error)
        Assert.assertEquals(null, newState.videoStreamID)
    }

    @Test
    fun deviceStateReducer_reduce_when_CameraOffFailed_then_changeState() {

        // arrange
        val videoStreamId = "some videoStreamId"
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PENDING,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamId,
            null,
            localParticipantRole = null
        )
        val error = CallCompositeError(
            ErrorCode.TURN_CAMERA_OFF_FAILED,
            Throwable("CameraOff failed")
        )
        val action = LocalParticipantAction.CameraOffFailed(error)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CameraOperationalStatus.ON, newState.cameraState.operation)
        Assert.assertEquals(error, newState.cameraState.error)
        Assert.assertEquals(videoStreamId, newState.videoStreamID)
    }

    @Test
    fun deviceStateReducer_reduce_when_CameraPreviewOnRequested_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val action = LocalParticipantAction.CameraPreviewOnRequested()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CameraOperationalStatus.PENDING, newState.cameraState.operation)
    }

    @Test
    fun deviceStateReducer_reduce_when_CameraPreviewOnTriggered_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val action = LocalParticipantAction.CameraPreviewOnTriggered()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CameraOperationalStatus.PENDING, newState.cameraState.operation)
    }

    @Test
    fun deviceStateReducer_reduce_when_CameraPreviewOnSucceeded_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val videoStreamId = "some_videoStreamId"
        val action = LocalParticipantAction.CameraPreviewOnSucceeded(videoStreamId)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CameraOperationalStatus.ON, newState.cameraState.operation)
        Assert.assertEquals(null, newState.cameraState.error)
        Assert.assertEquals(videoStreamId, newState.videoStreamID)
    }

    @Test
    fun deviceStateReducer_reduce_when_CameraPreviewOnFailed_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val error =
            CallCompositeError(
                ErrorCode.TURN_CAMERA_ON_FAILED,
                Throwable("CameraOn has failed")
            )
        val action = LocalParticipantAction.CameraPreviewOnFailed(error)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CameraOperationalStatus.OFF, newState.cameraState.operation)
        Assert.assertEquals(error, newState.cameraState.error)
    }

    @Test
    fun deviceStateReducer_reduce_when_CameraPreviewOffTriggered_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PENDING,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = "some video stream id",
            displayName = null,
            localParticipantRole = null
        )
        val action = LocalParticipantAction.CameraPreviewOffTriggered()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CameraOperationalStatus.OFF, newState.cameraState.operation)
        Assert.assertEquals(null, newState.cameraState.error)
        Assert.assertEquals(null, newState.videoStreamID)
    }

    @Test
    fun deviceStateReducer_reduce_when_BluetoothDetected_then_changeState() {
        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PENDING,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = "some video stream id",
            displayName = null,
            localParticipantRole = null
        )

        val action = LocalParticipantAction.AudioDeviceBluetoothSCOAvailable(
            available = true,
            deviceName = "testDevice"
        )

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(true, newState.audioState.bluetoothState.available)
        Assert.assertEquals("testDevice", newState.audioState.bluetoothState.deviceName)
    }

    @Test
    fun deviceStateReducer_reduce_when_HeadsetDetected_then_changeState() {
        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PENDING,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = "some video stream id",
            displayName = null,
            localParticipantRole = null
        )

        val action = LocalParticipantAction.AudioDeviceHeadsetAvailable(
            available = true,
        )

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(true, newState.audioState.isHeadphonePlugged)
    }

    @Test
    fun deviceStateReducer_reduce_when_CameraSwitchTriggered_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = "some video stream id",
            displayName = null,
            localParticipantRole = null
        )
        val action = LocalParticipantAction.CameraSwitchTriggered()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CameraDeviceSelectionStatus.SWITCHING, newState.cameraState.device)
        Assert.assertEquals(null, newState.cameraState.error)
    }

    @Test
    fun deviceStateReducer_reduce_when_CameraSwitchSucceeded_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = "some video stream id",
            displayName = null,
            localParticipantRole = null
        )
        val expectedNewCameraDevice = CameraDeviceSelectionStatus.BACK
        val action = LocalParticipantAction.CameraSwitchSucceeded(expectedNewCameraDevice)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(expectedNewCameraDevice, newState.cameraState.device)
        Assert.assertEquals(null, newState.cameraState.error)
    }

    @Test
    fun deviceStateReducer_reduce_when_CameraSwitchFailed_then_doNotchangeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )

        val error = CallCompositeError(
            ErrorCode.SWITCH_CAMERA_FAILED,
            Throwable("CameraSwitch failed")
        )
        val previousCameraState = CameraDeviceSelectionStatus.FRONT

        val action = LocalParticipantAction.CameraSwitchFailed(previousCameraState, error)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(previousCameraState, newState.cameraState.device)
        Assert.assertEquals(error, newState.cameraState.error)
    }

    @Test
    fun deviceStateReducer_reduce_when_MicOnTriggered_then_changeState_ON() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val action = LocalParticipantAction.MicOnTriggered()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(AudioOperationalStatus.PENDING, newState.audioState.operation)
    }

    @Test
    fun deviceStateReducer_reduce_when_audioStateUpdated_then_reflectNewAudioState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val expectedAudioOperationalStatus = AudioOperationalStatus.ON
        val action =
            LocalParticipantAction.AudioStateOperationUpdated(audioOperationalStatus = expectedAudioOperationalStatus)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(expectedAudioOperationalStatus, newState.audioState.operation)
    }

    @Test
    fun deviceStateReducer_reduce_when_MicOnFailed_then_changeState_ON() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val error =
            CallCompositeError(ErrorCode.TURN_MIC_ON_FAILED, Throwable("MicOn failed"))
        val action = LocalParticipantAction.MicOnFailed(error)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(AudioOperationalStatus.OFF, newState.audioState.operation)
        Assert.assertEquals(error, newState.audioState.error)
    }

    @Test
    fun deviceStateReducer_reduce_when_MicOffTriggered_then_changeState_OFF() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.ON,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val action = LocalParticipantAction.MicOffTriggered()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(AudioOperationalStatus.PENDING, newState.audioState.operation)
    }

    @Test
    fun deviceStateReducer_reduce_when_MicOffFailed_then_changeState_ON() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PENDING,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.ON,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )
        val error =
            CallCompositeError(ErrorCode.TURN_MIC_OFF_FAILED, Throwable("Mic OFF failed"))
        val action = LocalParticipantAction.MicOffFailed(error)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(AudioOperationalStatus.ON, newState.audioState.operation)
    }

    // Helper for Audio Device Selection/Requested tests
    private fun deviceStateReducer_reduce_when_AudioDeviceAction(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PENDING,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.ON,
                audioDeviceSelectionStatus,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = null,
            localParticipantRole = null
        )

        val action =
            LocalParticipantAction.AudioDeviceChangeRequested(audioDeviceSelectionStatus)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(
            audioDeviceSelectionStatus,
            newState.audioState.device
        )
    }

    @Test
    fun localUserState_reduce_when_RoleChanged_then_changeState() {
        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PENDING,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = "some video stream id",
            displayName = null,
            localParticipantRole = null
        )

        val action = LocalParticipantAction.RoleChanged(
            callCompositeParticipantRole = CallCompositeParticipantRole.PRESENTER,
        )

        // assert
        Assert.assertEquals(null, oldState.localParticipantRole)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CallCompositeParticipantRole.PRESENTER, newState.localParticipantRole)
    }

    @Test
    fun deviceStateReducer_reduce_when_AudioDeviceRequested_request_receiver() =
        deviceStateReducer_reduce_when_AudioDeviceAction(AudioDeviceSelectionStatus.RECEIVER_REQUESTED)

    @Test
    fun deviceStateReducer_reduce_when_AudioDeviceRequested_request_bluetooth() =
        deviceStateReducer_reduce_when_AudioDeviceAction(AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED)

    @Test
    fun deviceStateReducer_reduce_when_AudioDeviceRequested_select_receiver() =
        deviceStateReducer_reduce_when_AudioDeviceAction(AudioDeviceSelectionStatus.RECEIVER_SELECTED)

    @Test
    fun deviceStateReducer_reduce_when_AudioDeviceRequested_select_bluetooth() =
        deviceStateReducer_reduce_when_AudioDeviceAction(AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED)
}
