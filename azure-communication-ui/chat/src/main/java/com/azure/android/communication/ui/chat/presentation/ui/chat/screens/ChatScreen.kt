// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.AcsChatActionBarViewModel
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.ChatCompositeActionBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.ChatCompositeBottomBar
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.ChatCompositeMessageList
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.ChatScreenViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.toViewModelList
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType

@Composable
internal fun ChatScreen(viewModel: ChatScreenViewModel) {

    Scaffold(
        topBar = {
            val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            ChatCompositeActionBar(
                AcsChatActionBarViewModel(
                    participantCount = 4,
                    topic = stringResource(R.string.azure_communication_ui_chat_chat_action_bar_title)
                )
            ) {
                dispatcher?.onBackPressed()
            }
        },
        content = {
            ChatCompositeMessageList(
                modifier = Modifier.padding(it),
                messages = viewModel.messages
            )
        },
        bottomBar = { ChatCompositeBottomBar() }
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
                        id = null
                    ),

                    MessageInfoModel(
                        messageType = ChatMessageType.TEXT,
                        content = "Test Message 2 ",
                        internalId = null,
                        id = null
                    ),

                    MessageInfoModel(
                        messageType = ChatMessageType.TEXT,
                        content = "Test Message 3",
                        internalId = null,
                        id = null
                    ),

                ).toViewModelList(),
                state = "state",
                buildCount = 2,
                postMessage = {}
            )
        )
    }
}
