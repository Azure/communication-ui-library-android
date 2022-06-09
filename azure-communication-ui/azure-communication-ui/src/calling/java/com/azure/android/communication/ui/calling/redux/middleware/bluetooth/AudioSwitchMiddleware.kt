// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware.bluetooth



import android.content.Context
import android.media.AudioManager
import com.azure.android.communication.ui.calling.redux.Dispatch
import com.azure.android.communication.ui.calling.redux.Middleware
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState

/// This class tracks actions for AudioRequests and
/// enables the appropriate device.
/// It'll also detect New connections and dispatch auto-switch
internal class AudioSwitchingMiddleware(
    context: Context,
) :
    Middleware<ReduxState> {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->
            when (action) {
                is LocalParticipantAction.AudioDeviceChangeRequested ->
                    if (switchToDevice(action)) {
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

    private fun switchToDevice(action: LocalParticipantAction.AudioDeviceChangeRequested): Boolean {
        when (action.requestedAudioDevice) {
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED -> enableBluetooth()
            AudioDeviceSelectionStatus.SPEAKER_SELECTED -> enableSpeakerPhone()
            AudioDeviceSelectionStatus.RECEIVER_SELECTED -> enableEarpiece()
        }
        return true
    }

    private fun enableSpeakerPhone() : Boolean{
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        audioManager.isSpeakerphoneOn = true
        return true
    }

    private fun enableEarpiece() : Boolean {
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        audioManager.isSpeakerphoneOn = false
        return true
    }

    private fun enableBluetooth() : Boolean {

        if (!audioManager.isBluetoothScoOn) {
            audioManager.startBluetoothSco()
            audioManager.isBluetoothScoOn = true
            audioManager.isSpeakerphoneOn = false
          return true
        }
        return false
    }
}
