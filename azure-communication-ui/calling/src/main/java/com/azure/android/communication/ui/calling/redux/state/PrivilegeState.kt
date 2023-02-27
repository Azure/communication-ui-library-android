// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

internal data class PrivilegeState(
    val canUseCamera: Boolean,
    val canUseMicrophone: Boolean,
)
