package com.azure.android.communication.ui.chat.implementation.ui.view

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import com.azure.android.communication.ui.arch.locator.ServiceLocator
import com.azure.android.communication.ui.arch.view_model.ReduxViewModel
import com.azure.android.communication.ui.chat.implementation.redux.actions.MockUIActions
import com.azure.android.communication.ui.chat.implementation.redux.states.MockUIChatState
import com.azure.android.communication.ui.chat.implementation.ui.screen.AcsChatScreen
import com.azure.android.communication.ui.chat.implementation.ui.view_models.MockUiViewModel

class MockStateComposeChatView(context: Context) : FrameLayout(context) {
    private val composeView = ComposeView(context)
    private val locator = ServiceLocator.getInstance(9999)
    private lateinit var mockUIReduxViewModel: ReduxViewModel<MockUiViewModel>

    init {
        addView(composeView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mockUIReduxViewModel = ReduxViewModel(builder = { store ->

            val mockUiState = store.getCurrentState().getSubState<MockUIChatState>()
            MockUiViewModel(
                mockMessages = mockUiState.mockMessages,
                mockParticipants = mockUiState.mockParticipants,
                postMessage = {
                    store.dispatch(MockUIActions.PostMessage(message = it, mockParticipant = mockUiState.activeMockParticipant))
                },
                onUserTyping = {}
            )
        }, onChanged = { viewModel ->
            composeView.setContent {
                AcsChatScreen(viewModel)
            }
        }, locator.locate())
            mockUIReduxViewModel.start()
        }

        override fun onDetachedFromWindow() {
            mockUIReduxViewModel.stop()
            super.onDetachedFromWindow()
        }
    }
    