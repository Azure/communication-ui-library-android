// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import android.app.Activity
import android.content.Context
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.utilities.DetectPhoneCallTrigger
import kotlinx.coroutines.flow.collect

// Redux Trigger Interface
internal abstract class ReduxTrigger {
    abstract fun shouldTrigger(lastState: ReduxState, newState: ReduxState): Boolean
    abstract fun action(context: Context, store: Store<ReduxState>)
}

// Manager to create Hooks that do things with context.
// E.g. Bind something to a context, or react to state changes with context.
// 1) Implement ReduxTrigger
// 2) Register in Triggers list
internal class ReduxTriggerManager(
    private val store: Store<ReduxState>,
) {
    // Add Triggers to this list
    private val triggers = listOf(
        DetectPhoneCallTrigger()
    )

    private var lastState: ReduxState = store.getCurrentState()

    suspend fun start(activity: Activity) {
        store.getStateFlow().collect { newState ->
            triggers.forEach {
                if (it.shouldTrigger(lastState, newState)) it.action(activity, store)
            }
            lastState = newState
        }
    }

}


