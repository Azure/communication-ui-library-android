// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.common.controlbarmore

import kotlinx.coroutines.flow.MutableStateFlow

internal class ControlBarMoreMenuViewModel {
    val displayStateFlow = MutableStateFlow(false)

    fun display() {
        displayStateFlow.value = true
    }

    fun close() {
        displayStateFlow.value = false
    }
}
