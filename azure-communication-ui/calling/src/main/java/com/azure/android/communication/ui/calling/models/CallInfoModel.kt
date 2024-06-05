// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.redux.state.CallingStatus

internal class CallInfoModel(
    val callingStatus: CallingStatus,
    val callStateError: CallStateError?,
    val callEndReasonCode: Int? = null,
    val callEndReasonSubCode: Int? = null,
)
