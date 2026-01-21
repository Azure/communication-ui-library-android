// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware

import android.os.Handler
import android.os.Looper
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.redux.Dispatch
import com.azure.android.communication.ui.calling.redux.Middleware
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.AudioDeviceAction
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.AudioDeviceService

internal interface AudioDeviceMiddleware

internal class AudioDeviceMiddlewareImpl(
    private val audioDeviceService: AudioDeviceService,
    private val logger: Logger,
) : Middleware<ReduxState>,
    AudioDeviceMiddleware {

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->
            logger.info(action.toString())
            when (action) {
                is AudioDeviceAction.SelectDeviceRequested -> {
                    handleDeviceSelection(action, store, next)
                }
                else -> next(action)
            }
        }
    }

    private fun handleDeviceSelection(
        action: AudioDeviceAction.SelectDeviceRequested,
        store: Store<ReduxState>,
        next: Dispatch
    ) {
        val targetDevice = action.device
        val state = store.getCurrentState()
        
        // Verify device is available
        if (!state.audioState.availableDevices.contains(targetDevice)) {
            next(AudioDeviceAction.DeviceSwitchFailed(
                targetDevice,
                state.audioState.currentDevice,
                "Selected device is not available"
            ))
            return
        }

        // Don't switch if already switching
        if (state.audioState.switchingState == com.azure.android.communication.ui.calling.redux.state.AudioDeviceSwitchingState.SWITCHING) {
            return
        }

        // Don't switch if already using this device
        if (state.audioState.currentDevice == targetDevice) {
            return
        }

        // Start switching process
        next(AudioDeviceAction.DeviceSwitchStarted(
            targetDevice,
            state.audioState.currentDevice
        ))

        // Attempt to configure the device asynchronously
        configureDeviceAsync(targetDevice, state.audioState.currentDevice, next)
    }

    private fun configureDeviceAsync(
        targetDevice: com.azure.android.communication.ui.calling.redux.state.AudioDevice,
        previousDevice: com.azure.android.communication.ui.calling.redux.state.AudioDevice,
        next: Dispatch
    ) {
        mainHandler.post {
            try {
                val success = audioDeviceService.configureAudioRouting(targetDevice)
                
                if (success) {
                    next(AudioDeviceAction.DeviceSwitchCompleted(targetDevice))
                } else {
                    next(AudioDeviceAction.DeviceSwitchFailed(
                        targetDevice,
                        previousDevice,
                        "Failed to configure audio routing"
                    ))
                    // Attempt to restore previous device
                    audioDeviceService.configureAudioRouting(previousDevice)
                }
            } catch (e: Exception) {
                next(AudioDeviceAction.DeviceSwitchFailed(
                    targetDevice,
                    previousDevice,
                    "Error configuring audio routing: ${e.message}"
                ))
                // Attempt to restore previous device
                audioDeviceService.configureAudioRouting(previousDevice)
            }
        }
    }
}
