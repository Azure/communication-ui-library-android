// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.navigation

import com.azure.android.communication.ui.redux.state.NavigationStatus
import kotlinx.coroutines.flow.StateFlow

internal interface NavigationRouter {
    suspend fun start()
    fun getNavigationStateFlow(): StateFlow<NavigationStatus>
}
