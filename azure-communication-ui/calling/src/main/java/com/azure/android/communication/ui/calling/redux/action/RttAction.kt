// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

internal sealed class RttAction : Action {
    class SendRtt(val message: String, val isFinalized: Boolean) : RttAction()

    class EnableRtt : RttAction()
    class UpdateMaximized(val isMaximized: Boolean) : RttAction()
}
