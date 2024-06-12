// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.ToastNotificationAction
import com.azure.android.communication.ui.calling.redux.state.ToastNotificationState

internal interface ToastNotificationReducer : Reducer<ToastNotificationState>

internal class ToastNotificationReducerImpl : ToastNotificationReducer {
    override fun reduce(state: ToastNotificationState, action: Action): ToastNotificationState {
        return when (action) {
            is ToastNotificationAction.ShowNotification -> {
                state.copy(kind = action.kind)
            }
            is ToastNotificationAction.DismissNotification -> {
                state.copy(kind = null)
            }
            else -> state
        }
    }
}
