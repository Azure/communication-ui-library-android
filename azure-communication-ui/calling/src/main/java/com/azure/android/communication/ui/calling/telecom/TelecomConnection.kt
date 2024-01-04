package com.azure.android.communication.ui.calling.telecom

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.CallAudioState
import android.telecom.Connection
import android.telecom.DisconnectCause
import android.util.Log
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.getDependencyInjectionContainer
import com.azure.android.communication.ui.calling.redux.action.CallingAction

@RequiresApi(Build.VERSION_CODES.M)
class TelecomConnection(private val callComposite: CallComposite) : Connection() {
    companion object {
        private const val TAG = "TelecomIntegration"
    }

    override fun onStateChanged(state: Int) {
        super.onStateChanged(state)

        val stateName = when (state) {
            Connection.STATE_ACTIVE -> "ACTIVE"
            Connection.STATE_DIALING -> "DIALING"
            Connection.STATE_DISCONNECTED -> "DISCONNECTED"
            Connection.STATE_INITIALIZING -> "INITIALIZING"
            Connection.STATE_HOLDING -> "HOLDING"
            Connection.STATE_NEW -> "NEW"
            Connection.STATE_PULLING_CALL -> "PULLING_CALL"
            Connection.STATE_RINGING -> "RINGING"
            else -> "UNKNOWN"
        }

        Log.d(TAG, "onStateChanged: $stateName")
    }

    override fun onCallAudioStateChanged(state: CallAudioState?) {
        super.onCallAudioStateChanged(state)
        Log.d(TAG, "onCallAudioStateChange:" + state.toString())
    }

    override fun onDisconnect() {
        super.onDisconnect()
        Log.d(TAG, "onDisconnect")
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL, "Missed"))
        destroy()
    }

    override fun onAnswer(videoState: Int) {
        super.onAnswer(videoState)
        Log.d(TAG, "onAnswer videoState: $videoState")
        setActive()
    }

    override fun onAnswer() {
        super.onAnswer()
        Log.d(TAG, "onAnswer")
        setActive()
    }

    override fun onHold() {
        super.onHold()
        callComposite.getDependencyInjectionContainer()
            .appStore.dispatch(CallingAction.HoldRequested())
        setOnHold()
        Log.d(TAG, "onHold")
    }

    override fun onUnhold() {
        super.onUnhold()
        callComposite.getDependencyInjectionContainer()
            .appStore.dispatch(CallingAction.ResumeRequested())
        setActive()
        Log.d(TAG, "onUnhold")
    }

    override fun onShowIncomingCallUi() {
        super.onShowIncomingCallUi()
        Log.d(TAG, "onShowIncomingCallUi")
//        setActive()
    }

    override fun onAbort() {
        super.onAbort()
        Log.d(TAG, "onAbort")
    }

    override fun onDeflect(address: Uri?) {
        super.onDeflect(address)
        Log.d(TAG, "onDeflect")
    }

    override fun onCallEvent(event: String?, extras: Bundle?) {
        super.onCallEvent(event, extras)
        Log.d(TAG, "onCallEvent: $event $extras")
    }

    override fun onReject() {
        super.onReject()
        Log.d(TAG, "onReject")
        setDisconnected(DisconnectCause(DisconnectCause.REJECTED, "Rejected"))
    }
}
