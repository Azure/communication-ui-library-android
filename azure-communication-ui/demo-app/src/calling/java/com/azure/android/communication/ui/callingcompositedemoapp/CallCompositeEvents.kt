// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallInfo

interface CallCompositeEvents {
    fun getCallComposite(): CallComposite?
    fun showIncomingCallUI(incomingCallInfo: CallCompositeIncomingCallInfo)
    fun hideIncomingCallUI()
    fun handleIncomingCall(data: Map<String, String>,
                           acsToken: String,
                           displayName: String)
    fun onCompositeDismiss()
    fun onRemoteParticipantJoined(rawId: String)
    fun incomingCallEnded()
}
