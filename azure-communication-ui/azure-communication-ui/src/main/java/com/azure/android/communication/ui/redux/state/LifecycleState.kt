// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.state

internal enum class LifecycleStatus {
    FOREGROUND,
    BACKGROUND,
}

internal data class LifecycleState(val state: LifecycleStatus)
