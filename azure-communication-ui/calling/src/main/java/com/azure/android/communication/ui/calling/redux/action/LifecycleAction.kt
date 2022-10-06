// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

internal sealed class LifecycleAction : Action {
    object EnterForegroundTriggered : LifecycleAction()
    object EnterBackgroundTriggered : LifecycleAction()

    object EnterForegroundSucceeded : LifecycleAction()
    object EnterBackgroundSucceeded : LifecycleAction()
}
