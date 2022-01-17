// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.di

import android.content.Context
import com.azure.android.communication.ui.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.error.ErrorHandler
import com.azure.android.communication.ui.logger.DefaultLogger
import com.azure.android.communication.ui.logger.Logger
import com.azure.android.communication.ui.presentation.UIManager
import com.azure.android.communication.ui.presentation.VideoViewManager
import com.azure.android.communication.ui.presentation.fragment.CallingCompositeFragmentFactory
import com.azure.android.communication.ui.presentation.fragment.ViewModelFactory
import com.azure.android.communication.ui.presentation.fragment.calling.CallingViewModel
import com.azure.android.communication.ui.presentation.fragment.factories.CallingViewModelFactory
import com.azure.android.communication.ui.presentation.fragment.factories.ParticipantGridCellViewModelFactory
import com.azure.android.communication.ui.presentation.fragment.factories.SetupViewModelFactory
import com.azure.android.communication.ui.presentation.fragment.setup.SetupViewModel
import com.azure.android.communication.ui.presentation.manager.AudioSessionManager
import com.azure.android.communication.ui.presentation.manager.LifecycleManagerImpl
import com.azure.android.communication.ui.presentation.manager.PermissionManager
import com.azure.android.communication.ui.presentation.navigation.NavigationRouterImpl
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.Middleware
import com.azure.android.communication.ui.redux.middleware.CallingMiddleware
import com.azure.android.communication.ui.redux.middleware.CallingMiddlewareImpl
import com.azure.android.communication.ui.redux.middleware.handler.CallingMiddlewareActionHandlerImpl
import com.azure.android.communication.ui.redux.reducer.AppStateReducer
import com.azure.android.communication.ui.redux.reducer.CallStateReducer
import com.azure.android.communication.ui.redux.reducer.CallStateReducerImpl
import com.azure.android.communication.ui.redux.reducer.ErrorReducer
import com.azure.android.communication.ui.redux.reducer.ErrorReducerImpl
import com.azure.android.communication.ui.redux.reducer.LifecycleReducer
import com.azure.android.communication.ui.redux.reducer.LifecycleReducerImpl
import com.azure.android.communication.ui.redux.reducer.LocalParticipantStateReducer
import com.azure.android.communication.ui.redux.reducer.LocalParticipantStateReducerImpl
import com.azure.android.communication.ui.redux.reducer.NavigationReducer
import com.azure.android.communication.ui.redux.reducer.NavigationReducerImpl
import com.azure.android.communication.ui.redux.reducer.ParticipantStateReducer
import com.azure.android.communication.ui.redux.reducer.ParticipantStateReducerImpl
import com.azure.android.communication.ui.redux.reducer.PermissionStateReducer
import com.azure.android.communication.ui.redux.reducer.PermissionStateReducerImpl
import com.azure.android.communication.ui.redux.reducer.Reducer
import com.azure.android.communication.ui.redux.state.AppReduxState
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.service.calling.CallingService
import com.azure.android.communication.ui.service.calling.sdk.CallingSDKEventHandler
import com.azure.android.communication.ui.service.calling.sdk.CallingSDKWrapper
import com.azure.android.communication.ui.utilities.CoroutineContextProvider
import com.azure.android.communication.ui.utilities.StoreHandlerThread

internal class DependencyInjectionContainerImpl(
    private val callCompositeConfiguration: CallCompositeConfiguration,
    private val parentContext: Context,
) : DependencyInjectionContainer {

    override val navigationRouter by lazy {
        NavigationRouterImpl(appStore)
    }

    override val fragmentFactory by lazy {
        CallingCompositeFragmentFactory(viewModelFactory, videoViewManager)
    }

    override val configuration = callCompositeConfiguration

    private val participantGridCellViewModelFactory by lazy {
        ParticipantGridCellViewModelFactory()
    }


    private val setupViewModelFactory by lazy {
        SetupViewModelFactory(appStore)
    }

    private val callingViewModelFactory by lazy {
        CallingViewModelFactory(appStore, participantGridCellViewModelFactory)
    }


    //region View Model Factory
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

    override val videoViewManager by lazy {
        VideoViewManager(provideCallingSDKWrapper(), provideApplicationContext())
    }

    override val permissionManager by lazy {
        PermissionManager(appStore)
    }

    override val audioSessionManager by lazy {
        AudioSessionManager(appStore)
    }

    override val lifecycleManager by lazy {
        LifecycleManagerImpl(appStore)
    }

    override val appStore by lazy {
        AppStore(
            provideState(),
            appReduxStateReducer,
            provideAppMiddleware(),
            storeHandlerThread
        )
    }

    private val storeHandlerThread by lazy { StoreHandlerThread() }

    private val appReduxStateReducer : Reducer<ReduxState> by lazy {
        AppStateReducer(
            provideCallStateReducer(),
            provideParticipantStateReducer(),
            provideLocalParticipantStateReducer(),
            providePermissionStateReducer(),
            provideLifecycleReducer(),
            provideErrorReducer(),
            provideNavigationReducer()
        ) as Reducer<ReduxState>
    }

    private fun provideNavigationReducer(): NavigationReducer {
        return NavigationReducerImpl()
    }

    private fun provideCallStateReducer(): CallStateReducer {
        return CallStateReducerImpl()
    }

    private fun provideParticipantStateReducer(): ParticipantStateReducer {
        return ParticipantStateReducerImpl()
    }

    private fun provideLocalParticipantStateReducer(): LocalParticipantStateReducer {
        return LocalParticipantStateReducerImpl()
    }

    private fun providePermissionStateReducer(): PermissionStateReducer {
        return PermissionStateReducerImpl()
    }

    private fun provideLifecycleReducer(): LifecycleReducer {
        return LifecycleReducerImpl()
    }

    private fun provideErrorReducer(): ErrorReducer {
        return ErrorReducerImpl()
    }

    //endregion

    //region state
    private fun provideState(): ReduxState {
        return AppReduxState(configuration.callConfig!!.displayName)
    }

    //endregion

    //region Middleware

    private fun provideCallingMiddleware(): CallingMiddleware {
        return CallingMiddlewareImpl(
            callingMiddlewareActionHandler,
            provideLogger()
        )
    }

    private fun provideAppMiddleware(): MutableList<Middleware<ReduxState>> =
        mutableListOf(provideCallingMiddleware() as Middleware<ReduxState>)

    override val callingMiddlewareActionHandler by lazy {
        CallingMiddlewareActionHandlerImpl(
            provideCallingService(),
            provideCoroutineContextProvider()
        )
    }


    //region Logger
    private val logger by lazy {
        DefaultLogger()
    }

    private fun provideLogger(): Logger {
        return logger
    }
    //endregion

    //region GetContext
    private fun provideApplicationContext(): Context {
        return parentContext.applicationContext
    }
    //endregion

    //region Calling SDK Wrapper
    private fun provideCallingSDKWrapper(): CallingSDKWrapper {
        return callingSDKWrapper
    }

    private val callingSDKWrapper by lazy {
        CallingSDKWrapper(
            callCompositeConfiguration,
            provideApplicationContext(),
            provideCallingSDKEventHandler(),
        )
    }
    //endregion

    //region Calling SDK Event Handler
    private fun provideCallingSDKEventHandler(): CallingSDKEventHandler {
        return callingSDKEventHandler
    }

    private val callingSDKEventHandler by lazy {
        CallingSDKEventHandler(
            provideCoroutineContextProvider()
        )
    }
    //endregion

    //region Calling Service
    private val callingService by lazy {
        CallingService(provideCallingSDKWrapper(), provideCoroutineContextProvider())
    }

    private fun provideCallingService(): CallingService {
        return callingService
    }
    //endregion

    //region Coroutine Context Provider
    private fun provideCoroutineContextProvider(): CoroutineContextProvider {
        return coroutineContextProvider
    }

    private val coroutineContextProvider by lazy {
        CoroutineContextProvider()
    }
    //endregion

    override val errorHandler by lazy {
        ErrorHandler(configuration, appStore)
    }
}
