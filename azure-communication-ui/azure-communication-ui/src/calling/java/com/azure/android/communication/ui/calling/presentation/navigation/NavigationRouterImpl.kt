// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.navigation

import com.azure.android.communication.ui.calling.redux.state.NavigationStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.reduxkotlin.Store
import org.reduxkotlin.StoreSubscription

internal class NavigationRouterImpl(private val store: Store<ReduxState>) : NavigationRouter {

    private var unsubscribe: StoreSubscription? = null

    private val navigationFlow = MutableStateFlow(NavigationStatus.NONE)

    override fun start() {
        unsubscribe = store.subscribe {
            navigationFlow.value = store.state.navigationState.navigationState
        }
    }

    override fun getNavigationStateFlow(): StateFlow<NavigationStatus> {
        return navigationFlow
    }
}
