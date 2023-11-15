// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.di

import android.content.Context
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.data.CallHistoryRepositoryImpl
import com.azure.android.communication.ui.calling.error.ErrorHandler
import com.azure.android.communication.ui.calling.getConfig
import com.azure.android.communication.ui.calling.handlers.CallStateHandler
import com.azure.android.communication.ui.calling.handlers.RemoteParticipantHandler
import com.azure.android.communication.ui.calling.logger.DefaultLogger
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.presentation.VideoStreamRendererFactory
import com.azure.android.communication.ui.calling.presentation.VideoStreamRendererFactoryImpl
import com.azure.android.communication.ui.calling.presentation.VideoViewManager
import com.azure.android.communication.ui.calling.presentation.manager.AccessibilityAnnouncementManager
import com.azure.android.communication.ui.calling.presentation.manager.AudioFocusManager
import com.azure.android.communication.ui.calling.presentation.manager.AudioModeManager
import com.azure.android.communication.ui.calling.presentation.manager.AudioSessionManager
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.calling.presentation.manager.CompositeExitManager
import com.azure.android.communication.ui.calling.presentation.manager.CameraStatusHook
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManagerImpl
import com.azure.android.communication.ui.calling.presentation.manager.LifecycleManagerImpl
import com.azure.android.communication.ui.calling.presentation.manager.MeetingJoinedHook
import com.azure.android.communication.ui.calling.presentation.manager.MicStatusHook
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.presentation.manager.ParticipantAddedOrRemovedHook
import com.azure.android.communication.ui.calling.presentation.manager.PermissionManager
import com.azure.android.communication.ui.calling.presentation.manager.SwitchCameraStatusHook

import com.azure.android.communication.ui.calling.presentation.navigation.NavigationRouterImpl
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.Middleware
import com.azure.android.communication.ui.calling.redux.middleware.CallingMiddlewareImpl
import com.azure.android.communication.ui.calling.redux.middleware.handler.CallingMiddlewareActionHandlerImpl
import com.azure.android.communication.ui.calling.redux.reducer.AppStateReducer
import com.azure.android.communication.ui.calling.redux.reducer.AudioSessionStateReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.CallDiagnosticsReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.CallStateReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.ErrorReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.LifecycleReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.LocalParticipantStateReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.NavigationReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.ParticipantStateReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.PermissionStateReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.Reducer
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.CallingService
import com.azure.android.communication.ui.calling.presentation.manager.MultitaskingManager
import com.azure.android.communication.ui.calling.redux.reducer.PipReducerImpl
import com.azure.android.communication.ui.calling.service.CallHistoryService
import com.azure.android.communication.ui.calling.service.CallHistoryServiceImpl
import com.azure.android.communication.ui.calling.service.NotificationService
import com.azure.android.communication.ui.calling.service.sdk.CallingSDK
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKEventHandler
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKInstanceManager
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKWrapper
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider

internal class DependencyInjectionContainerImpl(
    private val instanceId: Int,
    private val parentContext: Context,
    override val callComposite: CallComposite,
    private val customCallingSDK: CallingSDK?,
    private val customVideoStreamRendererFactory: VideoStreamRendererFactory?,
    private val customCoroutineContextProvider: CoroutineContextProvider?,
) : DependencyInjectionContainer {

    override val configuration by lazy {
        callComposite.getConfig()
    }

    override val navigationRouter by lazy {
        NavigationRouterImpl(appStore)
    }

    override val callingMiddlewareActionHandler by lazy {
        CallingMiddlewareActionHandlerImpl(
            callingService,
            coroutineContextProvider,
        )
    }

    override val callStateHandler by lazy {
        CallStateHandler(configuration, appStore)
    }

    override val errorHandler by lazy {
        ErrorHandler(configuration, appStore)
    }

    override val videoViewManager by lazy {
        VideoViewManager(
            callingSDKWrapper,
            applicationContext,
            customVideoStreamRendererFactory ?: VideoStreamRendererFactoryImpl()
        )
    }

    override val compositeExitManager by lazy {
        CompositeExitManager(appStore, configuration)
    }

    override val permissionManager by lazy {
        PermissionManager(appStore)
    }

    override val audioSessionManager by lazy {
        AudioSessionManager(
            appStore,
            applicationContext,
        )
    }

    override val audioFocusManager by lazy {
        AudioFocusManager(
            appStore,
            applicationContext,
            configuration,
        )
    }

    override val audioModeManager by lazy {
        AudioModeManager(
            appStore,
            applicationContext,
        )
    }

    override val networkManager by lazy {
        NetworkManager(
            applicationContext,
        )
    }

    override val debugInfoManager: DebugInfoManager by lazy {
        DebugInfoManagerImpl(
            callHistoryRepository,
        )
    }

    override val callHistoryService: CallHistoryService by lazy {
        CallHistoryServiceImpl(
            appStore,
            callHistoryRepository
        )
    }

    override val avatarViewManager by lazy {
        AvatarViewManager(
            coroutineContextProvider,
            appStore,
            configuration.callCompositeLocalOptions,
            configuration.remoteParticipantsConfiguration
        )
    }

    override val accessibilityManager by lazy {
        AccessibilityAnnouncementManager(
            appStore,
            listOf(
                MeetingJoinedHook(),
                CameraStatusHook(),
                ParticipantAddedOrRemovedHook(),
                MicStatusHook(),
                SwitchCameraStatusHook(),
            )
        )
    }

    override val lifecycleManager by lazy {
        LifecycleManagerImpl(appStore)
    }

    override val multitaskingManager by lazy {
        MultitaskingManager(appStore, configuration)
    }

    override val appStore by lazy {
        AppStore(
            initialState,
            appReduxStateReducer,
            appMiddleware,
            storeDispatcher
        )
    }

    override val notificationService by lazy {
        NotificationService(parentContext, appStore, configuration, instanceId)
    }

    override val remoteParticipantHandler by lazy {
        RemoteParticipantHandler(configuration, appStore, callingSDKWrapper)
    }

    override val callHistoryRepository by lazy {
        CallHistoryRepositoryImpl(applicationContext, logger)
    }

    private val localOptions by lazy {
        configuration.callCompositeLocalOptions
    }

    //region Redux
    // Initial State
    private val initialState by lazy {
        AppReduxState(
            configuration.callConfig?.displayName,
            localOptions?.isCameraOn == true,
            localOptions?.isMicrophoneOn == true
        )
    }

    // Reducers
    private val callStateReducer get() = CallStateReducerImpl()
    private val participantStateReducer = ParticipantStateReducerImpl()
    private val localParticipantStateReducer get() = LocalParticipantStateReducerImpl()
    private val permissionStateReducer get() = PermissionStateReducerImpl()
    private val lifecycleReducer get() = LifecycleReducerImpl()
    private val errorReducer get() = ErrorReducerImpl()
    private val navigationReducer get() = NavigationReducerImpl()
    private val audioSessionReducer get() = AudioSessionStateReducerImpl()
    private val pipReducer get() = PipReducerImpl()
    private val callDiagnosticsReducer get() = CallDiagnosticsReducerImpl()

    // Middleware
    private val appMiddleware get() = mutableListOf(callingMiddleware)

    private val callingMiddleware: Middleware<ReduxState> by lazy {
        CallingMiddlewareImpl(
            callingMiddlewareActionHandler,
            logger
        )
    }

    private val appReduxStateReducer: Reducer<ReduxState> by lazy {
        AppStateReducer(
            callStateReducer,
            participantStateReducer,
            localParticipantStateReducer,
            permissionStateReducer,
            lifecycleReducer,
            errorReducer,
            navigationReducer,
            audioSessionReducer,
            pipReducer,
            callDiagnosticsReducer
        ) as Reducer<ReduxState>
    }
    //endregion

    //region System
    private val applicationContext get() = parentContext.applicationContext

    override val logger: Logger by lazy { DefaultLogger() }

    override val callingSDKWrapper: CallingSDK by lazy {
        customCallingSDK
            ?: CallingSDKWrapper(
                applicationContext,
                callingSDKEventHandler,
                configuration.callConfig,
                CallingSDKInstanceManager.callingSDKCallAgentWrapper!!,
            )
    }

    private val callingSDKEventHandler by lazy {
        CallingSDKEventHandler(
            coroutineContextProvider
        )
    }

    override val callingService by lazy {
        CallingService(callingSDKWrapper, coroutineContextProvider)
    }
    //endregion

    //region Threading
    private val coroutineContextProvider by lazy {
        customCoroutineContextProvider ?: CoroutineContextProvider()
    }
    private val storeDispatcher by lazy {
        customCoroutineContextProvider?.SingleThreaded ?: coroutineContextProvider.SingleThreaded
    }
    //endregion
}
