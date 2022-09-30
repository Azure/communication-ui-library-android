// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeUITheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.chatviewcomponents.AcsChatActionBarViewModel
import com.azure.android.communication.ui.chat.presentation.ui.chat.chatviewcomponents.ChatCompositeActionBar

@Composable
fun ChattingScreen() {
    ChatCompositeUITheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
                ChatCompositeActionBar(
                    AcsChatActionBarViewModel(
                        4,
                        stringResource(R.string.azure_communication_ui_chat_chat_action_bar_title)
                    )
                ) {
                    dispatcher?.onBackPressed()
                }
            }
        }
    }
}
