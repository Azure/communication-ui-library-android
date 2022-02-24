// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.navigation

import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.state.NavigationStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import kotlinx.coroutines.flow.collect

internal class NavigationRouterImpl(private val store: Store<ReduxState>) : NavigationRouter {

    private var currentNavigationState: NavigationStatus? = null
    private val subscribers = HashSet<(NavigationStatus) -> Unit>()

    override suspend fun start() {
        store.getStateFlow().collect {
            onNavigationStateChange(it)
        }
    }

    private fun onNavigationStateChange(state: ReduxState) {
        val navigationState = state.navigationState.navigationState

        if (navigationState != currentNavigationState) {
            currentNavigationState = navigationState
            subscribers.forEach { onNavigationStateChange ->
                onNavigationStateChange(navigationState)
            }
        }
    }

    override fun addOnNavigationStateChanged(callback: (navigationState: NavigationStatus) -> Unit) {
        subscribers.add(callback)
    }

    override fun removeOnNavigationStateChanged(callback: (navigationState: NavigationStatus) -> Unit) {
        subscribers.remove(callback)
    }
}
