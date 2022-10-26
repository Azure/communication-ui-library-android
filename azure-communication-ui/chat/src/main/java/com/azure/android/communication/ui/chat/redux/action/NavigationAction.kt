// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.action

import com.azure.android.communication.ui.chat.models.MessageInfoModel

internal sealed class NavigationAction : Action {
    class GotoParticipants : NavigationAction()
    class Pop: NavigationAction()
}
