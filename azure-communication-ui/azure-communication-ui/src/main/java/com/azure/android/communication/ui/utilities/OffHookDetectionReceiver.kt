package com.azure.android.communication.ui.utilities

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import java.util.concurrent.Executor

internal interface OffHookDetectionReceiver {

    companion object {
        fun register(context: Context): OffHookDetectionReceiver {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var receiver : OffHookDetectionReceiver = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                OffHookDetectionReceiver31(tm)
            } else {
                OffHookDetectionReceiverLegacy(tm)
            }
            receiver.register()
            return receiver
        }
    }

    fun register()
    fun unregister()

    fun offhook() {
        System.out.println("OFF HOOK NOW")
    }
}

/// Legacy Version for < 31
internal class OffHookDetectionReceiverLegacy(private val telephonyManager: TelephonyManager) :
    PhoneStateListener(), OffHookDetectionReceiver {

    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        super.onCallStateChanged(state, phoneNumber)
        if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
            offhook()
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
internal class OffHookDetectionReceiver31(val telephonyManager: TelephonyManager) :
    TelephonyCallback(), TelephonyCallback.CallStateListener, OffHookDetectionReceiver {
    override fun onCallStateChanged(state: Int) {
        if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
            offhook()
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