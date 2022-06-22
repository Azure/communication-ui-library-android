// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware.bluetooth
import com.azure.android.communication.ui.calling.redux.Dispatch
import com.azure.android.communication.ui.calling.redux.Middleware
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState

// / This class tracks actions for AudioRequests and
// / enables the appropriate device.
// / It'll also detect New connections and dispatch auto-switch
internal class AudioSwitchingMiddleware(
    private val audioSwitchingAdapter: AudioSwitchingAdapter,
) : Middleware<ReduxState> {


    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->
            when (action) {
                is LocalParticipantAction.AudioDeviceChangeRequested ->
                    if (store.getCurrentState().callState.callingStatus == CallingStatus.CONNECTED) {
                        if (switchToDevice(action.requestedAudioDevice)) {
                            next(action)
                        }
                    } else {
                        next(action)
                    }
                is CallingAction.StateUpdated -> {
                    if (store.getCurrentState().callState.callingStatus != CallingStatus.CONNECTED &&
                        action.callingState == CallingStatus.CONNECTED
                    ) {
                        switchToDevice(store.getCurrentState().localParticipantState.audioState.device)
                    } else if (store.getCurrentState().callState.callingStatus == CallingStatus.CONNECTED &&
                        action.callingState != CallingStatus.CONNECTED
                    ) {
                        audioSwitchingAdapter.disconnectAudio()
                    }
                    next(action)
                }
                is LocalParticipantAction.AudioDeviceBluetoothSCOAvailable -> {
                    // Auto Connect
                    if (action.available && !store.getCurrentState().localParticipantState.audioState.bluetoothState.available) {
                        store
                            .dispatch(
                                LocalParticipantAction
                                    .AudioDeviceChangeRequested(
                                        AudioDeviceSelectionStatus
                                            .BLUETOOTH_SCO_SELECTED
                                    )
                            )
                    } else if (!action.available && store.getCurrentState().localParticipantState.audioState.bluetoothState.available) {
                        store.getCurrentState().localParticipantState.audioState.previousDevice?.apply {
                            store
                                .dispatch(
                                    LocalParticipantAction.AudioDeviceChangeRequested(this)
                                )
                        }
                    }
                    next(action)
                }
                else -> next(action)
            }
        }
    }



    private fun switchToDevice(requestedAudioDevice: AudioDeviceSelectionStatus): Boolean {
        when (requestedAudioDevice) {
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED -> audioSwitchingAdapter.enableBluetooth()
            AudioDeviceSelectionStatus.SPEAKER_SELECTED -> audioSwitchingAdapter.enableSpeakerPhone()
            AudioDeviceSelectionStatus.RECEIVER_SELECTED -> audioSwitchingAdapter.enableEarpiece()
        }
        return true
    }


}
