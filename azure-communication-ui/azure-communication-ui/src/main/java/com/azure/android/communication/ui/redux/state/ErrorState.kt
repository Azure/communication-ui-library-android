// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.state

import com.azure.android.communication.ui.error.CallStateError
import com.azure.android.communication.ui.error.FatalError

internal data class ErrorState(
    val fatalError: FatalError?,
    val callStateError: CallStateError?,
)
