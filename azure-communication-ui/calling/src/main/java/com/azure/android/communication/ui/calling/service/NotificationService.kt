// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.presentation.CallCompositeActivity
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
    private val instanceId: Int,
) {

    private var inCallServiceConnection: InCallServiceConnection? = null

    private val callingStatus = MutableStateFlow(CallingStatus.NONE)

    fun start(lifecycleScope: LifecycleCoroutineScope, instanceId: Int) {
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
        inCallServiceIntent.putExtra(CallCompositeActivity.KEY_INSTANCE_ID, instanceId)
        val inCallServiceConnection = InCallServiceConnection()
        this.inCallServiceConnection = inCallServiceConnection
        context.applicationContext.bindService(inCallServiceIntent, inCallServiceConnection, Context.BIND_AUTO_CREATE)
    }

    fun removeNotification() {
        val inCallServiceIntent = Intent(context.applicationContext, InCallService::class.java)
        inCallServiceConnection?.let {
            context.applicationContext.unbindService(it)
        }
    }
}

class InCallServiceConnection: ServiceConnection {
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

    }

    override fun onServiceDisconnected(name: ComponentName?) {

    }

}