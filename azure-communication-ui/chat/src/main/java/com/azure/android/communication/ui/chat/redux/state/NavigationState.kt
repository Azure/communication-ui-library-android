// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.state

internal enum class NavigationStatus {
    NONE,
}

internal data class NavigationState(val navigationStatus: NavigationStatus)
