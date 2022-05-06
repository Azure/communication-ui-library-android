// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.error

import com.azure.android.communication.ui.calling.models.CommunicationUIErrorCode

internal class CallCompositeError(
    var communicationUIErrorCode: CommunicationUIErrorCode,
    var cause: Throwable,
)
