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
    val uiManager: UIManager
    val navigationRouter: NavigationRouter
    val fragmentFactory: FragmentFactory
    val appStore: Store<ReduxState>
    val configuration: CallCompositeConfiguration
    val permissionManager: PermissionManager
    val audioSessionManager: AudioSessionManager
    val lifecycleManager: LifecycleManager
    val errorHandler: ErrorHandler
    val callingMiddlewareActionHandler: CallingMiddlewareActionHandler
    val videoViewManager: VideoViewManager
}
