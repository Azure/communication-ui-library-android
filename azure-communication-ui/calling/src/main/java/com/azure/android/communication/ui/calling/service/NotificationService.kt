// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class NotificationService(
    private val context: Context,
    private val store: Store<ReduxState>,
    private val configuration: CallCompositeConfiguration,
) {

    private val callingStatus = MutableStateFlow(CallingStatus.NONE)

    fun start(lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch {
            store.getStateFlow().collect {
                callingStatus.value = it.callState.callingStatus
            }
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

    private fun displayNotification() {
        val inCallServiceIntent = Intent(context.applicationContext, InCallService::class.java)
        inCallServiceIntent.putExtra("enableMultitasking", configuration.enableMultitasking)
        inCallServiceIntent.putExtra("enableSystemPiPWhenMultitasking", configuration.enableSystemPiPWhenMultitasking)
        context.applicationContext.startService(inCallServiceIntent)
    }

    fun removeNotification() {
        val inCallServiceIntent = Intent(context.applicationContext, InCallService::class.java)
        context.applicationContext.stopService(inCallServiceIntent)
    }
}
