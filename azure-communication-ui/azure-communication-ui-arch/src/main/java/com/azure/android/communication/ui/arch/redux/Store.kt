// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.arch.redux

interface Store<S> {
    fun addListener(listener: StoreListener<S>)
    fun removeListener(listener: StoreListener<S>)
    fun dispatch(action: Any)
    fun getCurrentState(): S
    fun end()
}

data class StoreListener<S> (val onStoreChanged:(currentState : S) -> Unit)