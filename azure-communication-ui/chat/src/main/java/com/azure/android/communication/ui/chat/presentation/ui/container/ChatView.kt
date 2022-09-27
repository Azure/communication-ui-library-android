// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.container

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeUITheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.screens.ChatScreen

class ChatView(context: Context) : FrameLayout(context) {
    private val composeView = ComposeView(context)

    init {
        addView(composeView)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        composeView.setContent {
            ChatCompositeUITheme {
                ChatScreen()
            }
        }
    }
}
