// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

internal sealed class DeviceConfigurationAction : Action {
    class KeyboardVisibilityChanged(val isSoftwareKeyboardVisible: Boolean) : DeviceConfigurationAction()
    class IsTableChanged(val isTablet: Boolean) : DeviceConfigurationAction()
    class IsPortraitChanged(val isPortrait: Boolean) : DeviceConfigurationAction()
}
