package com.azure.android.communication.ui.utilities

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.action.CallingAction
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.ReduxState
import java.util.concurrent.Executor

/// Common Interface for the API >= 31 and < 31 mechanisms to do this
internal interface OffHookDetectionReceiver {
    companion object {
        fun register(context: Context, store: Store<ReduxState>): OffHookDetectionReceiver {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var receiver : OffHookDetectionReceiver = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                OffHookDetectionReceiver31(tm, store)
            } else {
                OffHookDetectionReceiverLegacy(tm, store)
            }
            receiver.register()
            return receiver
        }
    }

    val store: Store<ReduxState>
    fun register()
    fun unregister()
    fun onOffHook() {
        // Hang up the call
        store.dispatch(CallingAction.CallEndRequested())
    }
}

/// Legacy Version for < 31
internal class OffHookDetectionReceiverLegacy(private val telephonyManager: TelephonyManager, override val store:Store<ReduxState>) :
    PhoneStateListener(), OffHookDetectionReceiver {

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
internal class OffHookDetectionReceiver31(val telephonyManager: TelephonyManager, override val store:Store<ReduxState>) :
    TelephonyCallback(), TelephonyCallback.CallStateListener, OffHookDetectionReceiver {
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