// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent

// Handles forwarding of error messages to the CallLauncherActivity
//
// CallLauncherActivity is loosely coupled and will detach the weak reference after disposed.
class CallLauncherActivityErrorHandler(
    private val errorHandler: OnErrorEventHandler
) :
    CallCompositeEventHandler<CallCompositeErrorEvent> {

    override fun handle(it: CallCompositeErrorEvent) {
        errorHandler.showError("${it.errorCode} ${it.cause?.message}")
    }
}
