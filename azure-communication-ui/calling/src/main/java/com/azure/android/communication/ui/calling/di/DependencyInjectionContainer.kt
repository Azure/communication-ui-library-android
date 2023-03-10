// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.di

import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.data.CallHistoryRepository
import com.azure.android.communication.ui.calling.error.ErrorHandler
import com.azure.android.communication.ui.calling.handlers.RemoteParticipantHandler
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.presentation.VideoViewManager
import com.azure.android.communication.ui.calling.presentation.manager.AccessibilityAnnouncementManager
import com.azure.android.communication.ui.calling.presentation.manager.AudioFocusManager
import com.azure.android.communication.ui.calling.presentation.manager.AudioSessionManager
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.calling.presentation.manager.LifecycleManager
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.presentation.manager.PermissionManager
import com.azure.android.communication.ui.calling.presentation.navigation.NavigationRouter
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.middleware.handler.CallingMiddlewareActionHandler
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager
import com.azure.android.communication.ui.calling.service.CallHistoryService
import com.azure.android.communication.ui.calling.service.NotificationService

// Dependency Container for the Call Composite Activity
// For implementation
// @see: {@link DependencyInjectionContainerImpl}
internal interface DependencyInjectionContainer {
    val logger: Logger

    // Redux Store
    val appStore: Store<ReduxState>
    val callingMiddlewareActionHandler: CallingMiddlewareActionHandler

    val callComposite: CallComposite

    // Config
    val configuration: CallCompositeConfiguration
    val errorHandler: ErrorHandler
    val remoteParticipantHandler: RemoteParticipantHandler

    // System
    val permissionManager: PermissionManager
    val avatarViewManager: AvatarViewManager
    val audioSessionManager: AudioSessionManager
    val accessibilityManager: AccessibilityAnnouncementManager
    val lifecycleManager: LifecycleManager
    val navigationRouter: NavigationRouter
    val notificationService: NotificationService
    val audioFocusManager: AudioFocusManager
    val networkManager: NetworkManager
    val debugInfoManager: DebugInfoManager
    val callHistoryService: CallHistoryService

    // UI
    val videoViewManager: VideoViewManager

    // Data
    val callHistoryRepository: CallHistoryRepository
}
