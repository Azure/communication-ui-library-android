// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.action

internal sealed class LifecycleAction : Action {
    class Wakeup : LifecycleAction()
    class Shutdown : LifecycleAction()

    class EnterForegroundTriggered : LifecycleAction()
    class EnterBackgroundTriggered : LifecycleAction()

    class EnterForegroundSucceeded : LifecycleAction()
    class EnterBackgroundSucceeded : LifecycleAction()
}
