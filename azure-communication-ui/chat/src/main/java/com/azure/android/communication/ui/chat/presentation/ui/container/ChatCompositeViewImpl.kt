// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.container

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.chat.ChatAdapter
import com.azure.android.communication.ui.chat.instanceIdAccessor
import com.azure.android.communication.ui.chat.locator.ServiceLocator
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.screens.NavigatableBaseScreen
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.ChatScreenViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.buildChatScreenViewModel
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.LifecycleAction
import com.azure.android.communication.ui.chat.redux.action.NavigationAction
import com.azure.android.communication.ui.chat.redux.state.AppReduxState
import com.azure.android.communication.ui.chat.redux.state.NavigationStatus
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.repository.MessageRepository
import com.azure.android.communication.ui.chat.utilities.ReduxViewModelGenerator

internal class ChatCompositeViewImpl(
    context: Context,
    private val chatAdapter: ChatAdapter,
    private val showActionBar: Boolean = false,
) : FrameLayout(context) {
    private val composeView = ComposeView(context)
    private val locator get() = ServiceLocator.getInstance(chatAdapter.instanceIdAccessor())
    private val dispatch: Dispatch by lazy { locator.locate() }
    private lateinit var reduxViewModelGenerator: ReduxViewModelGenerator<ReduxState, ChatScreenViewModel>

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
        reduxViewModelGenerator =
            ReduxViewModelGenerator(
                builder = { store ->
                    buildChatScreenViewModel(
                        context = context,
                        store = store,
                        messages = locator.locate<MessageRepository>().snapshotList,
                        localUserIdentifier = locator.locate<ChatCompositeRemoteOptions>().identity,
                        dispatch = locator.locate(),
                    )
                },
                onChanged = {
                    composeView.setContent {
                        ChatCompositeTheme {
                            NavigatableBaseScreen(viewModel = it, showActionBar = showActionBar)
                        }
                    }
                },
                coroutineScope = findViewTreeLifecycleOwner()!!.lifecycleScope,
                store = locator.locate(),
            )
    }

    override fun onDetachedFromWindow() {
        tryPop()
        super.onDetachedFromWindow()
        count--
        reduxViewModelGenerator.stop()
        if (count == 0) {
            dispatch(LifecycleAction.EnterBackground)
        }
    }

    private fun tryPop(): Boolean {
        val store = locator.locate<AppStore<AppReduxState>>()
        val state = store.getCurrentState()
        val canPop = state.navigationState.navigationStatus != NavigationStatus.NONE
        if (canPop) {
            store.dispatch(NavigationAction.Pop())
            return true
        }
        return false
    }

    companion object {
        internal var count = 0
    }
}
