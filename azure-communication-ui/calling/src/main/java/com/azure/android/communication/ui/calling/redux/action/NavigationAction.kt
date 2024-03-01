// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

internal sealed class NavigationAction :
    Action {
    class SetupLaunched : NavigationAction()
    class CallLaunched : NavigationAction()
    class Exit : NavigationAction()
    class CallLaunchWithoutSetup : NavigationAction()
    class HideSupportForm : NavigationAction()
    class ShowSupportForm : NavigationAction()

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }
}
