// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.di

import com.azure.android.communication.ui.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.configuration.LocalizationProvider
import com.azure.android.communication.ui.error.ErrorHandler
import com.azure.android.communication.ui.presentation.VideoViewManager
import com.azure.android.communication.ui.presentation.manager.*
import com.azure.android.communication.ui.presentation.manager.AccessibilityAnnouncementManager
import com.azure.android.communication.ui.presentation.manager.AudioSessionManager
import com.azure.android.communication.ui.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.presentation.manager.LifecycleManager
import com.azure.android.communication.ui.presentation.manager.PermissionManager
import com.azure.android.communication.ui.presentation.manager.ReduxHookManager
import com.azure.android.communication.ui.presentation.navigation.NavigationRouter
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.middleware.handler.CallingMiddlewareActionHandler
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.service.calling.NotificationService

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
    val avatarViewManager: AvatarViewManager
    val audioSessionManager: AudioSessionManager
    val accessibilityManager: AccessibilityAnnouncementManager
    val reduxHookManager : ReduxHookManager
    val lifecycleManager: LifecycleManager
    val navigationRouter: NavigationRouter
    val notificationService: NotificationService
    val localizationProvider: LocalizationProvider

    // UI
    val videoViewManager: VideoViewManager
}
