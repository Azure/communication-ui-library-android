// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <RTT_POC>
package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.RttAction
import com.azure.android.communication.ui.calling.redux.state.RttMessage
import com.azure.android.communication.ui.calling.redux.state.RttState

internal interface RttReducer : Reducer<RttState>

internal class RttReducerImpl : RttReducer {
    override fun reduce(state: RttState, action: Action): RttState {

        return when (action) {
            is RttAction.SendRtt -> {
                // Do nothing? I think middleware should handle this
                return state
            }
            is RttAction.IncomingMessageReceived -> {
                val text = action.rttContent
                val participantId = action.participantId
                val lastParticipantMessage = state.messages.findLast {
                    it.participantID == participantId && !it.isFinalized
                }

                if (lastParticipantMessage != null) {
                    // Update the current message with new characters
                    val updatedMessages = state.messages.toMutableList()
                    updatedMessages.replaceAll {
                        if (it == lastParticipantMessage) {
                            lastParticipantMessage.copy(
                                message = lastParticipantMessage.message + text
                            )
                        } else {
                            it
                        }
                    }
                    return state.copy(messages = updatedMessages, isRttActive = true)
                } else {
                    var updatedMessages = state.messages + RttMessage(text, participantId)
                    if (updatedMessages.size > 5) {
                        updatedMessages = updatedMessages.subList(1, updatedMessages.size)
                    }
                    return state.copy(messages = updatedMessages)
                }
            }
            is RttAction.DisableRttLocally -> {
                state.copy(isRttActive = false)
            }
            else -> state
        }
    }
}
</RTT_POC> */
