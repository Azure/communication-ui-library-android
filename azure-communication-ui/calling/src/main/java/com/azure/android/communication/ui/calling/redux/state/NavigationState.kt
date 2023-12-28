// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import com.azure.android.communication.ui.calling.redux.action.NavigationAction

internal enum class NavigationStatus {
    NONE,
    IN_CALL,
    SETUP,
    EXIT,
}

internal data class NavigationState(val navigationState: NavigationStatus, val supportVisible: Boolean = false)
