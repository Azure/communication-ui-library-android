// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal fun CallCompositeDiagnosticsInfo.setCallId(lastKnownCallId: String?) {
    this.lastKnownCallId = lastKnownCallId
}

internal fun buildCallCompositeDiagnosticsInfo(): CallCompositeDiagnosticsInfo = CallCompositeDiagnosticsInfo()
