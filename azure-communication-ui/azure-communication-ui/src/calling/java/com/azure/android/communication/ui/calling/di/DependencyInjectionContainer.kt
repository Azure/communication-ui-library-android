// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.di

import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.error.ErrorHandler
import com.azure.android.communication.ui.calling.handlers.RemoteParticipantHandler
import com.azure.android.communication.ui.calling.presentation.VideoViewManager
import com.azure.android.communication.ui.calling.presentation.manager.*
import com.azure.android.communication.ui.calling.presentation.manager.AccessibilityAnnouncementManager
import com.azure.android.communication.ui.calling.presentation.manager.AudioFocusManager
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.calling.presentation.manager.BluetoothDetectionManager
import com.azure.android.communication.ui.calling.presentation.manager.LifecycleManager
import com.azure.android.communication.ui.calling.presentation.manager.PermissionManager
import com.azure.android.communication.ui.calling.presentation.navigation.NavigationRouter
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.middleware.handler.CallingMiddlewareActionHandler
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.NotificationService

// Dependency Container for the Call Composite Activity
// For implementation
// @see: {@link DependencyInjectionContainerImpl}
internal interface DependencyInjectionContainer {
    // Redux Store
    val appStore: Store<ReduxState>
    val callingMiddlewareActionHandler: CallingMiddlewareActionHandler

    // Config
    val configuration: CallCompositeConfiguration
    val errorHandler: ErrorHandler
    val remoteParticipantHandler: RemoteParticipantHandler

    // System
    val permissionManager: PermissionManager
    val avatarViewManager: AvatarViewManager
    val bluetoothDetectionManager: BluetoothDetectionManager
    val accessibilityManager: AccessibilityAnnouncementManager
    val lifecycleManager: LifecycleManager
    val navigationRouter: NavigationRouter
    val notificationService: NotificationService
    val audioFocusManager: AudioFocusManager

    // UI
    val videoViewManager: VideoViewManager
}
