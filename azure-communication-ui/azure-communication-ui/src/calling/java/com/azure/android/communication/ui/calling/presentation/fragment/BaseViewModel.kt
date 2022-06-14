// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.reduxkotlin.Store

internal abstract class BaseViewModel constructor(
    protected val store: Store<ReduxState>,
) {

    open fun init(coroutineScope: CoroutineScope) {
        store.subscribe {
            onStateChange(store.state)
        }
    }

    protected abstract fun onStateChange(state: ReduxState)

    protected fun dispatchAction(action: Action) {
        store.dispatch(action)
    }
}
