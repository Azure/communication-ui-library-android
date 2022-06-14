// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.reduxkotlin.Store

internal class NotificationService(
    private val context: Context,
    private val store: Store<ReduxState>,
) {

    private var callingStatus = MutableStateFlow(CallingStatus.NONE)

    fun start(lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch {
            store.subscribe {
                onStateChanged()
            }
            onStateChanged()
        }
        lifecycleScope.launch {
            callingStatus.collect {
                if (it == CallingStatus.NONE || it == CallingStatus.DISCONNECTED)
                    removeNotification()
                else
                    displayNotification()
            }
        }
    }

    private fun onStateChanged() {
        callingStatus.value = store.state.callState.callingStatus
    }

    private fun displayNotification() {
        val inCallServiceIntent = Intent(context, InCallService::class.java)
        context.startService(inCallServiceIntent)
    }

    fun removeNotification() {
        val inCallServiceIntent = Intent(context, InCallService::class.java)
        context.stopService(inCallServiceIntent)
    }
}
