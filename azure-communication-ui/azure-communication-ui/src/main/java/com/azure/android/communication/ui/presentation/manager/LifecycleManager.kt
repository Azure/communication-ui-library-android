// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.action.LifecycleAction
import com.azure.android.communication.ui.redux.state.ReduxState

internal interface LifecycleManager {
    fun pause()
    fun resume()
}

internal class LifecycleManagerImpl(
    private val store: Store<ReduxState>,
) :
    LifecycleManager {

    override fun pause() {
        store.dispatch(action = LifecycleAction.EnterBackgroundTriggered())
    }

    override fun resume() {
        store.dispatch(action = LifecycleAction.EnterForegroundTriggered())
    }
}
