// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

internal sealed class RttAction : Action {
    class RttMessagesUpdated(val rttContent: String, val participantId: String) : RttAction()
    class SendRtt(val message: String) : RttAction()

    class DisableRttLocally() : RttAction()

    class EnableRtt(): RttAction()
}
