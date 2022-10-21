// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.ChatScreenStateViewModel
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.ActionBarView
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.BottomBarView
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.MessageListView
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.TypingIndicatorView
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.UnreadMessagesIndicatorView
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.ChatScreenViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.toViewModelList
import com.azure.android.communication.ui.chat.preview.MOCK_LOCAL_USER_ID
import com.azure.android.communication.ui.chat.preview.MOCK_MESSAGES
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import com.azure.android.communication.ui.chat.utilities.outOfViewItemCount

@Composable
internal fun ChatScreen(
    viewModel: ChatScreenViewModel,
    stateViewModel: ChatScreenStateViewModel = viewModel(),
) {
    val scaffoldState = rememberScaffoldState()
    val listState = rememberLazyListState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            ActionBarView(
                participantCount = viewModel.participants.count(),
                topic = viewModel.chatTopic ?: stringResource(R.string.azure_communication_ui_chat_chat_action_bar_title)
            ) {
                dispatcher?.onBackPressed()
            }
        },
        content = { paddingValues ->
            if (viewModel.showError) {
                Column {
                    BasicText("ERROR")
                    BasicText(viewModel.errorMessage)
                }
            } else if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                MessageListView(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxWidth(),
                    messages = viewModel.messages,
                    scrollState = listState,
                )
            }
        },
        bottomBar = {
            Column {
                UnreadMessagesIndicatorView(
                    scrollState = listState,
                    visible = listState.outOfViewItemCount() > 0,
                    unreadCount = 0,
                    totalMessages = viewModel.messages.size/* TODO ViewModelLogic */
                )

                Box(modifier = Modifier.fillMaxWidth().height(ChatCompositeTheme.dimensions.typingIndicatorAreaHeight), contentAlignment = Alignment.CenterStart) {
                    TypingIndicatorView(viewModel.typingParticipants.toList())
                }

                BottomBarView(
                    messageInputTextState = stateViewModel.messageInputTextState,
                    chatStatus = viewModel.chatStatus,
                    postAction = viewModel.postAction
                )
            }
        }
    )
}

@Preview
@Composable
internal fun ChatScreenPreview() {
    ChatCompositeTheme {
        ChatScreen(
            viewModel = ChatScreenViewModel(
                messages = MOCK_MESSAGES.toViewModelList(MOCK_LOCAL_USER_ID),
                chatStatus = ChatStatus.INITIALIZED,
                buildCount = 2,
                typingParticipants = setOf("John Doe", "Mary Sue"),
                postAction = {},
                participants = listOf(
                    RemoteParticipantInfoModel(
                        CommunicationIdentifier.UnknownIdentifier("7A13DD2C-B49F-4521-9364-975F12F6E333"),
                        "John Smith"
                    ),
                    RemoteParticipantInfoModel(
                        CommunicationIdentifier.UnknownIdentifier("931804B1-D72E-4E70-BFEA-7813C7761BD2"),
                        "William Brown"
                    ),
                    RemoteParticipantInfoModel(
                        CommunicationIdentifier.UnknownIdentifier("152D5D76-3DDC-44BE-873F-A4575F8C91DF"),
                        "James Miller"
                    ),
                    RemoteParticipantInfoModel(
                        CommunicationIdentifier.UnknownIdentifier("85FF2697-2ABB-480E-ACCA-09EBE3D6F5EC"),
                        "George Johnson"
                    ),
                    RemoteParticipantInfoModel(
                        CommunicationIdentifier.UnknownIdentifier("DB75F1F0-65E4-46B0-A213-DA4F574659A5"),
                        "Henry Jones"
                    ),
                ).associateBy({ it.userIdentifier.id })

                // error = ChatStateError(
                //    errorCode = ErrorCode.CHAT_JOIN_FAILED
                // )
            ),

        )
    }
}
