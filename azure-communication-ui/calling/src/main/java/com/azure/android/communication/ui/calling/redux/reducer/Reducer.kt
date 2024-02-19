// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action

internal interface Reducer<S> {
    fun reduce(
        state: S,
        action: Action,
    ): S
}
