// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.di

import android.content.Context
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.error.ErrorHandler
import com.azure.android.communication.ui.calling.handlers.RemoteParticipantHandler
import com.azure.android.communication.ui.calling.logger.DefaultLogger
import com.azure.android.communication.ui.calling.presentation.VideoStreamRendererFactory
import com.azure.android.communication.ui.calling.presentation.VideoViewManager
import com.azure.android.communication.ui.calling.presentation.manager.AccessibilityAnnouncementManager
import com.azure.android.communication.ui.calling.presentation.manager.AudioFocusManager
import com.azure.android.communication.ui.calling.presentation.manager.AudioSessionManager
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.calling.presentation.manager.CameraStatusHook
import com.azure.android.communication.ui.calling.presentation.manager.LifecycleManagerImpl
import com.azure.android.communication.ui.calling.presentation.manager.MeetingJoinedHook
import com.azure.android.communication.ui.calling.presentation.manager.MicStatusHook
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
import com.azure.android.communication.ui.calling.service.NotificationService
import com.azure.android.communication.ui.calling.service.sdk.CallingSDK
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKEventHandler
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKWrapper
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider
import com.azure.android.communication.ui.calling.utilities.StoreHandlerThread

internal class DependencyInjectionContainerImpl(
    private val parentContext: Context,
    private val instanceId: Int,
) : DependencyInjectionContainer {

    //region Overrides
    // These getters are required by the interface
    override val configuration get() = CallCompositeConfiguration.getConfig(instanceId)

    override val navigationRouter by lazy {
        NavigationRouterImpl(appStore)
    }

    override val callingMiddlewareActionHandler by lazy {
        CallingMiddlewareActionHandlerImpl(
            callingService,
            coroutineContextProvider
        )
    }

    override val errorHandler by lazy {
        ErrorHandler(configuration, appStore)
    }

    override val videoViewManager by lazy {
        VideoViewManager(callingSDKWrapper, applicationContext, VideoStreamRendererFactory())
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

    override val appStore by lazy {
        AppStore(
            initialState,
            appReduxStateReducer,
            appMiddleware,
            storeHandlerThread
        )
    }

    override val notificationService by lazy {
        NotificationService(parentContext, appStore)
    }

    override val remoteParticipantHandler by lazy {
        RemoteParticipantHandler(configuration, appStore, callingSDKWrapper)
    }

    //region Redux
    // Initial State
    private val initialState by lazy { AppReduxState(configuration.callConfig!!.displayName) }

    // Reducers
    private val callStateReducer get() = CallStateReducerImpl()
    private val participantStateReducer = ParticipantStateReducerImpl()
    private val localParticipantStateReducer get() = LocalParticipantStateReducerImpl()
    private val permissionStateReducer get() = PermissionStateReducerImpl()
    private val lifecycleReducer get() = LifecycleReducerImpl()
    private val errorReducer get() = ErrorReducerImpl()
    private val navigationReducer get() = NavigationReducerImpl()
    private val audioSessionReducer get() = AudioSessionStateReducerImpl()

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
            audioSessionReducer
        ) as Reducer<ReduxState>
    }
    //endregion

    //region System
    private val applicationContext get() = parentContext.applicationContext

    private val logger by lazy { DefaultLogger() }
    private val callingSDKWrapper: CallingSDK by lazy {
        CallingSDKWrapper(
            instanceId,
            applicationContext,
            callingSDKEventHandler,
        )
    }

    private val callingSDKEventHandler by lazy {
        CallingSDKEventHandler(
            coroutineContextProvider
        )
    }

    private val callingService by lazy {
        CallingService(callingSDKWrapper, coroutineContextProvider)
    }
    //endregion

    //region Threading
    private val coroutineContextProvider by lazy { CoroutineContextProvider() }
    private val storeHandlerThread by lazy { StoreHandlerThread() }
    //endregion
}
