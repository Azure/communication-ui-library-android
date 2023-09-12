package com.azure.android.communication.ui.callingcompositedemoapp.telecom_utils

import android.content.Context
import android.os.Build
import android.telecom.CallAudioState
import android.telecom.Connection
import android.telecom.DisconnectCause
import android.util.Log
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity

@RequiresApi(Build.VERSION_CODES.M)
class CallConnection(private val connectionContext: Context) : Connection() {
    companion object {
        private const val TAG = "CallConnection"
    }

    override fun onCallAudioStateChanged(state: CallAudioState?) {
        Log.e(TAG, "onCallAudioStateChange:" + state.toString())
    }

    override fun onDisconnect() {
        super.onDisconnect()
        destroyConnection()
        Log.e(TAG,"onDisconnect")
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL, "Missed"))
    }

    override fun onAnswer() {
        destroyConnection()
        CallLauncherActivity.callLauncherActivity?.answerCall()
    }

    private fun destroyConnection() {
        setDisconnected(DisconnectCause(DisconnectCause.REMOTE, "Rejected"))
        Log.e(TAG, "destroyConnection" )
        super.destroy()
    }

    override fun onReject() {
        Log.e(TAG, "onReject: " )
        destroyConnection()
    }

    fun onOutgoingReject() {
        Log.e(TAG,"onDisconnect")
        destroyConnection()
        setDisconnected(DisconnectCause(DisconnectCause.REMOTE, "REJECTED"))
    }
}