// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

internal sealed class PipAction : Action {
//    class PipModeRequested : PipAction()
    class PipModeEntered : PipAction()
    class PipModeExited : PipAction()
}
