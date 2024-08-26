// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.di

import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.data.CallHistoryRepository
import com.azure.android.communication.ui.calling.error.ErrorHandler
import com.azure.android.communication.ui.calling.handlers.CallStateHandler
import com.azure.android.communication.ui.calling.handlers.RemoteParticipantHandler
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.presentation.CallCompositeActivity
import com.azure.android.communication.ui.calling.presentation.VideoViewManager
import com.azure.android.communication.ui.calling.presentation.manager.AccessibilityAnnouncementManager
import com.azure.android.communication.ui.calling.presentation.manager.AudioFocusManager
import com.azure.android.communication.ui.calling.presentation.manager.AudioModeManager
import com.azure.android.communication.ui.calling.presentation.manager.AudioSessionManager
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
/* <CUSTOM_CALL_HEADER>
import com.azure.android.communication.ui.calling.presentation.manager.CallDurationManager
</CUSTOM_CALL_HEADER> */
import com.azure.android.communication.ui.calling.presentation.manager.CapabilitiesManager
import com.azure.android.communication.ui.calling.presentation.manager.CaptionsDataManager
import com.azure.android.communication.ui.calling.presentation.manager.CompositeExitManager
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager
import com.azure.android.communication.ui.calling.presentation.manager.LifecycleManager
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.presentation.manager.PermissionManager
import com.azure.android.communication.ui.calling.presentation.navigation.NavigationRouter
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.middleware.handler.CallingMiddlewareActionHandler
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.presentation.manager.MultitaskingManager
import com.azure.android.communication.ui.calling.service.CallHistoryService
import com.azure.android.communication.ui.calling.service.CallingService
import com.azure.android.communication.ui.calling.service.NotificationService
import java.lang.ref.WeakReference

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
    val callStateHandler: CallStateHandler

    // System
    val permissionManager: PermissionManager
    val avatarViewManager: AvatarViewManager
    val audioSessionManager: AudioSessionManager
    val accessibilityManager: AccessibilityAnnouncementManager
    val lifecycleManager: LifecycleManager
    val multitaskingManager: MultitaskingManager
    val compositeExitManager: CompositeExitManager
    val navigationRouter: NavigationRouter
    val notificationService: NotificationService
    val audioFocusManager: AudioFocusManager
    val networkManager: NetworkManager
    val debugInfoManager: DebugInfoManager
    val callHistoryService: CallHistoryService
    val audioModeManager: AudioModeManager

    // UI
    val videoViewManager: VideoViewManager

    // Data
    val callHistoryRepository: CallHistoryRepository

    // Calling Service
    val callingService: CallingService

    // Added for Screenshot ability.
    //
    // To poke across contexts to do. (CallComposite Contoso Host -> CallCompositeActivity)
    // This isn't generally encouraged, but CallCompositeActivity context is needed for screenshot.
    var callCompositeActivityWeakReference: WeakReference<CallCompositeActivity>

    val capabilitiesManager: CapabilitiesManager
    val captionsDataManager: CaptionsDataManager

    /* <CUSTOM_CALL_HEADER>
    val callDurationManager: CallDurationManager
    </CUSTOM_CALL_HEADER> */
}
