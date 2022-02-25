// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.middleware

import android.media.AudioManager
import com.azure.android.communication.ui.redux.Dispatch
import com.azure.android.communication.ui.redux.Middleware
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.ReduxState

internal interface AudioMiddleware

// This class is middleware that handles Redux requests for AudioDevices
// You can omit AudioManager in tests and it should behave "as if" it's switching
internal class AudioMiddlewareImpl(
    private val audioManager: AudioManager?
) : Middleware<ReduxState>, AudioMiddleware {

    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->
            when (action) {
                is LocalParticipantAction.AudioDeviceChangeRequested -> {
                    switchAudioDevice(store, action.requestedAudioDevice)
                }

            }
            next(action)

        }
    }

    private fun switchAudioDevice(store: Store<ReduxState>, audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        when (audioDeviceSelectionStatus) {
            AudioDeviceSelectionStatus.SPEAKER_REQUESTED -> {
                audioManager?.apply {
                    stopBluetoothSco()
                    isBluetoothScoOn = false
                    isSpeakerphoneOn = true
                }
                store.dispatch(
                    LocalParticipantAction.AudioDeviceChangeSucceeded(
                        AudioDeviceSelectionStatus.SPEAKER_SELECTED
                    )
                )
            }
            AudioDeviceSelectionStatus.RECEIVER_REQUESTED -> {
                audioManager?.apply {
                    stopBluetoothSco()
                    isBluetoothScoOn = false
                    isSpeakerphoneOn = false
                }
                    store.dispatch(
                        LocalParticipantAction.AudioDeviceChangeSucceeded(
                            AudioDeviceSelectionStatus.RECEIVER_SELECTED
                        )
                    )

            }
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED -> {
                audioManager?.apply {
                    startBluetoothSco()
                    isBluetoothScoOn = true
                    isSpeakerphoneOn = false
                }
                store.dispatch(
                    LocalParticipantAction.AudioDeviceChangeSucceeded(
                        AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED
                    )
                )
            }
            else -> { /* No-op */ }
        }
    }
}
