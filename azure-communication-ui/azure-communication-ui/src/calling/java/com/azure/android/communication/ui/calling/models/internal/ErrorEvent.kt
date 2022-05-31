// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling.models.internal

internal data class ErrorEvent(
    val errorCode: ErrorCode,
    val cause: Throwable,
)
