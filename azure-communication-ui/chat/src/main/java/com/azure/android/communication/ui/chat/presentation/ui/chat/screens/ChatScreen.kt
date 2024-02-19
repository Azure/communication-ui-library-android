// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Card
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageContextMenuModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.ChatScreenStateViewModel
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.ActionBarView
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.BottomBarView
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.FluentCircularIndicator
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.MessageListView
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.TypingIndicatorView
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.UnreadMessagesIndicatorView
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.ChatScreenViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.toViewModelList
import com.azure.android.communication.ui.chat.preview.MOCK_LOCAL_USER_ID
import com.azure.android.communication.ui.chat.preview.MOCK_MESSAGES
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.action.NavigationAction
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import com.jakewharton.threetenabp.AndroidThreeTen
import com.microsoft.fluentui.theme.ThemeMode
import kotlinx.coroutines.launch

@Composable
internal fun ChatScreen(
    viewModel: ChatScreenViewModel,
    stateViewModel: ChatScreenStateViewModel = viewModel(),
    showActionBar: Boolean = false,
) {
    val scaffoldState = rememberScaffoldState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        backgroundColor = ChatCompositeTheme.colors.background,
        scaffoldState = scaffoldState,
        topBar = {
            if (!showActionBar) return@Scaffold
            val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            val topic =
                when {
                    viewModel.chatTopic != null -> viewModel.chatTopic
                    else -> stringResource(R.string.azure_communication_ui_chat_chat_action_bar_title)
                }
            val subTitle =
                stringResource(
                    id = R.string.azure_communication_ui_chat_count_people,
                    viewModel.participants.count(),
                )

            ActionBarView(
                title = topic,
                subTitle = subTitle,
                onTitleClicked = {
                    viewModel.postAction(NavigationAction.GotoParticipants())
                },
                onBackButtonPressed = {
                    dispatcher?.onBackPressed()
                },
                postAction = viewModel.postAction,
            )
        },
        content = { paddingValues ->
            if (viewModel.showError) {
                Column {
                    BasicText("ERROR", style = LocalTextStyle.current.copy(color = ChatCompositeTheme.colors.textColor))
                    BasicText(viewModel.errorMessage, style = LocalTextStyle.current.copy(color = ChatCompositeTheme.colors.textColor))
                }
            } else if (viewModel.isLoading) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    FluentCircularIndicator()
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    MessageListView(
                        modifier =
                            Modifier
                                .padding(paddingValues)
                                .width(ChatCompositeTheme.dimensions.messageListMaxWidth),
                        messages = viewModel.messages,
                        scrollState = listState,
                        showLoading = viewModel.areMessagesLoading,
                        dispatchers = viewModel.postAction,
                    )

                    Box(
                        modifier =
                            Modifier
                                .width(ChatCompositeTheme.dimensions.messageListMaxWidth)
                                .padding(paddingValues)
                                .padding(ChatCompositeTheme.dimensions.unreadMessagesIndicatorPadding),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        UnreadMessagesIndicatorView(
                            scrollState = listState,
                            visible =
                                viewModel.unreadMessagesIndicatorVisibility &&
                                    listState.firstVisibleItemIndex > 1 &&
                                    !(viewModel.messages.lastOrNull()?.isLocalUser ?: true),
                            unreadCount = viewModel.unreadMessagesCount,
                        )
                    }
                }
            }
            if (viewModel.debugOverlayText.isNotEmpty()) {
                Card {
                    BasicText(viewModel.debugOverlayText)
                }
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(Modifier.width(ChatCompositeTheme.dimensions.messageListMaxWidth)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier =
                                Modifier
                                    .align(alignment = Alignment.Start)
                                    .padding(horizontal = 5.dp),
                        ) {
                            TypingIndicatorView(viewModel.typingParticipants.toList())
                        }
                        BottomBarView(
                            messageInputTextState = stateViewModel.messageInputTextState,
                            postAction = {
                                if (it is ChatAction.SendMessage) {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                }
                                viewModel.postAction(it)
                            },
                            sendMessageEnabled = viewModel.sendMessageEnabled,
                        )
                    }
                }
            }
        },
    )

    /* TODO: Add this Composable back in to support Context Menu (Copy)
    messageContextMenu(
        menu = viewModel.messageContextMenu,
        dispatch = viewModel.postAction,)
     */
}

@Preview
@Composable
internal fun ChatScreenPreview() {
    AndroidThreeTen.init(LocalContext.current)
    ChatCompositeTheme(themeMode = ThemeMode.Dark) {
        ChatScreen(
            viewModel =
                ChatScreenViewModel(
                    messages =
                        MOCK_MESSAGES.toViewModelList(
                            context = LocalContext.current,
                            localUserIdentifier = MOCK_LOCAL_USER_ID,
                            hiddenParticipant = mutableSetOf(),
                        ),
                    chatStatus = ChatStatus.INITIALIZED,
                    buildCount = 2,
                    areMessagesLoading = true,
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
