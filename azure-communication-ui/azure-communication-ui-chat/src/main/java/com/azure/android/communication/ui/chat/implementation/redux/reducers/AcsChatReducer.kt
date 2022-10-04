package com.azure.android.communication.ui.chat.implementation.redux.reducers

import android.util.Log
import com.azure.android.communication.ui.arch.redux.GenericState
import com.azure.android.communication.ui.arch.redux.Reducer
import com.azure.android.communication.ui.chat.implementation.redux.states.AcsChatState
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageReceivedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageWrapperNewMessageEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatParticipant
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsRetrievedEvent

class AcsChatReducer : Reducer<GenericState> {

    override fun reduce(state: GenericState, action: Any): GenericState {
        Log.i("Reducing:", action.toString())
        when (action) {
            is ParticipantsRetrievedEvent -> return setParticipants(state, action.participants)
            is ChatMessageReceivedEvent -> return addMessage(state, action)
        }
        return state
    }
}

internal fun addMessage(genericState: GenericState, action: ChatMessageReceivedEvent): GenericState {
    val state = genericState.getSubState<AcsChatState>()
    return genericState.replace(
        state.copy(
            messages = state.messages.plus(
                ChatMessageWrapperNewMessageEvent(action)
            )
        )
    )
}
internal fun setParticipants(genericState: GenericState, participants: List<ChatParticipant>): GenericState {
    val state = genericState.getSubState<AcsChatState>()
    return genericState.replace(state.copy(participants = participants))
}
