// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

internal sealed class LifecycleAction : Action {
    class EnterForegroundTriggered : LifecycleAction()

    class EnterBackgroundTriggered : LifecycleAction()

    class EnterForegroundSucceeded : LifecycleAction()

    class EnterBackgroundSucceeded : LifecycleAction()
}
