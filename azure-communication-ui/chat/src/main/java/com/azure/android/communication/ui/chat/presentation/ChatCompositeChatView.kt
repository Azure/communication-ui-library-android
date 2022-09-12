// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import com.azure.android.communication.ui.chat.presentation.screens.ChattingScreen

class ChatCompositeChatView(context: Context): FrameLayout(context) {
    private val composeView = ComposeView(context)

    init {
        addView(composeView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        composeView.setContent {
            ChattingScreen()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}