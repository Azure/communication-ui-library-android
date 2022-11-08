// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.reducer

import com.azure.android.communication.ui.chat.error.ChatStateError
import com.azure.android.communication.ui.chat.error.ErrorCode
import com.azure.android.communication.ui.chat.models.ChatCompositeEventCode
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ErrorAction
import com.azure.android.communication.ui.chat.redux.action.ParticipantAction
import com.azure.android.communication.ui.chat.redux.state.ErrorState

internal interface ErrorReducer : Reducer<ErrorState>

internal class ErrorReducerImpl : ErrorReducer {
    override fun reduce(state: ErrorState, action: Action): ErrorState {
        when (action) {
            is ErrorAction.ChatStateErrorOccurred -> return state.copy(
                chatStateError = action.chatStateError
            )
            is ParticipantAction.LocalUserRemoved -> {
                return state.copy(
                    chatStateError = ChatStateError(
                        errorCode = ErrorCode.CHAT_LOCAL_PARTICIPANT_EVICTED,
                        chatCompositeEventCode = ChatCompositeEventCode.CHAT_EVICTED
                    )
                )
            }
        }
        return state
    }
}
