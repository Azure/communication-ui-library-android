// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.action

import com.azure.android.communication.ui.error.CallStateError
import com.azure.android.communication.ui.error.FatalError

internal sealed class ErrorAction : Action {
    class FatalErrorOccurred(val error: FatalError) : ErrorAction()
    class CallStateErrorOccurred(val callStateError: CallStateError) : ErrorAction()
    class EmergencyExit : ErrorAction()
}
