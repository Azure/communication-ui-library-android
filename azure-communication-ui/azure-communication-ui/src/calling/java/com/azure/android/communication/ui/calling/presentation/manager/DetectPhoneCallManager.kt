package com.azure.android.communication.ui.calling.presentation.manager

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

// Detects phone calls and hangs up if accepted.
internal class DetectPhoneCallManager(val context: Context, val store: Store<ReduxState>) {
    private var detectPhoneCall: DetectPhoneCall? = null

    suspend fun onCreate() {
        store.getStateFlow().collect {
            if (it.permissionState.phonePermissionState == PermissionStatus.GRANTED) {
                detectPhoneCall = DetectPhoneCall.register(context, store)
            }
        }
    }

    fun onDestroy() = detectPhoneCall?.unregister()
}

// / Common Interface for the API >= 31 and < 31 mechanisms to do this
private interface DetectPhoneCall {
    companion object {
        fun register(context: Context, store: Store<ReduxState>): DetectPhoneCall {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var receiver: DetectPhoneCall = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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

// / Legacy Version for < 31
private class DetectPhoneCallLegacy(private val telephonyManager: TelephonyManager, override val store: Store<ReduxState>) :
    PhoneStateListener(), DetectPhoneCall {

    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        super.onCallStateChanged(state, phoneNumber)
        if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
            onOffHook()
        }
    }

    override fun register() {
        telephonyManager.listen(this, LISTEN_CALL_STATE)
    }

    override fun unregister() {
        telephonyManager.listen(this, LISTEN_NONE)
    }
}

// New version for >=31
@RequiresApi(Build.VERSION_CODES.S)
private class DetectPhoneCallV31(val telephonyManager: TelephonyManager, override val store: Store<ReduxState>) :
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
