// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux

import com.azure.android.communication.ui.redux.action.Action
import kotlinx.coroutines.flow.StateFlow

internal interface Store<S> {
    fun dispatch(action: Action)
    fun getStateFlow(): StateFlow<S>
    fun getCurrentState(): S
    fun end()
}
