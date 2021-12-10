// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.di

import androidx.fragment.app.FragmentFactory
import com.azure.android.communication.ui.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.error.ErrorHandler
import com.azure.android.communication.ui.presentation.UIManager
import com.azure.android.communication.ui.presentation.VideoViewManager
import com.azure.android.communication.ui.presentation.manager.AudioSessionManager
import com.azure.android.communication.ui.presentation.manager.LifecycleManager
import com.azure.android.communication.ui.presentation.manager.PermissionManager
import com.azure.android.communication.ui.presentation.navigation.NavigationRouter
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.middleware.handler.CallingMiddlewareActionHandler
import com.azure.android.communication.ui.redux.state.ReduxState

internal interface DependencyInjectionContainer {
    fun provideUIManager(): UIManager
    fun provideNavigationRouter(): NavigationRouter
    fun provideFragmentFactory(): FragmentFactory
    fun provideStore(): Store<ReduxState>
    fun provideConfiguration(): CallCompositeConfiguration
    fun providePermissionManager(): PermissionManager
    fun provideAudioSessionManager(): AudioSessionManager
    fun provideLifecycleManager(): LifecycleManager
    fun provideErrorHandler(): ErrorHandler
    fun provideCallingMiddlewareActionHandler(): CallingMiddlewareActionHandler
    fun provideVideoViewManager(): VideoViewManager
}
