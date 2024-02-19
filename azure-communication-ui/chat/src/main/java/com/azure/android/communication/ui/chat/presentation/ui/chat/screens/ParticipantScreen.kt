// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageContextMenuModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.ActionBarView
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.ParticipantsListView
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.ChatScreenViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.toViewModelList
import com.azure.android.communication.ui.chat.preview.MOCK_LOCAL_USER_ID
import com.azure.android.communication.ui.chat.preview.MOCK_MESSAGES
import com.azure.android.communication.ui.chat.redux.action.NavigationAction
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import com.jakewharton.threetenabp.AndroidThreeTen

@Composable
internal fun ParticipantScreen(viewModel: ChatScreenViewModel) {
    val scaffoldState = rememberScaffoldState()
    val listState = rememberLazyListState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            val topic = stringResource(id = R.string.azure_communication_ui_chat_people)
            val subTitle =
                viewModel.chatTopic
                    ?: stringResource(R.string.azure_communication_ui_chat_chat_action_bar_title)

            ActionBarView(
                title = topic,
                subTitle = subTitle,
                onBackButtonPressed = {
                    viewModel.postAction(NavigationAction.Pop())
                },
                postAction = viewModel.postAction,
                onTitleClicked = null,
            )
        },
        content = { paddingValues ->
            ParticipantsListView(
                participants = viewModel.participants.values.toList(),
                modifier = Modifier.padding(paddingValues),
            )
        },
    )
}

@Preview
@Composable
internal fun ParticipantScreenPreview() {
    AndroidThreeTen.init(LocalContext.current)
    ChatCompositeTheme {
        ParticipantScreen(
            viewModel =
                ChatScreenViewModel(
                    messages =
                        MOCK_MESSAGES.toViewModelList(
                            context = LocalContext.current,
                            localUserIdentifier = MOCK_LOCAL_USER_ID,
                            hiddenParticipant = mutableSetOf(),
                        ),
                    areMessagesLoading = false,
                    chatStatus = ChatStatus.INITIALIZED,
                    buildCount = 2,
                    typingParticipants = listOf("John Doe", "Mary Sue"),
                    postAction = {},
                    participants =
                        listOf(
                            RemoteParticipantInfoModel(
                                CommunicationIdentifier.UnknownIdentifier("7A13DD2C-B49F-4521-9364-975F12F6E333"),
                                "John Smith",
                            ),
                            RemoteParticipantInfoModel(
                                CommunicationIdentifier.UnknownIdentifier("931804B1-D72E-4E70-BFEA-7813C7761BD2"),
                                "William Brown",
                            ),
                            RemoteParticipantInfoModel(
                                CommunicationIdentifier.UnknownIdentifier("152D5D76-3DDC-44BE-873F-A4575F8C91DF"),
                                "James Miller",
                            ),
                            RemoteParticipantInfoModel(
                                CommunicationIdentifier.UnknownIdentifier("85FF2697-2ABB-480E-ACCA-09EBE3D6F5EC"),
                                "George Johnson",
                            ),
                            RemoteParticipantInfoModel(
                                CommunicationIdentifier.UnknownIdentifier("DB75F1F0-65E4-46B0-A213-DA4F574659A5"),
                                "Henry Jones",
                            ),
                        ).associateBy { it.userIdentifier.id },
                    messageContextMenu =
                        MessageContextMenuModel(
                            messageInfoModel = EMPTY_MESSAGE_INFO_MODEL,
                            menuItems = emptyList(),
                        ),
                ),
        )
    }
}
