package com.azure.android.communication.ui.chat.implementation.ui.view

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import com.azure.android.communication.ui.arch.locator.ServiceLocator
import com.azure.android.communication.ui.arch.view_model.ReduxViewModel
import com.azure.android.communication.ui.chat.implementation.redux.actions.UserActions
import com.azure.android.communication.ui.chat.implementation.redux.states.AcsChatState
import com.azure.android.communication.ui.chat.implementation.redux.states.MockMessage
import com.azure.android.communication.ui.chat.implementation.redux.states.MockParticipant
import com.azure.android.communication.ui.chat.implementation.ui.screen.AcsChatScreen
import com.azure.android.communication.ui.chat.implementation.ui.view_models.MockUiViewModel

class LiveStateComposeChatView(context: Context, instanceId: Int) : FrameLayout(context) {
    private val composeView = ComposeView(context)
    private lateinit var mockUIReduxViewModel: ReduxViewModel<MockUiViewModel>
    private val locator = ServiceLocator.getInstance(instanceId)

    init {
        addView(composeView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mockUIReduxViewModel = ReduxViewModel(
            builder = { store ->
                // Get Live State
                // val mockUiState = store.getCurrentState().getSubState<MockUIChatState>()
                val acsChatState = store.getCurrentState().getSubState<AcsChatState>()
                MockUiViewModel(
                    mockMessages = acsChatState.messages.map {
                        MockMessage(
                            mockParticipant = MockParticipant(it.senderDisplayName ?: "Unknown"),
                            message = it.content ?: "blank"
                        )
                    },
                    mockParticipants = acsChatState.participants.map {
                        MockParticipant(
                            it.displayName
                        )
                    },
                    postMessage = {
                        store.dispatch(UserActions.SendMessage(it))
                    },
                    onUserTyping = {
                        store.dispatch(UserActions.IsTyping())
                    }
                )
            }, onChanged = { viewModel ->
            composeView.setContent {
                AcsChatScreen(viewModel)
            }
        },
            genericStore = locator.locate()
        )
        mockUIReduxViewModel.start()
    }

    override fun onDetachedFromWindow() {
        mockUIReduxViewModel.stop()
        super.onDetachedFromWindow()
    }
}
