package com.azure.android.communication.ui.callingcompositedemoapp.telecom

import android.os.Build
import android.os.Bundle
import android.telecom.CallAudioState
import android.telecom.Connection
import android.telecom.DisconnectCause
import android.util.Log
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.calling.CallComposite

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
        Log.d(TAG,"onDisconnect")
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL, "Missed"))
        destroy()
    }

    override fun onAnswer(videoState: Int) {
        super.onAnswer(videoState)
        Log.d(TAG, "onAnswer videoState: $videoState" )
       // callComposite.acceptIncomingCall(context)
    }

    override fun onAnswer() {
        super.onAnswer()
        Log.d(TAG, "onAnswer" )

        // callComposite.acceptIncomingCall(context)
    }

    override fun onHold() {
        super.onHold()
        callComposite.hold()
        setOnHold()
        Log.d(TAG, "onHold")
    }

    override fun onUnhold() {
        super.onUnhold()
        callComposite.resume()
        setActive()
        Log.d(TAG, "onUnhold")
    }

    override fun onShowIncomingCallUi() {
        super.onShowIncomingCallUi()
        Log.d(TAG, "onShowIncomingCallUi")
        // TODO: show UI notification. If user accepts then call setActive(),
        //  if user declines call setDisconnected(DisconnectCause(DisconnectCause.REJECTED, "Rejected"))
//        setActive()
//        callComposite.acceptIncomingCall(context)
    }

    override fun onCallEvent(event: String?, extras: Bundle?) {
        super.onCallEvent(event, extras)
        Log.d(TAG, "onCallEvent: $event $extras")
    }

}