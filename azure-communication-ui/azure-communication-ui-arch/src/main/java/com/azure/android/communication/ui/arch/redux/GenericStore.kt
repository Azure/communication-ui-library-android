// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.arch.redux

// Before: Store<T>
// Now GenericStore
// I've added a GenericState that is extensible
//
// I've removed the Type from Store<> and AppStore<T>, as generics get messy when combined with
// Service Locator. E.g. getCurrentState<T>() wouldn't know the type of T after locating via
// the service locator.
//
// By making it just work on GenericState with types, it becomes ServiceLocator friendly
interface GenericStore {
    fun addListener(listener: StoreListener)
    fun removeListener(listener: StoreListener)
    fun dispatch(action: Any)
    fun getCurrentState(): GenericState
    fun end()
}

data class StoreListener(val onStoreChanged: (currentState: GenericState) -> Unit)
