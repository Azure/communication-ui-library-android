// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

import com.azure.android.communication.ui.calling.redux.state.ToastNotificationKind

internal sealed class ToastNotificationAction : Action {
    class ShowNotification(val kind: ToastNotificationKind) : ToastNotificationAction()
    class DismissNotification : ToastNotificationAction()
}
