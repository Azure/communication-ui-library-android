// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.error.FatalError

internal data class ErrorState(
    val fatalError: FatalError?,
    val callStateError: CallStateError?,
)
