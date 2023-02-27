// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

internal sealed class PrivilegeAction : Action {
    class CanUseMicIsSet(val canUseMic: Boolean) : PrivilegeAction()
    class CanUseCameraIsSet(val canUseCamera: Boolean) : PrivilegeAction()
}
