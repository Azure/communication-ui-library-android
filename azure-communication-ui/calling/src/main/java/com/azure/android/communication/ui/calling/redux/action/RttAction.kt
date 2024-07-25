// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <RTT_POC> */

package com.azure.android.communication.ui.calling.redux.action

import com.azure.android.communication.ui.calling.data.model.RawRttPayload

internal sealed class RttAction : Action {
    class IncomingMessageReceived(val rttContent: String, val participantId: String) : RttAction()
    class SendRtt(val message: String) : RttAction()

    class DisableRttLocally() : RttAction()
}
/* </RTT_POC> */
