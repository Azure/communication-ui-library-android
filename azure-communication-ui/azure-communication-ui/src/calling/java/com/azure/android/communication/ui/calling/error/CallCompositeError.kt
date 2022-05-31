// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.error

import com.azure.android.communication.ui.calling.models.internal.ErrorCode

internal class CallCompositeError(
    var errorCode: ErrorCode,
    var cause: Throwable,
)
