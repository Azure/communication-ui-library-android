// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.service.CallingService

internal class CaptionsViewManager(private val callingService: CallingService,) {
    fun getCaptionsDataReceivedSharedFlow() = callingService.getCaptionsReceivedSharedFlow()
}
