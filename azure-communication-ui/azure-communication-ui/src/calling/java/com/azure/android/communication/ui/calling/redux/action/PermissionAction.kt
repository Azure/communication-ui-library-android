// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

import com.azure.android.communication.ui.calling.redux.state.PermissionStatus

internal sealed class PermissionAction :
    Action {
    sealed class AudioPermissionRequested : PermissionAction()
    sealed class CameraPermissionRequested : PermissionAction()
    sealed class PhonePermissionRequested : PermissionAction()
    sealed class AudioPermissionIsSet(val permissionState: PermissionStatus) : PermissionAction()
    sealed class CameraPermissionIsSet(val permissionState: PermissionStatus) : PermissionAction()
    sealed class PhonePermissionIsSet(val permissionState: PermissionStatus) : PermissionAction()
}
