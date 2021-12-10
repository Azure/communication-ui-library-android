// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.reducer

import com.azure.android.communication.ui.configuration.events.CallCompositeErrorCode
import com.azure.android.communication.ui.error.CallCompositeError
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.AudioState
import com.azure.android.communication.ui.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.redux.state.CameraState
import com.azure.android.communication.ui.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.redux.state.LocalUserState
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
        )
        val error =
            CallCompositeError(CallCompositeErrorCode.TURN_CAMERA_ON, Throwable("CameraOn has failed"))
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
        )
        val action = LocalParticipantAction.CameraOffTriggered()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CameraOperationalStatus.PENDING, newState.cameraState.operation)
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = "some video streamId",
            displayName = null
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamId,
            null
        )
        val error = CallCompositeError(CallCompositeErrorCode.TURN_CAMERA_OFF, Throwable("CameraOff failed"))
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
        )
        val error =
            CallCompositeError(CallCompositeErrorCode.TURN_CAMERA_ON, Throwable("CameraOn has failed"))
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = "some video stream id",
            displayName = null
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
    fun deviceStateReducer_reduce_when_CameraSwitchTriggered_then_changeState() {

        // arrange
        val reducer = LocalParticipantStateReducerImpl()
        val oldState = LocalUserState(
            CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = "some video stream id",
            displayName = null
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = "some video stream id",
            displayName = null
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
        )

        val error = CallCompositeError(CallCompositeErrorCode.SWITCH_CAMERA, Throwable("CameraSwitch failed"))
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
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
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
        )
        val error = CallCompositeError(CallCompositeErrorCode.TURN_MIC_ON, Throwable("MicOn failed"))
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
            AudioState(AudioOperationalStatus.ON, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
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
            AudioState(AudioOperationalStatus.ON, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = null
        )
        val error = CallCompositeError(CallCompositeErrorCode.TURN_MIC_OFF, Throwable("Mic OFF failed"))
        val action = LocalParticipantAction.MicOffFailed(error)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(AudioOperationalStatus.ON, newState.audioState.operation)
    }
}
