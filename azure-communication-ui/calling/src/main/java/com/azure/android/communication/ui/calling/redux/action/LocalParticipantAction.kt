// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.error.CallCompositeError
import com.azure.android.communication.ui.calling.models.ParticipantRole
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus

internal sealed class LocalParticipantAction : Action {
    class DeviceManagerFetchFailed(val error: CallCompositeError) : LocalParticipantAction()
    class CameraPreviewOnRequested : LocalParticipantAction()
    class CameraPreviewOnTriggered : LocalParticipantAction()
    class CameraPreviewOnSucceeded(var videoStreamID: String) : LocalParticipantAction()
    class CameraPreviewOnFailed(val error: CallCompositeError) : LocalParticipantAction()

    class CameraPreviewOffTriggered : LocalParticipantAction()

    class CameraOnRequested : LocalParticipantAction()
    class CameraOnTriggered : LocalParticipantAction()
    class CameraOnSucceeded(var videoStreamID: String) : LocalParticipantAction()
    class CameraOnFailed(val error: CallCompositeError) : LocalParticipantAction()

    class CameraOffTriggered : LocalParticipantAction()
    class CameraOffSucceeded : LocalParticipantAction()
    class CameraOffFailed(val error: CallCompositeError) : LocalParticipantAction()

    class CameraSwitchTriggered : LocalParticipantAction()
    class CameraSwitchSucceeded(val cameraDeviceSelectionStatus: CameraDeviceSelectionStatus) :
        LocalParticipantAction()

    class CameraSwitchFailed(
        val previousDevice: CameraDeviceSelectionStatus,
        val error: CallCompositeError,
    ) :
        LocalParticipantAction()

    class CameraPauseSucceeded : LocalParticipantAction()
    class CameraPauseFailed(val error: CallCompositeError) : LocalParticipantAction()

    class CamerasCountUpdated(val count: Int) : LocalParticipantAction()

    class MicPreviewOnTriggered : LocalParticipantAction()
    class MicPreviewOffTriggered : LocalParticipantAction()

    class MicOnTriggered : LocalParticipantAction()
    class MicOnFailed(val error: CallCompositeError) : LocalParticipantAction()

    class MicOffTriggered : LocalParticipantAction()
    class MicOffFailed(val error: CallCompositeError) : LocalParticipantAction()

    class AudioStateOperationUpdated(val audioOperationalStatus: AudioOperationalStatus) :
        LocalParticipantAction()

    class AudioDeviceChangeRequested(val requestedAudioDevice: AudioDeviceSelectionStatus) :
        LocalParticipantAction()

    class AudioDeviceChangeSucceeded(val selectedAudioDevice: AudioDeviceSelectionStatus) :
        LocalParticipantAction()

    class AudioDeviceHeadsetAvailable(val available: Boolean) : LocalParticipantAction()
    class AudioDeviceBluetoothSCOAvailable(val available: Boolean, val deviceName: String) : LocalParticipantAction()

    class AudioDeviceChangeFailed(
        val previousDevice: AudioDeviceSelectionStatus,
        val error: CallCompositeError,
    ) :
        LocalParticipantAction()

    class DisplayNameIsSet(val displayName: String) : LocalParticipantAction()

    class RoleChanged(val participantRole: ParticipantRole?) : LocalParticipantAction()

    class SetCapabilities(val capabilities: List<ParticipantCapabilityType>) : LocalParticipantAction()
}
