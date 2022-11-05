// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.azure.android.communication.ui.chat.locator.ServiceLocator
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.ChatScreenStateViewModel
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.ParticipantsListView
import com.azure.android.communication.ui.chat.presentation.ui.chat.screens.NavigatableBaseScreen
import com.azure.android.communication.ui.chat.presentation.ui.chat.screens.ParticipantScreen
import com.azure.android.communication.ui.chat.presentation.ui.chat.screens.chatScaffoldBody
import com.azure.android.communication.ui.chat.presentation.ui.chat.screens.chatScreenBottomBar
import com.azure.android.communication.ui.chat.utilities.ReduxViewModelGenerator
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.ChatScreenViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.buildChatScreenViewModel
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.LifecycleAction
import com.azure.android.communication.ui.chat.redux.state.ReduxState

internal enum class ChatComponent {
    ChatBody,
    Participants,
}

class ChatThreadView(context: Context) : ChatComponentView(context) {
    override val component = ChatComponent.ChatBody
}

class ChatParticipantView(context: Context) : ChatComponentView(context) {
    override val component = ChatComponent.Participants
}

abstract class ChatComponentView(context: Context) : FrameLayout(context) {
    private val composeView = ComposeView(context)
    private lateinit var reduxViewModelGenerator: ReduxViewModelGenerator<ReduxState, ChatScreenViewModel>
    private var chatThreadManager: ChatThreadManager? = null
    private val dispatch: Dispatch get() = chatThreadManager?.container?.locator?.locate()!!
    private val locator: ServiceLocator get() = chatThreadManager?.container?.locator!!

    internal abstract val component: ChatComponent

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
                    messages = locator.locate(),
                    localUserIdentifier = locator.locate<ChatCompositeRemoteOptions>().identity,
                    dispatch = locator.locate()
                )
            },
            onChanged = {
                composeView.setContent {
                    val stateViewModel = remember {
                        ChatScreenStateViewModel()
                    }
                    ChatCompositeTheme {
                        val vm = it
                        when (component) {
                            ChatComponent.ChatBody ->
                                Scaffold(
                                    content = {
                                        chatScaffoldBody(
                                            viewModel = vm,
                                            paddingValues = it
                                        )
                                    },
                                    bottomBar = {
                                        chatScreenBottomBar(
                                            viewModel = vm,
                                            stateViewModel = stateViewModel
                                        )
                                    },
                                )
                            ChatComponent.Participants ->
                                Scaffold(content = {
                                    ParticipantsListView(
                                        participants = vm.participants.values.toList(),
                                        modifier = Modifier.padding(it)
                                    )
                                })

                        }
                    }
                }
            },
            coroutineScope = findViewTreeLifecycleOwner()!!.lifecycleScope,
            store = locator.locate()
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
