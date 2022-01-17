// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.di

import android.content.Context
import androidx.fragment.app.FragmentFactory
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
import com.azure.android.communication.ui.presentation.manager.LifecycleManager
import com.azure.android.communication.ui.presentation.manager.LifecycleManagerImpl
import com.azure.android.communication.ui.presentation.manager.PermissionManager
import com.azure.android.communication.ui.presentation.navigation.NavigationRouter
import com.azure.android.communication.ui.presentation.navigation.NavigationRouterImpl
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.Middleware
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.middleware.CallingMiddleware
import com.azure.android.communication.ui.redux.middleware.CallingMiddlewareImpl
import com.azure.android.communication.ui.redux.middleware.handler.CallingMiddlewareActionHandler
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



    override val uiManager by lazy {
        UIManager(parentContext)
    }

    override val navigationRouter by lazy {
        NavigationRouterImpl(provideStore())
    }

    override val fragmentFactory by lazy {
        CallingCompositeFragmentFactory(provideViewModelFactory(), provideVideoViewManager())
    }
    //endregion

    //region Participant Grid Cell ViewModel Factory
    private fun provideParticipantGridCellViewModelFactory(): ParticipantGridCellViewModelFactory {
        return participantGridCellViewModelFactory
    }

    private val participantGridCellViewModelFactory by lazy {
        ParticipantGridCellViewModelFactory()
    }
    //endregion

    //region Setup View Model Factory
    private fun provideSetupViewModelFactory(): SetupViewModelFactory {
        return setupViewModelFactory
    }

    private val setupViewModelFactory by lazy {
        SetupViewModelFactory(provideStore())
    }
    //endregion

    //region Calling View Model Factory
    private fun provideCallingViewModelFactory(): CallingViewModelFactory {
        return callingViewModelFactory
    }

    private val callingViewModelFactory by lazy {
        CallingViewModelFactory(provideStore(), provideParticipantGridCellViewModelFactory())
    }
    //endregion

    //region View Model Factory
    private val viewModelFactory by lazy {
        ViewModelFactory(
            CallingViewModel(
                provideStore(),
                provideCallingViewModelFactory()
            ),
            SetupViewModel(
                provideStore(),
                provideSetupViewModelFactory()
            )
        )
    }

    private fun provideViewModelFactory(): ViewModelFactory {
        return viewModelFactory
    }

    //endregion

    //region Video View Manager
    private val videoViewManager by lazy {
        VideoViewManager(provideCallingSDKWrapper(), provideApplicationContext())
    }

    override fun provideVideoViewManager(): VideoViewManager {
        return videoViewManager
    }
    //endregion

    //region Configuration
    override fun provideConfiguration(): CallCompositeConfiguration {
        return callCompositeConfiguration
    }
    //endregion

    //region Permission Manager
    override fun providePermissionManager(): PermissionManager {
        return permissionManager
    }

    private val permissionManager by lazy {
        PermissionManager(provideStore())
    }
    //endregion

    //region Audio Session Manager
    override fun provideAudioSessionManager(): AudioSessionManager {
        return audioSessionManager
    }

    private val audioSessionManager by lazy {
        AudioSessionManager(provideStore())
    }
    //endregion

    //region Lifecycle Manager
    override fun provideLifecycleManager(): LifecycleManager {
        return lifecycleManager
    }

    private val lifecycleManager by lazy {
        LifecycleManagerImpl(provideStore())
    }
    //endregion

    //region Store
    override fun provideStore(): Store<ReduxState> {
        return appStore
    }

    private val appStore by lazy {
        AppStore(
            provideState(),
            provideReduxStateReducer(),
            provideAppMiddleware(),
            provideStoreHandlerThread()
        )
    }
    //endregion

    //region StoreHandlerThread

    private fun provideStoreHandlerThread(): StoreHandlerThread {
        return StoreHandlerThread()
    }

    //endregion

    //region Reducer
    private fun provideReduxStateReducer(): Reducer<ReduxState> {
        return appReduxStateReducer as Reducer<ReduxState>
    }

    private val appReduxStateReducer by lazy {
        AppStateReducer(
            provideCallStateReducer(),
            provideParticipantStateReducer(),
            provideLocalParticipantStateReducer(),
            providePermissionStateReducer(),
            provideLifecycleReducer(),
            provideErrorReducer(),
            provideNavigationReducer()
        )
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
        return AppReduxState(provideConfiguration().callConfig!!.displayName)
    }

    //endregion

    //region Middleware

    private fun provideCallingMiddleware(): CallingMiddleware {
        return CallingMiddlewareImpl(
            provideCallingMiddlewareActionHandler(),
            provideLogger()
        )
    }

    private fun provideAppMiddleware(): MutableList<Middleware<ReduxState>> =
        mutableListOf(provideCallingMiddleware() as Middleware<ReduxState>)
    //endregion

    //region Calling Middleware Action Handler
    private val callingMiddlewareActionHandler by lazy {
        CallingMiddlewareActionHandlerImpl(
            provideCallingService(),
            provideCoroutineContextProvider()
        )
    }

    override fun provideCallingMiddlewareActionHandler(): CallingMiddlewareActionHandler {
        return callingMiddlewareActionHandler
    }
    //endregion

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

    //region Error Handler
    override fun provideErrorHandler(): ErrorHandler {
        return errorHandler
    }

    private val errorHandler by lazy {
        ErrorHandler(provideConfiguration(), provideStore())
    }
    //endregion
}
