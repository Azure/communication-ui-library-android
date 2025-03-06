// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageContextMenuModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.ChatScreenStateViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.ChatScreenViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.toViewModelList
import com.azure.android.communication.ui.chat.preview.MOCK_LOCAL_USER_ID
import com.azure.android.communication.ui.chat.preview.MOCK_MESSAGES
import com.azure.android.communication.ui.chat.redux.action.NavigationAction
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.redux.state.NavigationStatus
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import com.jakewharton.threetenabp.AndroidThreeTen

// Handles navigation between screens
// i.e. show participants and chatscreen in a stack
@Composable
internal fun NavigatableBaseScreen(
    viewModel: ChatScreenViewModel,
    stateViewModel: ChatScreenStateViewModel = viewModel(),
    showActionBar: Boolean = false,
    inputAutoFocus: Boolean = false,
) {
    BackHandler(enabled = viewModel.navigationStatus != NavigationStatus.NONE, onBack = {
        viewModel.postAction(NavigationAction.Pop())
    })
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        ChatScreen(
            viewModel = viewModel,
            stateViewModel = stateViewModel,
            showActionBar = showActionBar,
            inputAutoFocus = inputAutoFocus,
        )

        AnimatedVisibility(
            visible = viewModel.navigationStatus == NavigationStatus.PARTICIPANTS,
            enter = slideInHorizontally(animationSpec = tween(durationMillis = 200)) { fullWidth -> fullWidth / 3 } +
                fadeIn(animationSpec = tween(durationMillis = 200)),
            exit = slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessHigh)) { 200 } +
                fadeOut(animationSpec = tween(durationMillis = 200))
        ) {
            ParticipantScreen(viewModel = viewModel)
        }
    }
}

@Preview
@Composable
internal fun NavigatableBaseScreenPreview() {
    AndroidThreeTen.init(LocalContext.current)
    ChatCompositeTheme {
        NavigatableBaseScreen(
            viewModel = ChatScreenViewModel(
                messages = MOCK_MESSAGES.toViewModelList(
                    context = LocalContext.current,
                    localUserIdentifier = MOCK_LOCAL_USER_ID,
                    hiddenParticipant = mutableSetOf()
                ),
                areMessagesLoading = false,
                chatStatus = ChatStatus.INITIALIZED,
                buildCount = 2,
                // Uncomment to verify nav
                navigationStatus = NavigationStatus.PARTICIPANTS,
                typingParticipants = listOf("John Doe", "Mary Sue"),
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
                ).associateBy { it.userIdentifier.id },
                messageContextMenu = MessageContextMenuModel(
                    messageInfoModel = EMPTY_MESSAGE_INFO_MODEL,
                    menuItems = emptyList()
                ),

                // error = ChatStateError(
                //    errorCode = ErrorCode.CHAT_JOIN_FAILED
                // )
            ),

        )
    }
}
