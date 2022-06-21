// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.arch.redux

internal interface Store<S> {
    fun dispatch(action: Any)
    fun getCurrentState(): S
    fun end()
}
