// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service

import com.azure.android.communication.ui.calling.models.CallCompositeDiagnosticsInfo

internal interface DiagnosticsService {
    fun getDiagnosticsInfo(): CallCompositeDiagnosticsInfo
}

internal class DiagnosticsServiceImpl : DiagnosticsService {
    private var callId: String? = null

    override fun getDiagnosticsInfo(): CallCompositeDiagnosticsInfo {
        return CallCompositeDiagnosticsInfo().setLastKnownCallId(callId)
    }
}
