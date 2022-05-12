package com.azure.android.communication.ui.calling.presentation.manager

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi

import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.flow.collect
import java.util.concurrent.Executor


// Hook/Trigger to manage listening to the service
// The way this works
// - When a call is connected, and the permissions granted and flag enabled, it will start listening
// - When a call is disconnected and it's listening, it will stop
// - While listening, if CallStatus == OFF_HOOK, we request to end the call
/*
internal class DetectPhoneCallTrigger : ReduxTrigger() {
    private var receiver : DetectPhoneCall? = null
    private val started
        get() = receiver != null

    override fun shouldTrigger(lastState: ReduxState, newState: ReduxState): Boolean {
        if (!FeatureFlags.EndCallOnOffHook.active) return false

        return if (!started) {
            // Turn on when CallingStatus == Connected and has permissions
            (!started && newState.permissionState.phonePermissionState == PermissionStatus.GRANTED && newState.callState.callingStatus == CallingStatus.CONNECTED)
        } else {
            // Turn off when CallingStatus != CONNECTED (if it has been started)
            (started && newState.callState.callingStatus != CallingStatus.CONNECTED)
        }
    }

    override fun action(context: Context, store: Store<ReduxState>) {
        receiver = when (started) {
            // Turn On
            false -> DetectPhoneCall.register(context, store)
            // Turn Off
            true -> {
                receiver?.unregister()
                null
            }
        }
    }
}*/

internal class DetectPhoneCallManager(val context: Context, val store : Store<ReduxState>) {
    private val detectPhoneCall = DetectPhoneCall.register(context, store)

    suspend fun onCreate() {
        store.getStateFlow().collect {
            if (it.permissionState.phonePermissionState == PermissionStatus.GRANTED) {
                detectPhoneCall.register()
            }
        }
    }

    fun onDestroy() = detectPhoneCall.unregister()

}

/// Common Interface for the API >= 31 and < 31 mechanisms to do this
private interface DetectPhoneCall {
    companion object {
        fun register(context: Context, store: Store<ReduxState>): DetectPhoneCall {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var receiver : DetectPhoneCall = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                DetectPhoneCallV31(tm, store)
            } else {
                DetectPhoneCallLegacy(tm, store)
            }
            receiver.register()
            return receiver
        }
    }

    val store: Store<ReduxState>
    fun register()
    fun unregister()
    fun onOffHook() {
        if (store.getCurrentState().callState.callingStatus == CallingStatus.CONNECTED) {
            store.dispatch(CallingAction.CallEndRequested())
        }
    }
}

/// Legacy Version for < 31
private class DetectPhoneCallLegacy(private val telephonyManager: TelephonyManager, override val store:Store<ReduxState>) :
    PhoneStateListener(), DetectPhoneCall {

    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        super.onCallStateChanged(state, phoneNumber)
        if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
            onOffHook()
        }
    }

    override fun register() {
        telephonyManager.listen(this, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun unregister() {
        telephonyManager.listen(this, PhoneStateListener.LISTEN_NONE)
    }
}

// New version for >=31
@RequiresApi(Build.VERSION_CODES.S)
private class DetectPhoneCallV31(val telephonyManager: TelephonyManager, override val store:Store<ReduxState>) :
    TelephonyCallback(), TelephonyCallback.CallStateListener, DetectPhoneCall {
    override fun onCallStateChanged(state: Int) {
        if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
            onOffHook()
        }
    }

    override fun register() {
        telephonyManager.registerTelephonyCallback(
            object : Executor {
                val handler = Handler(Looper.getMainLooper())
                override fun execute(command: Runnable) {
                    handler.post(command)
                }
            },
            this
        )
    }

    override fun unregister() {
        telephonyManager.unregisterTelephonyCallback(this)
    }

}