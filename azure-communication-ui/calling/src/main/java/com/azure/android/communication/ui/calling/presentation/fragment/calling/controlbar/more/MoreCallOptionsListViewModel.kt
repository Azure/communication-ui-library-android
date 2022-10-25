// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more

import com.azure.android.communication.ui.calling.presentation.manager.DiagnosticsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class MoreCallOptionsListViewModel(
    private val diagnosticsManager: DiagnosticsManager
) {
    private val unknown = "UNKNOWN"
    var callId: String = setCallIdField(null)

    val displayStateFlow = MutableStateFlow(false)

    fun display() {
        displayStateFlow.value = true
    }

    fun close() {
        displayStateFlow.value = false
    }

    fun init(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            diagnosticsManager.diagnosticsInfoFlow.collect {
                callId = setCallIdField(it.lastKnownCallId)
            }
        }
    }

    private fun setCallIdField(callId: String?): String {
        return "Call ID: \"${ if (callId.isNullOrEmpty()) unknown else callId}\""
    }
}
