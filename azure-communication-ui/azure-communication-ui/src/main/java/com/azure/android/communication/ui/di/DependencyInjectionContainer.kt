// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.di

import com.azure.android.communication.ui.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.error.ErrorHandler
import com.azure.android.communication.ui.presentation.VideoViewManager
import com.azure.android.communication.ui.presentation.manager.AudioSessionManager
import com.azure.android.communication.ui.presentation.manager.LifecycleManager
import com.azure.android.communication.ui.presentation.manager.PermissionManager
import com.azure.android.communication.ui.presentation.navigation.NavigationRouter
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.middleware.handler.CallingMiddlewareActionHandler
import com.azure.android.communication.ui.redux.state.ReduxState

// Dependency Container for the Call Composite Activity
// For implementation
// @see: {@link com.azure.android.communication.ui.di.DependencyInjectionContainerImpl}
internal interface DependencyInjectionContainer {
    // Redux Store
    val appStore: Store<ReduxState>
    val callingMiddlewareActionHandler: CallingMiddlewareActionHandler

    // Config
    val configuration: CallCompositeConfiguration
    val errorHandler: ErrorHandler

    // System
    val permissionManager: PermissionManager
    val audioSessionManager: AudioSessionManager
    val lifecycleManager: LifecycleManager
    val navigationRouter: NavigationRouter

    // UI
    val videoViewManager: VideoViewManager
}
