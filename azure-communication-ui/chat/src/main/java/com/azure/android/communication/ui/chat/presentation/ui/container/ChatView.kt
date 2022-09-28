// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.container

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.chat.locator.ServiceLocator
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeUITheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.screens.ChatScreen
import com.azure.android.communication.ui.chat.presentation.ui.redux_view_model.ReduxViewModel
import com.azure.android.communication.ui.chat.presentation.ui.view_model.ChatScreenViewModel
import com.azure.android.communication.ui.chat.presentation.ui.view_model.buildChatScreenViewModel
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.state.ReduxState

internal class ChatView(context: Context, instanceId : Int) : FrameLayout(context) {
    private val composeView = ComposeView(context)

    private val reduxViewModel by lazy {
        ReduxViewModel(
            builder = { store -> buildChatScreenViewModel(store, emptyList()) },
            onChanged = {
                composeView.setContent {
                    ChatCompositeUITheme {
                        ChatScreen(viewModel = it)
                    }
                }
            },
            coroutineScope = findViewTreeLifecycleOwner()!!.lifecycleScope,
            store = ServiceLocator.getInstance(instanceId).locate())
    }

    init {
        addView(composeView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        reduxViewModel.start()
    }
}
