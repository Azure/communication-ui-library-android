// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.navigation

import com.azure.android.communication.ui.redux.state.NavigationStatus

internal interface NavigationRouter {
    suspend fun start()
    fun addOnNavigationStateChanged(callback: (navigationState: NavigationStatus) -> Unit)
    fun removeOnNavigationStateChanged(callback: (navigationState: NavigationStatus) -> Unit)
}
