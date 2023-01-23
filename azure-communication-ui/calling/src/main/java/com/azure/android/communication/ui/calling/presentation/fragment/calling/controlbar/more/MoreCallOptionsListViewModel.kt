// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more

import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager
import kotlinx.coroutines.flow.MutableStateFlow

internal class MoreCallOptionsListViewModel(
    private val debugInfoManager: DebugInfoManager
) {
    private val unknown = "UNKNOWN"
    val callId: String
        get() {
            val lastKnownCallId = debugInfoManager.getDebugInfo().callHistoryRecordList.lastOrNull()?.callIds?.lastOrNull()
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
