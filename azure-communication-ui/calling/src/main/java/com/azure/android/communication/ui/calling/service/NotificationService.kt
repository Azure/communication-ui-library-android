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
import com.azure.android.communication.ui.calling.redux.state.CallStatus
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

    private val callStatus = MutableStateFlow(CallStatus.NONE)

    fun start(lifecycleScope: LifecycleCoroutineScope, instanceId: Int) {
        lifecycleScope.launch {
            store.getStateFlow().collect {
                callStatus.value = it.callState.callStatus
            }
        }
        lifecycleScope.launch {
            callStatus.collect {
                if (it == CallStatus.NONE || it == CallStatus.DISCONNECTED)
                    removeNotification()
                else
                    displayNotification()
            }
        }
    }

    private fun displayNotification() {
        if (inCallServiceConnection != null)
            return

        val inCallServiceIntent = Intent(context.applicationContext, InCallService::class.java)
        inCallServiceIntent.putExtra("enableMultitasking", configuration.enableMultitasking)
        inCallServiceIntent.putExtra("enableSystemPiPWhenMultitasking", configuration.enableSystemPiPWhenMultitasking)
        inCallServiceIntent.putExtra(CallCompositeActivity.KEY_INSTANCE_ID, instanceId)
        val inCallServiceConnection = InCallServiceConnection()
        this.inCallServiceConnection = inCallServiceConnection
        println("InCallService bindService")
        context.bindService(inCallServiceIntent, inCallServiceConnection, Context.BIND_AUTO_CREATE)
    }

    fun removeNotification() {
        println("InCallService removeNotification")

        inCallServiceConnection?.let {
            it.destroyNotification()
            context.unbindService(it)
        }
        inCallServiceConnection = null
    }
}

internal class InCallServiceConnection : ServiceConnection {
    internal var inCallServiceBinding: InCallServiceBinder? = null
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        inCallServiceBinding = service as InCallServiceBinder
        println("InCallService InCallServiceConnection.onServiceConnected")
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        inCallServiceBinding = null
    }

    fun destroyNotification() {
        inCallServiceBinding?.getService()?.destroyNotification()
    }
}
