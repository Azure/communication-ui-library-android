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
}
