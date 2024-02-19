// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.action

import org.threeten.bp.OffsetDateTime

internal sealed class NetworkAction : Action {
    class Connected : NetworkAction()

    class Disconnected : NetworkAction()

    class SetDisconnectedOffset(val disconnectOffsetDateTime: OffsetDateTime) : NetworkAction()
}
