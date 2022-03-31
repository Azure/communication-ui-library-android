// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration.events

import com.azure.android.communication.ui.CallingEventHandler

internal class CallCompositeEventsHandler {
    private var errorHandlers: CallingEventHandler? = null

    fun getOnErrorHandler() = errorHandlers

    fun setOnErrorHandler(errorHandler: CallingEventHandler?) {
        errorHandlers = errorHandler
    }
}
