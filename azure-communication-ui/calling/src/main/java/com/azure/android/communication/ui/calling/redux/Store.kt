// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux

import com.azure.android.communication.ui.calling.redux.action.Action
import kotlinx.coroutines.flow.MutableStateFlow

internal interface Store<S> {
    fun dispatch(action: Action)

    fun getStateFlow(): MutableStateFlow<S>

    fun getCurrentState(): S

    fun end()
}
