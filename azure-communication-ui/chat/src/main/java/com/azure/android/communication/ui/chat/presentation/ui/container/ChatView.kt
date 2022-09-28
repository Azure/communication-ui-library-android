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
import com.azure.android.communication.ui.chat.presentation.ui.reduxviewmodelgenerator.ReduxViewModelGenerator
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.buildChatScreenViewModel

internal class ChatView(context: Context, private val instanceId: Int) : FrameLayout(context) {
    private val composeView = ComposeView(context)

    private val locator get() = ServiceLocator.getInstance(instanceId)

    init {
        addView(composeView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ReduxViewModelGenerator(
            builder = { store ->
                buildChatScreenViewModel(
                    store = store,
                    repository = locator.locate()
                )
            },
            onChanged = {
                composeView.setContent {
                    ChatCompositeUITheme {
                        ChatScreen(viewModel = it)
                    }
                }
            },
            coroutineScope = findViewTreeLifecycleOwner()!!.lifecycleScope,
            store = locator.locate()
        )
    }
}
