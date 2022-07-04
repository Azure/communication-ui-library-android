// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware.audio
import com.azure.android.communication.ui.calling.redux.Dispatch
import com.azure.android.communication.ui.calling.redux.Middleware
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LifecycleAction
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.utilities.audio.AudioSwitchingAdapter

// This class tracks actions for AudioRequests and
// enables the appropriate device.
//
// on AudioDeviceChangeRequested    --  Switches to requested device
// on EnterForegroundSucceeded      --  Enable Audio for Setup Screen
// on EnterBackgroundSucceeded      --  Disable Audio on Set Page
//
internal class AudioSwitchingMiddleware(
    private val audioSwitchingAdapter: AudioSwitchingAdapter,
) : Middleware<ReduxState> {
    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->
            when (action) {
                is LocalParticipantAction.AudioDeviceChangeRequested ->
                    handleAudioDeviceChangeRequested(action, next)

                is LocalParticipantAction.AudioDeviceBluetoothSCOAvailable -> onBluetoothDetectionChange(
                    action,
                    store,
                    next)

                is LifecycleAction.EnterForegroundSucceeded -> onForegroundTransition(
                    action,
                    store,
                    next)

                is LifecycleAction.EnterBackgroundSucceeded -> onBackgroundTransition(
                    action,
                    store,
                    next)

                else -> next(action)
            }
        }
    }

    // Call when app is done
    // It'll disconnect the audio
    fun dispose() {
        audioSwitchingAdapter.disconnectAudio()
    }


    // Called when the App enters from foreground to background
    // Generally this means activating the currently selected device
    private fun onForegroundTransition(
        action: LifecycleAction.EnterForegroundSucceeded,
        store: Store<ReduxState>,
        next: Dispatch
    ) {
        // If we are connected, we hold onto the audio
        // So we can just return early and pass the action through
        if (store.getCurrentState().callState.callingStatus == CallingStatus.CONNECTED) {
            next(action)

        } else {
            // Otherwise, we are coming back to the setup screen (and not currently connected)
            // So we need to switch to the correct device
            when (store.getCurrentState().localParticipantState.audioState.device) {
                AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED -> audioSwitchingAdapter.enableBluetooth()
                AudioDeviceSelectionStatus.SPEAKER_SELECTED -> audioSwitchingAdapter.enableSpeakerPhone()
                AudioDeviceSelectionStatus.RECEIVER_SELECTED -> audioSwitchingAdapter.enableEarpiece()
            }
        }
        next(action)
    }

    // We are going into the background.
    // If a cell is connected
    //  Hold Audio
    // Otherwise
    //  Release audio
    private fun onBackgroundTransition(
        action: LifecycleAction.EnterBackgroundSucceeded,
        store: Store<ReduxState>,
        next: Dispatch
    ) {
        val state = store.getCurrentState()
        val callingStatus = state.callState.callingStatus

        if (callingStatus != CallingStatus.CONNECTED) {
            audioSwitchingAdapter.disconnectAudio()
        }
        next(action)
    }


    // Handle Bluetooth Detection
    // Switches to and away  bluetooth when connection changes
    private fun onBluetoothDetectionChange(
        action: LocalParticipantAction.AudioDeviceBluetoothSCOAvailable,
        store: Store<ReduxState>,
        next: Dispatch
    ) {
        // Auto Connect
        val audioState = store.getCurrentState().localParticipantState.audioState
        val btState = audioState.bluetoothState

        if (action.available
            &&
            !btState.available) {
            // If this action is saying it's available, but it isn't available yet in the store
            // It is time to switch to bluetooth.
            // We will dispatch a request for bluetooth

            next(action)
            store
                .dispatch(
                    LocalParticipantAction
                        .AudioDeviceChangeRequested(
                            AudioDeviceSelectionStatus
                                .BLUETOOTH_SCO_SELECTED
                        )
                )
        } else if (!action.available && btState.available) {
            // Since BT is available in store still, but this action is disabling
            // We need to disconnect
            //
            // Revert to the previous selected device
            next(action)
            audioState.previousDevice?.apply {
                store
                    .dispatch(
                        LocalParticipantAction.AudioDeviceChangeRequested(this)
                    )
            }
        }

    }



    // Switch to the Device requested by the Action
    private fun handleAudioDeviceChangeRequested(
        action: LocalParticipantAction.AudioDeviceChangeRequested,
        next: Dispatch
    ) {
        when (action.requestedAudioDevice) {
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED -> audioSwitchingAdapter.enableBluetooth()
            AudioDeviceSelectionStatus.SPEAKER_SELECTED -> audioSwitchingAdapter.enableSpeakerPhone()
            AudioDeviceSelectionStatus.RECEIVER_SELECTED -> audioSwitchingAdapter.enableEarpiece()
        }
        next(action)
    }

}
