// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallInfo

interface CallCompositeEvents {
    fun getCallComposite(): CallComposite?
    fun showIncomingCallUI(incomingCallInfo: CallCompositeIncomingCallInfo, applicationContext: Context)
    fun hideIncomingCallUI(applicationContext: Context)
    fun handleIncomingCall(
        data: Map<String, String>,
        acsToken: String,
        displayName: String,
        applicationContext: Context
    )
    fun onCompositeDismiss(applicationContext: Context)
    fun acceptIncomingCall(applicationContext: Context)
}
