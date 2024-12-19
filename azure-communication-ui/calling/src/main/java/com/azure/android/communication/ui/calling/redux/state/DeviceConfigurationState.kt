// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

internal data class DeviceConfigurationState(
    val isSoftwareKeyboardVisible: Boolean,
    val isTablet: Boolean,
    val isPortrait: Boolean,
)
