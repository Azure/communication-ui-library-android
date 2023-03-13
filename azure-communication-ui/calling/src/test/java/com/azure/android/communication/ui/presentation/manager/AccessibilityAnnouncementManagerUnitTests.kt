// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import android.content.Context
import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.presentation.manager.CameraStatusHook
import com.azure.android.communication.ui.calling.presentation.manager.MeetingJoinedHook
import com.azure.android.communication.ui.calling.presentation.manager.MicStatusHook
import com.azure.android.communication.ui.calling.presentation.manager.ParticipantAddedOrRemovedHook
import com.azure.android.communication.ui.calling.presentation.manager.SwitchCameraStatusHook
import com.azure.android.communication.ui.calling.redux.state.*

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class AccessibilityAnnouncementManagerUnitTests : ACSBaseTestCoroutine() {
    @Test
    fun cameraStatusHook_message_reduxStateOn_then_returnVideoOn() {
        // Arrange
        val reduxState = AppReduxState("")
        val cameraStatusHook = CameraStatusHook()
        val mockContext = mock<Context> {
            on { getString(R.string.azure_communication_ui_calling_setup_view_button_video_on) } doAnswer { "Video on" }
        }
        val mockAudioState = mock<AudioState> {}
        reduxState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.BACK,
                CameraTransmissionStatus.LOCAL
            ),
            mockAudioState, "", ""
        )

        // Act
        val message = cameraStatusHook.message(AppReduxState(""), reduxState, mockContext)

        // Assert
        Assert.assertEquals(message, "Video on")
    }

    @Test
    fun cameraStatusHook_message_reduxStateOff_then_returnVideoOff() {
        // Arrange
        val reduxState = AppReduxState("")
        val cameraStatusHook = CameraStatusHook()
        val mockContext = mock<Context> {
            on { getString(R.string.azure_communication_ui_calling_setup_view_button_video_off) } doAnswer { "Video off" }
        }
        val mockAudioState = mock<AudioState> {}
        reduxState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.BACK,
                CameraTransmissionStatus.LOCAL
            ),
            mockAudioState, "", ""
        )

        // Act
        val message = cameraStatusHook.message(AppReduxState(""), reduxState, mockContext)

        // Assert
        Assert.assertEquals(message, "Video off")
    }

    @Test
    fun cameraStatusHook_message_reduxStateOthers_then_returnEmpty() {
        // Arrange
        val reduxState = AppReduxState("")
        val cameraStatusHook = CameraStatusHook()
        val mockContext = mock<Context> { }
        val mockAudioState = mock<AudioState> {}
        reduxState.localParticipantState =
            LocalUserState(
                CameraState(
                    CameraOperationalStatus.PAUSED,
                    CameraDeviceSelectionStatus.BACK,
                    CameraTransmissionStatus.LOCAL
                ),
                mockAudioState, "", ""
            )

        // Act
        val message = cameraStatusHook.message(AppReduxState(""), reduxState, mockContext)

        // Assert
        Assert.assertEquals(message, "")
    }

    @Test
    fun cameraStatusHook_shouldTrigger_reduxStateDiffer_then_returnTrue() {
        // Arrange
        val reduxState = AppReduxState("")
        val mockAudioState = mock<AudioState> {}
        reduxState.localParticipantState =
            LocalUserState(
                CameraState(
                    CameraOperationalStatus.PAUSED,
                    CameraDeviceSelectionStatus.BACK,
                    CameraTransmissionStatus.LOCAL
                ),
                mockAudioState, "", ""
            )
        val cameraStatusHook = CameraStatusHook()

        // Act
        val result = cameraStatusHook.shouldTrigger(AppReduxState(""), reduxState)

        // Assert
        Assert.assertEquals(result, true)
    }

    @Test
    fun cameraStatusHook_shouldTrigger_reduxStateSame_then_returnFalse() {
        // Arrange
        val reduxState = AppReduxState("")
        val mockAudioState = mock<AudioState> {}
        reduxState.localParticipantState =
            LocalUserState(
                CameraState(
                    CameraOperationalStatus.PAUSED,
                    CameraDeviceSelectionStatus.BACK,
                    CameraTransmissionStatus.LOCAL
                ),
                mockAudioState, "", ""
            )
        val cameraStatusHook = CameraStatusHook()

        // Act
        val result = cameraStatusHook.shouldTrigger(reduxState, reduxState)

        // Assert
        Assert.assertEquals(result, false)
    }

    @Test
    fun micStatusHook_message_reduxStateOn_then_returnMicOn() {
        // Arrange
        val reduxState = AppReduxState("")
        val micStatusHook = MicStatusHook()
        val mockContext = mock<Context> {
            on { getString(R.string.azure_communication_ui_calling_setup_view_button_mic_on) } doAnswer { "Mic on" }
        }
        reduxState.localParticipantState = LocalUserState(
            mock {},
            AudioState(
                AudioOperationalStatus.ON,
                AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED,
                BluetoothState(true, "")
            ),
            "",
            ""
        )

        // Act
        val message = micStatusHook.message(AppReduxState(""), reduxState, mockContext)

        // Assert
        Assert.assertEquals(message, "Mic on")
    }

    @Test
    fun micStatusHook_message_reduxStateOff_then_returnVideoOff() {
        // Arrange
        val reduxState = AppReduxState("")
        val micStatusHook = MicStatusHook()
        val mockContext = mock<Context> {
            on { getString(R.string.azure_communication_ui_calling_setup_view_button_mic_off) } doAnswer { "Mic off" }
        }
        reduxState.localParticipantState = LocalUserState(
            mock {},
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED,
                BluetoothState(true, "")
            ),
            "",
            ""
        )

        // Act
        val message = micStatusHook.message(AppReduxState(""), reduxState, mockContext)

        // Assert
        Assert.assertEquals(message, "Mic off")
    }

    @Test
    fun micStatusHook_message_reduxStateOthers_then_returnEmpty() {
        // Arrange
        val reduxState = AppReduxState("")
        val micStatusHook = MicStatusHook()
        val mockContext = mock<Context> { }
        reduxState.localParticipantState = LocalUserState(
            mock {},
            AudioState(
                AudioOperationalStatus.PENDING,
                AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED,
                BluetoothState(true, "")
            ),
            "",
            ""
        )

        // Act
        val message = micStatusHook.message(AppReduxState(""), reduxState, mockContext)

        // Assert
        Assert.assertEquals(message, "")
    }

    @Test
    fun micStatusHook_shouldTrigger_reduxStateDiffer_then_returnTrue() {
        // Arrange
        val reduxState = AppReduxState("")
        val micStatusHook = MicStatusHook()
        reduxState.localParticipantState = LocalUserState(
            mock {},
            AudioState(
                AudioOperationalStatus.PENDING,
                AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED,
                BluetoothState(true, "")
            ),
            "",
            ""
        )

        // Act
        val result = micStatusHook.shouldTrigger(AppReduxState(""), reduxState)

        // Assert
        Assert.assertEquals(result, true)
    }

    @Test
    fun micStatusHook_shouldTrigger_reduxStateSame_then_returnFalse() {
        // Arrange
        val reduxState = AppReduxState("")
        val micStatusHook = MicStatusHook()
        reduxState.localParticipantState = LocalUserState(
            mock {},
            AudioState(
                AudioOperationalStatus.PENDING,
                AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED,
                BluetoothState(true, "")
            ),
            "",
            ""
        )

        // Act
        val result = micStatusHook.shouldTrigger(reduxState, reduxState)

        // Assert
        Assert.assertEquals(result, false)
    }

    @Test
    fun switchCameraStatusHook_message_reduxStateFront_then_returnFrontCamera() {
        // Arrange
        val reduxState = AppReduxState("")
        val switchCameraStatusHook = SwitchCameraStatusHook()
        val mockContext = mock<Context> {
            on { getString(R.string.azure_communication_ui_calling_switch_camera_button_front) } doAnswer { "Front camera on, switch to back camera" }
        }
        val mockAudioState = mock<AudioState> {}
        reduxState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            mockAudioState, "", ""
        )

        // Act
        val message = switchCameraStatusHook.message(AppReduxState(""), reduxState, mockContext)

        // Assert
        Assert.assertEquals(message, "Front camera on, switch to back camera")
    }

    @Test
    fun switchCameraStatusHook_message_reduxStateBack_then_returnBackCamera() {
        // Arrange
        val reduxState = AppReduxState("")
        val switchCameraStatusHook = SwitchCameraStatusHook()
        val mockContext = mock<Context> {
            on { getString(R.string.azure_communication_ui_calling_switch_camera_button_back) } doAnswer { "Back camera on, switch to front camera" }
        }
        val mockAudioState = mock<AudioState> {}
        reduxState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.BACK,
                CameraTransmissionStatus.LOCAL
            ),
            mockAudioState, "", ""
        )

        // Act
        val message = switchCameraStatusHook.message(AppReduxState(""), reduxState, mockContext)

        // Assert
        Assert.assertEquals(message, "Back camera on, switch to front camera")
    }

    @Test
    fun switchCameraStatusHook_message_reduxStateOthers_then_returnEmpty() {
        // Arrange
        val reduxState = AppReduxState("")
        val switchCameraStatusHook = SwitchCameraStatusHook()
        val mockContext = mock<Context> { }
        val mockAudioState = mock<AudioState> {}
        reduxState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.SWITCHING,
                CameraTransmissionStatus.LOCAL
            ),
            mockAudioState, "", ""
        )

        // Act
        val message = switchCameraStatusHook.message(AppReduxState(""), reduxState, mockContext)

        // Assert
        Assert.assertEquals(message, "")
    }

    @Test
    fun switchCameraStatusHook_shouldTrigger_reduxStateSame_then_returnFalse() {
        // Arrange
        val reduxState = AppReduxState("")
        val switchCameraStatusHook = SwitchCameraStatusHook()
        val mockAudioState = mock<AudioState> {}
        reduxState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.SWITCHING,
                CameraTransmissionStatus.LOCAL
            ),
            mockAudioState, "", ""
        )

        // Act
        val result = switchCameraStatusHook.shouldTrigger(reduxState, reduxState)

        // Assert
        Assert.assertEquals(result, false)
    }

    @Test
    fun switchCameraStatusHook_shouldTrigger_reduxStateDiffer_then_returnTrue() {
        // Arrange
        val reduxState = AppReduxState("")
        val switchCameraStatusHook = SwitchCameraStatusHook()
        val mockAudioState = mock<AudioState> {}
        reduxState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.SWITCHING,
                CameraTransmissionStatus.LOCAL
            ),
            mockAudioState, "", ""
        )

        // Act
        val result = switchCameraStatusHook.shouldTrigger(AppReduxState(""), reduxState)

        // Assert
        Assert.assertEquals(result, true)
    }

    @Test
    fun meetingJoinedHook_shouldTrigger_reduxStateDiffer_then_returnTrue() {
        // Arrange
        val reduxState = AppReduxState("")
        val meetingJoinedHook = MeetingJoinedHook()
        reduxState.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)

        // Act
        val result = meetingJoinedHook.shouldTrigger(AppReduxState(""), reduxState)

        // Assert
        Assert.assertEquals(result, true)
    }

    @Test
    fun meetingJoinedHook_shouldTrigger_reduxStateSame_then_returnFalse() {
        // Arrange
        val reduxState = AppReduxState("")
        val meetingJoinedHook = MeetingJoinedHook()
        reduxState.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)

        // Act
        val result = meetingJoinedHook.shouldTrigger(reduxState, reduxState)

        // Assert
        Assert.assertEquals(result, false)
    }

    @Test
    fun meetingJoinedHook_message_reduxStateAny_then_returnJoined() {
        // Arrange
        val reduxState = AppReduxState("")
        val meetingJoinedHook = MeetingJoinedHook()
        val mockContext = mock<Context> {
            on { getString(R.string.azure_communication_ui_calling_accessibility_meeting_connected) } doAnswer { "You have joined the call" }
        }
        reduxState.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)

        // Act
        val message = meetingJoinedHook.message(reduxState, reduxState, mockContext)

        // Assert
        Assert.assertEquals(message, "You have joined the call")
    }

    @Test
    fun participantAddedOrRemovedHook_shouldTrigger_reduxStateDiffer_then_returnTrue() {
        // Arrange
        val reduxState = AppReduxState("")
        val meetingJoinedHook = MeetingJoinedHook()
        reduxState.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)
        reduxState.remoteParticipantState =
            RemoteParticipantsState(mapOf(Pair("a", mock { })), 5000)
        // Act
        val result = meetingJoinedHook.shouldTrigger(AppReduxState(""), reduxState)

        // Assert
        Assert.assertEquals(result, true)
    }

    @Test
    fun participantAddedOrRemovedHook_shouldTrigger_reduxStateSame_then_returnFalse() {
        // Arrange
        val reduxState = AppReduxState("")
        val meetingJoinedHook = MeetingJoinedHook()
        reduxState.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)
        reduxState.remoteParticipantState =
            RemoteParticipantsState(mapOf(Pair("a", mock { })), 5000)
        // Act
        val result = meetingJoinedHook.shouldTrigger(reduxState, reduxState)

        // Assert
        Assert.assertEquals(result, false)
    }

    @Test
    fun participantAddedOrRemovedHook_message_reduxStateHasParticipants_then_returnJoinedMessage() {
        // Arrange
        val reduxState = AppReduxState("")
        val participantAddedOrRemovedHook = ParticipantAddedOrRemovedHook()
        val mockContext = mock<Context> {
            on {
                getString(
                    R.string.azure_communication_ui_calling_accessibility_user_added,
                    "user"
                )
            } doAnswer { "user has joined the meeting" }
        }
        reduxState.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)
        reduxState.remoteParticipantState = RemoteParticipantsState(
            mapOf(
                Pair(
                    "a",
                    mock {
                        on { displayName } doAnswer { "user" }
                    }
                )
            ),
            5000
        )

        // Act
        val message =
            participantAddedOrRemovedHook.message(AppReduxState(""), reduxState, mockContext)

        // Assert
        Assert.assertEquals(message, "user has joined the meeting")
    }

    @Test
    fun participantAddedOrRemovedHook_message_reduxStateHasRemovedParticipants_then_returnLeftMessage() {
        // Arrange
        val reduxState = AppReduxState("")
        val participantAddedOrRemovedHook = ParticipantAddedOrRemovedHook()
        val mockContext = mock<Context> {
            on {
                getString(
                    R.string.azure_communication_ui_calling_accessibility_user_left,
                    "user"
                )
            } doAnswer { "user has left the meeting" }
        }
        reduxState.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)
        reduxState.remoteParticipantState = RemoteParticipantsState(
            mapOf(
                Pair(
                    "a",
                    mock {
                        on { displayName } doAnswer { "user" }
                    }
                )
            ),
            5000
        )

        // Act
        val message =
            participantAddedOrRemovedHook.message(reduxState, AppReduxState(""), mockContext)

        // Assert
        Assert.assertEquals(message, "user has left the meeting")
    }
}
