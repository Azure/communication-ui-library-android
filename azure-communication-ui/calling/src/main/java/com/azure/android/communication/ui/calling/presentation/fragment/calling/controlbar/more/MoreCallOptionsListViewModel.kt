// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more

import com.azure.android.communication.ui.calling.presentation.manager.DiagnosticsManager
import kotlinx.coroutines.flow.MutableStateFlow

internal class MoreCallOptionsListViewModel(
    private val diagnosticsManager: DiagnosticsManager
) {
    private val unknown = "UNKNOWN"
    val callId: String
        get() {
            val lastKnownCallId = diagnosticsManager.diagnostics.lastKnownCallId
            return "Call ID: \"${if (lastKnownCallId.isNullOrEmpty()) unknown else lastKnownCallId}\""
        }

    val displayStateFlow = MutableStateFlow(false)

    fun display() {
        displayStateFlow.value = true
    }

    fun close() {
        displayStateFlow.value = false
    }
}
