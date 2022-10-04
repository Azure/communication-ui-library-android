package com.azure.android.communication.ui.chat.implementation.redux.reducers

import com.azure.android.communication.ui.arch.redux.GenericState
import com.azure.android.communication.ui.arch.redux.Reducer
import com.azure.android.communication.ui.chat.implementation.redux.actions.MockUIActions
import com.azure.android.communication.ui.chat.implementation.redux.states.MockMessage
import com.azure.android.communication.ui.chat.implementation.redux.states.MockUIChatState

class MockReducer : Reducer<GenericState> {

    override fun reduce(state: GenericState, action: Any): GenericState {
        when (action) {
            is MockUIActions.PostMessage -> {
                return state.replace(
                    state.getSubState<MockUIChatState>().addMessage(
                        MockMessage(action.mockParticipant, action.message)
                    )
                )
            }

            is MockUIActions.AddParticipant -> {
                return state.replace(state.getSubState<MockUIChatState>().addParticipant(action.mockParticipant))
            }

            is MockUIActions.RemoveParticipant -> {
                return state.replace(state.getSubState<MockUIChatState>().removeParticipant(action.mockParticipant))
            }

            is MockUIActions.StartParticipantTyping -> {
                return state.replace(state.getSubState<MockUIChatState>().startParticipantTyping(action.mockParticipant))
            }

            is MockUIActions.StopParticipantTyping -> {
                return state.replace(state.getSubState<MockUIChatState>().stopParticipantTyping(action.mockParticipant))
            }
        }
        return state
    }
}
