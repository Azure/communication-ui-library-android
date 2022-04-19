// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import android.app.Activity
import android.content.Context
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.state.PermissionStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.utilities.OffHookDetectionReceiver
import kotlinx.coroutines.flow.collect

// Similar to AccessibilityAnnouncementManager, but more generic for hooks.
// E.g. First use case is the Off Hook listener, which should only start if permissions are granted
//
internal class ReduxHookManager(
    private val store: Store<ReduxState>,
) {

    private var lastState: ReduxState = store.getCurrentState()

    suspend fun start(activity: Activity) {
        store.getStateFlow().collect { newState ->
            reduxHooks.forEach {
                if (it.shouldTrigger(lastState, newState)) it.action(activity, store)
            }
            lastState = newState
        }
    }

}

// Accessibility Hook
//
// shouldTrigger -> detect if we should announce something
// message -> get the text tp read
internal abstract class ReduxTrigger {
    abstract fun shouldTrigger(lastState: ReduxState, newState: ReduxState): Boolean
    abstract fun action(context: Context, store: Store<ReduxState>)
}

// Hook to announce when participants join/leave a meeting
internal class ManagePhoneOffHookListener : ReduxTrigger() {
    var receiver : OffHookDetectionReceiver? = null
    private val started
        get() = receiver != null

    override fun shouldTrigger(lastState: ReduxState, newState: ReduxState) =
        (!started && newState.permissionState.audioPermissionState == PermissionStatus.GRANTED)

    override fun action(context: Context, store: Store<ReduxState>) {
        when (started) {
            false -> {
                receiver = OffHookDetectionReceiver.register(context);
            }
        }
    }
}


// List of all hooks
internal val reduxHooks = listOf(
    ManagePhoneOffHookListener()
)
