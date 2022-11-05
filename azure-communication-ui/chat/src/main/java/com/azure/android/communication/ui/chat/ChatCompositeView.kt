// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.screens.NavigatableBaseScreen
import com.azure.android.communication.ui.chat.utilities.ReduxViewModelGenerator
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.ChatScreenViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.buildChatScreenViewModel
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.LifecycleAction
import com.azure.android.communication.ui.chat.redux.state.ReduxState

class ChatCompositeView(context: Context) : FrameLayout(context) {
    private val composeView = ComposeView(context)
    private lateinit var reduxViewModelGenerator: ReduxViewModelGenerator<ReduxState, ChatScreenViewModel>
    private var chatThreadManager : ChatThreadManager? = null
    private val dispatch : Dispatch get() = chatThreadManager?.container?.dispatch!!

    init {
        addView(composeView)
        // TODO: We need to re-assess this state
        count = 0
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    private fun stop() {
        if (chatThreadManager == null) return
        count--
        reduxViewModelGenerator.stop()
        if (count == 0) {
            dispatch(LifecycleAction.EnterBackground)
        }
    }

    private fun start() {
        if (chatThreadManager == null) return


        count++
        if (count == 1) {
            dispatch(LifecycleAction.EnterForeground)
        }
        reduxViewModelGenerator = ReduxViewModelGenerator(
            builder = { store ->
                buildChatScreenViewModel(
                    context = context,
                    store = store,
                    messages = chatThreadManager?.container?.messageRepository!!,
                    localUserIdentifier = chatThreadManager?.container?.remoteOptions?.identity!!,
                    dispatch = chatThreadManager?.container?.dispatch!!
                )
            },
            onChanged = {
                composeView.setContent {
                    ChatCompositeTheme {
                        NavigatableBaseScreen(viewModel = it)
                    }
                }
            },
            coroutineScope = findViewTreeLifecycleOwner()!!.lifecycleScope,
            store = chatThreadManager?.container?.appStore!!
        )
    }

    fun setChatThreadManager(chatThreadManager: ChatThreadManager) {
        if (this.chatThreadManager != null) {
            this.chatThreadManager?.stop()
            this.chatThreadManager = null
        }
        this.chatThreadManager = chatThreadManager
        if (isAttachedToWindow) {
            start()
        }
    }

    companion object {
        internal var count = 0
    }
}
