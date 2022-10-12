// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.ActionBarView
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.ActionBarViewModel
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.BottomBarView
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.MessageListView
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.TypingIndicatorView
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.ChatScreenViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.toViewModelList
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType

@Composable
internal fun ChatScreen(viewModel: ChatScreenViewModel) {

    Scaffold(
        topBar = {
            val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            ActionBarView(
                viewModel = ActionBarViewModel(
                    participantCount = 4,
                    topic = stringResource(R.string.azure_communication_ui_chat_chat_action_bar_title)
                ),
            ) {
                dispatcher?.onBackPressed()
            }
        },
        content = {
            if (viewModel.showError) {
                Column() {
                    BasicText("ERROR")
                    BasicText(viewModel.errorMessage)
                }
            } else if (viewModel.isLoading) {
                CircularProgressIndicator()
            } else {
                MessageListView(
                    modifier = Modifier.padding(it),
                    messages = viewModel.messages,
                    scrollState = LazyListState(),
                )
            }

            viewModel.remoteParticipants?.also { remoteParticipants ->
                TypingIndicatorView(participants = remoteParticipants)
            }
        },
        bottomBar = { BottomBarView(viewModel.postMessage) }
    )
}

@Preview
@Composable
internal fun ChatScreenPreview() {
    ChatCompositeTheme {
        ChatScreen(
            viewModel = ChatScreenViewModel(
                listOf(
                    MessageInfoModel(
                        messageType = ChatMessageType.TEXT,
                        content = "Test Message",
                        internalId = null,
                        id = null,
                        senderDisplayName = "John Doe"
                    ),

                    MessageInfoModel(
                        messageType = ChatMessageType.TEXT,
                        content = "Test Message 2 ",
                        internalId = null,
                        id = null,
                        senderDisplayName = "John Doe Junior"
                    ),

                    MessageInfoModel(
                        messageType = ChatMessageType.TEXT,
                        content = "Test Message 3",
                        internalId = null,
                        id = null,
                        senderDisplayName = "Elliott Red"
                    ),

                ).toViewModelList(),
                state = ChatStatus.INITIALIZED.name,
                buildCount = 2,
                postMessage = {},

                // error = ChatStateError(
                //    errorCode = ErrorCode.CHAT_JOIN_FAILED
                // )
            )
        )
    }
}
