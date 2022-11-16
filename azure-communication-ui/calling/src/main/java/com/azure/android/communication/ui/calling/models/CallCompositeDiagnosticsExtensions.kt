// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal fun CallCompositeDiagnostics.setCallId(lastKnownCallId: String?) {
    this.lastKnownCallId = lastKnownCallId
}

internal fun buildCallCompositeDiagnostics(): CallCompositeDiagnostics = CallCompositeDiagnostics()
