// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.action

internal sealed class LifecycleAction : Action {
    object EnterForeground : LifecycleAction()

    object EnterBackground : LifecycleAction()
}
