// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.middleware

import com.azure.android.communication.ui.logger.Logger
import com.azure.android.communication.ui.redux.Dispatch
import com.azure.android.communication.ui.redux.Middleware
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.CallingAction
import com.azure.android.communication.ui.redux.action.ErrorAction
import com.azure.android.communication.ui.redux.action.LifecycleAction
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.action.PermissionAction
import com.azure.android.communication.ui.redux.middleware.handler.CallingMiddlewareActionHandler
import com.azure.android.communication.ui.redux.state.ReduxState

internal interface CallingMiddleware

internal class CallingMiddlewareImpl(
    private val callingMiddlewareActionHandler: CallingMiddlewareActionHandler,
    private val logger: Logger,
) :
    Middleware<ReduxState>,
    CallingMiddleware {

    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->
            logger.info(action.toString())
            when (action) {
                is LifecycleAction.EnterBackgroundTriggered -> {
                    callingMiddlewareActionHandler.enterBackground(store)
                }
                is LifecycleAction.EnterForegroundTriggered -> {
                    callingMiddlewareActionHandler.enterForeground(store)
                }
                is LocalParticipantAction.CameraPreviewOnRequested -> {
                    callingMiddlewareActionHandler.requestCameraPreviewOn(store)
                }
                is LocalParticipantAction.CameraPreviewOnTriggered -> {
                    callingMiddlewareActionHandler.turnCameraPreviewOn(store)
                }
                is LocalParticipantAction.CameraOffTriggered -> {
                    callingMiddlewareActionHandler.turnCameraOff(store)
                }
                is LocalParticipantAction.CameraOnRequested -> {
                    callingMiddlewareActionHandler.requestCameraOn(store)
                }
                is LocalParticipantAction.CameraOnTriggered -> {
                    callingMiddlewareActionHandler.turnCameraOn(store)
                }
                is LocalParticipantAction.CameraSwitchTriggered -> {
                    callingMiddlewareActionHandler.switchCamera(store)
                }
                is LocalParticipantAction.MicOffTriggered -> {
                    callingMiddlewareActionHandler.turnMicOff(store)
                }
                is LocalParticipantAction.MicOnTriggered -> {
                    callingMiddlewareActionHandler.turnMicOn(store)
                }
                is CallingAction.CallEndRequested -> {
                    callingMiddlewareActionHandler.endCall(store)
                }
                is CallingAction.SetupCall -> {
                    callingMiddlewareActionHandler.setupCall(store)
                }
                is CallingAction.CallStartRequested -> {
                    callingMiddlewareActionHandler.startCall(store)
                }
                is PermissionAction.CameraPermissionIsSet -> {
                    callingMiddlewareActionHandler.onCameraPermissionIsSet(store)
                }
                is ErrorAction.EmergencyExit -> {
                    callingMiddlewareActionHandler.exit(store)
                }
            }
            next(action)
        }
    }
}
