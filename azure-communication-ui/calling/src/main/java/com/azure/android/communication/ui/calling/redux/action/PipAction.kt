// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

// TODO: rename to VisibilityAction
internal sealed class PipAction : Action {
    class HideRequested : PipAction()

    class HideEntered : PipAction()

    class PipModeEntered : PipAction()
    class ShowNormalEntered : PipAction()
}
