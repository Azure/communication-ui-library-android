// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.di

import android.content.Context
import com.azure.android.communication.ui.configuration.AppLocalizationProvider
import com.azure.android.communication.ui.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.configuration.LocalizationProvider
import com.azure.android.communication.ui.error.ErrorHandler
import com.azure.android.communication.ui.logger.DefaultLogger
import com.azure.android.communication.ui.presentation.VideoViewManager
import com.azure.android.communication.ui.presentation.fragment.ViewModelFactory
import com.azure.android.communication.ui.presentation.fragment.calling.CallingViewModel
import com.azure.android.communication.ui.presentation.fragment.factories.CallingViewModelFactory
import com.azure.android.communication.ui.presentation.fragment.factories.ParticipantGridCellViewModelFactory
import com.azure.android.communication.ui.presentation.fragment.factories.SetupViewModelFactory
import com.azure.android.communication.ui.presentation.fragment.setup.SetupViewModel
import com.azure.android.communication.ui.presentation.manager.AccessibilityAnnouncementManager
import com.azure.android.communication.ui.presentation.manager.AudioSessionManager
import com.azure.android.communication.ui.presentation.manager.LifecycleManagerImpl
import com.azure.android.communication.ui.presentation.manager.PermissionManager
import com.azure.android.communication.ui.presentation.navigation.NavigationRouterImpl
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.Middleware
import com.azure.android.communication.ui.redux.middleware.CallingMiddlewareImpl
import com.azure.android.communication.ui.redux.middleware.handler.CallingMiddlewareActionHandlerImpl
import com.azure.android.communication.ui.redux.reducer.AppStateReducer
import com.azure.android.communication.ui.redux.reducer.CallStateReducerImpl
import com.azure.android.communication.ui.redux.reducer.ErrorReducerImpl
import com.azure.android.communication.ui.redux.reducer.LifecycleReducerImpl
import com.azure.android.communication.ui.redux.reducer.LocalParticipantStateReducerImpl
import com.azure.android.communication.ui.redux.reducer.NavigationReducerImpl
import com.azure.android.communication.ui.redux.reducer.ParticipantStateReducerImpl
import com.azure.android.communication.ui.redux.reducer.PermissionStateReducerImpl
import com.azure.android.communication.ui.redux.reducer.Reducer
import com.azure.android.communication.ui.redux.state.AppReduxState
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.service.calling.CallingService
import com.azure.android.communication.ui.service.calling.NotificationService
import com.azure.android.communication.ui.service.calling.sdk.CallingSDKEventHandler
import com.azure.android.communication.ui.service.calling.sdk.CallingSDKWrapper
import com.azure.android.communication.ui.utilities.CoroutineContextProvider
import com.azure.android.communication.ui.utilities.StoreHandlerThread

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
        VideoViewManager(callingSDKWrapper, applicationContext)
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

    override val accessibilityManager by lazy {
        AccessibilityAnnouncementManager(
            appStore
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

    override val localizationProvider: LocalizationProvider by lazy {
        AppLocalizationProvider()
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
            navigationReducer
        ) as Reducer<ReduxState>
    }
    //endregion

    //region System
    private val applicationContext get() = parentContext.applicationContext

    private val logger by lazy { DefaultLogger() }
    private val callingSDKWrapper by lazy {
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

    //region Factories
    private val viewModelFactory by lazy {
        ViewModelFactory(
            CallingViewModel(
                appStore,
                callingViewModelFactory
            ),
            SetupViewModel(
                appStore,
                setupViewModelFactory
            )
        )
    }

    private val participantGridCellViewModelFactory by lazy {
        ParticipantGridCellViewModelFactory()
    }

    private val setupViewModelFactory by lazy {
        SetupViewModelFactory(appStore)
    }

    private val callingViewModelFactory by lazy {
        CallingViewModelFactory(
            appStore,
            participantGridCellViewModelFactory
        )
    }
    //endregion

    //region Threading
    private val coroutineContextProvider by lazy { CoroutineContextProvider() }
    private val storeHandlerThread by lazy { StoreHandlerThread() }
    //endregion
}
