// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware.handler

import com.azure.android.communication.ui.calling.error.CallCompositeError
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.service.CallingService

/**
 * This class provides an enhanced implementation of the camera switching functionality
 * that properly handles camera switching after background/foreground transitions.
 */
internal class CameraSwitchingHandler(private val callingService: CallingService) {

    /**
     * Switches the camera with improved handling for background/foreground transitions.
     * This method ensures that camera switching works properly even after the app
     * has gone through a background/foreground cycle.
     *
     * @param store The Redux store
     */
    fun switchCamera(store: Store<ReduxState>) {
        val state = store.getCurrentState()
        val currentCamera = state.localParticipantState.cameraState.device
        
        // Only attempt to switch camera if it's currently ON
        if (state.localParticipantState.cameraState.operation != CameraOperationalStatus.ON) {
            store.dispatch(
                LocalParticipantAction.CameraSwitchFailed(
                    currentCamera,
                    CallCompositeError(
                        ErrorCode.SWITCH_CAMERA_FAILED,
                        Exception("Cannot switch camera when camera is not on")
                    )
                )
            )
            return
        }

        // Call the service to switch camera
        callingService.switchCamera().handle { cameraDevice, error: Throwable? ->
            if (error != null) {
                // If switching fails, try to reinitialize the camera and try again
                if (error.message?.contains("Camera not initialized") == true ||
                    error.message?.contains("Camera error") == true) {
                    
                    // First turn camera off
                    callingService.turnCameraOff().handle { _, offError ->
                        if (offError != null) {
                            // If turning off fails, report the original error
                            store.dispatch(
                                LocalParticipantAction.CameraSwitchFailed(
                                    currentCamera,
                                    CallCompositeError(ErrorCode.SWITCH_CAMERA_FAILED, error)
                                )
                            )
                        } else {
                            // Then turn camera back on
                            callingService.turnCameraOn().handle { _, onError ->
                                if (onError != null) {
                                    // If turning on fails, report the error
                                    store.dispatch(
                                        LocalParticipantAction.CameraSwitchFailed(
                                            currentCamera,
                                            CallCompositeError(ErrorCode.SWITCH_CAMERA_FAILED, onError)
                                        )
                                    )
                                } else {
                                    // Now try switching again
                                    callingService.switchCamera().handle { newCameraDevice, switchError ->
                                        if (switchError != null) {
                                            store.dispatch(
                                                LocalParticipantAction.CameraSwitchFailed(
                                                    currentCamera,
                                                    CallCompositeError(ErrorCode.SWITCH_CAMERA_FAILED, switchError)
                                                )
                                            )
                                        } else {
                                            store.dispatch(
                                                LocalParticipantAction.CameraSwitchSucceeded(newCameraDevice)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // For other errors, just report the failure
                    store.dispatch(
                        LocalParticipantAction.CameraSwitchFailed(
                            currentCamera,
                            CallCompositeError(ErrorCode.SWITCH_CAMERA_FAILED, error)
                        )
                    )
                }
            } else {
                // Success case
                store.dispatch(LocalParticipantAction.CameraSwitchSucceeded(cameraDevice))
            }
        }
    }
}
