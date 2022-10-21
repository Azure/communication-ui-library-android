// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.container

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.chat.locator.ServiceLocator
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.screens.ChatScreen
import com.azure.android.communication.ui.chat.utilities.ReduxViewModelGenerator
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.ChatScreenViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.buildChatScreenViewModel
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.LifecycleAction
import com.azure.android.communication.ui.chat.redux.state.ReduxState

internal class ChatView(context: Context, private val instanceId: Int) : FrameLayout(context) {
    private val composeView = ComposeView(context)
    private lateinit var reduxViewModelGenerator: ReduxViewModelGenerator<ReduxState, ChatScreenViewModel>
    private val locator get() = ServiceLocator.getInstance(instanceId)
    private val dispatch: Dispatch by lazy { locator.locate() }

    init {
        addView(composeView)
        count = 0
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        count++
        if (count == 1) {
            dispatch(LifecycleAction.EnterForeground)
        }
        reduxViewModelGenerator = ReduxViewModelGenerator(
            builder = { store ->
                buildChatScreenViewModel(
                    store = store,
                    messages = locator.locate(),
                    localUserIdentifier = locator.locate<ChatCompositeRemoteOptions>().identity
                )
            },
            onChanged = {
                composeView.setContent {
                    ChatCompositeTheme {
                        ChatScreen(viewModel = it)
                    }
                }
            },
            coroutineScope = findViewTreeLifecycleOwner()!!.lifecycleScope,
            store = locator.locate()
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        count--
        reduxViewModelGenerator.stop()
        if (count == 0) {
            dispatch(LifecycleAction.EnterBackground)
        }
    }

    companion object {
        internal var count = 0
    }
}
