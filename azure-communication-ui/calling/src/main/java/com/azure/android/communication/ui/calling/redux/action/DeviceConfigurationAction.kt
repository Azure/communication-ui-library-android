// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

internal sealed class DeviceConfigurationAction : Action {
    class ToggleKeyboardVisibility(val isSoftwareKeyboardVisible: Boolean) : DeviceConfigurationAction()
    class ToggleTabletMode(val isTablet: Boolean) : DeviceConfigurationAction()
    class TogglePortraitMode(val isPortrait: Boolean) : DeviceConfigurationAction()
}
