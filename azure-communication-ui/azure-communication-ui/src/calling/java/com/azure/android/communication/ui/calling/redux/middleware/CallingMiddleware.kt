// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware

import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.redux.middleware.handler.CallingMiddlewareActionHandler
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import org.reduxkotlin.Dispatcher
import org.reduxkotlin.Middleware

internal interface CallingMiddleware

internal class CallingMiddlewareImpl(
    private val callingMiddlewareActionHandler: CallingMiddlewareActionHandler,
    private val logger: Logger,
) :
    Middleware<ReduxState>,
    CallingMiddleware {

//    fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
//        { action: Action ->
//            logger.info(action.toString())
//            when (action) {
//                is LifecycleAction.EnterBackgroundTriggered -> {
//                    callingMiddlewareActionHandler.enterBackground(store)
//                }
//                is LifecycleAction.EnterForegroundTriggered -> {
//                    callingMiddlewareActionHandler.enterForeground(store)
//                }
//                is LocalParticipantAction.CameraPreviewOnRequested -> {
//                    callingMiddlewareActionHandler.requestCameraPreviewOn(store)
//                }
//                is LocalParticipantAction.CameraPreviewOnTriggered -> {
//                    callingMiddlewareActionHandler.turnCameraPreviewOn(store)
//                }
//                is LocalParticipantAction.CameraOffTriggered -> {
//                    callingMiddlewareActionHandler.turnCameraOff(store)
//                }
//                is LocalParticipantAction.CameraOnRequested -> {
//                    callingMiddlewareActionHandler.requestCameraOn(store)
//                }
//                is LocalParticipantAction.CameraOnTriggered -> {
//                    callingMiddlewareActionHandler.turnCameraOn(store)
//                }
//                is LocalParticipantAction.CameraSwitchTriggered -> {
//                    callingMiddlewareActionHandler.switchCamera(store)
//                }
//                is LocalParticipantAction.MicOffTriggered -> {
//                    callingMiddlewareActionHandler.turnMicOff(store)
//                }
//                is AudioSessionAction.AudioFocusApproved -> {
//                    store.dispatch(CallingAction.ResumeRequested())
//                }
//                is AudioSessionAction.AudioFocusInterrupted -> {
//                    store.dispatch(CallingAction.HoldRequested())
//                }
//                is LocalParticipantAction.MicOnTriggered -> {
//                    callingMiddlewareActionHandler.turnMicOn(store)
//                }
//                is CallingAction.HoldRequested -> {
//                    callingMiddlewareActionHandler.hold(store)
//                }
//                is CallingAction.ResumeRequested -> {
//                    callingMiddlewareActionHandler.resume(store)
//                }
//                is CallingAction.CallEndRequested -> {
//                    callingMiddlewareActionHandler.endCall(store)
//                }
//                is CallingAction.SetupCall -> {
//                    callingMiddlewareActionHandler.setupCall(store)
//                }
//                is CallingAction.CallStartRequested -> {
//                    callingMiddlewareActionHandler.startCall(store)
//                }
//                is PermissionAction.CameraPermissionIsSet -> {
//                    callingMiddlewareActionHandler.onCameraPermissionIsSet(store)
//                }
//                is ErrorAction.EmergencyExit -> {
//                    callingMiddlewareActionHandler.exit(store)
//                }
//            }
//            next(action)
//        }
//    }

    override fun invoke(store: org.reduxkotlin.Store<ReduxState>): (next: Dispatcher) -> (action: Any) -> Any {
        TODO("Not yet implemented")
    }
}
