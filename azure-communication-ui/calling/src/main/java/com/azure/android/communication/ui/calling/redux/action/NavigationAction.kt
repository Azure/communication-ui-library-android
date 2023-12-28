// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

internal sealed class NavigationAction :
    Action {
    class SetupLaunched : NavigationAction()
    class CallLaunched : NavigationAction()
    class Exit : NavigationAction()
    class CallLaunchWithoutSetup : NavigationAction()
    class ShowSupportForm : NavigationAction()
    class HideSupportForm : NavigationAction()

    // Since we are just using the type system, equality just on type is sufficient.
    override fun equals(other: Any?): Boolean {
        other?.let {
            return this::class == other::class
        }
        return false
    }
}
