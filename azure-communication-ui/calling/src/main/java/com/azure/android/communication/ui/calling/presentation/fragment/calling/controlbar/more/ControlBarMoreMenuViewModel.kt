// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more

import kotlinx.coroutines.flow.MutableStateFlow

internal class ControlBarMoreMenuViewModel {

    private val unknown = "UNKNOWN"
    var callId: String = unknown

    val displayStateFlow = MutableStateFlow(false)

    fun display() {
        displayStateFlow.value = true
    }

    fun close() {
        displayStateFlow.value = false
    }

    fun update(callId: String?) {
        setCallIdField(callId)
    }

    fun init(callId: String?) {
        setCallIdField(callId)
    }

    private fun setCallIdField(callId: String?) {
        this.callId = if (callId.isNullOrEmpty()) unknown else callId
    }
}
