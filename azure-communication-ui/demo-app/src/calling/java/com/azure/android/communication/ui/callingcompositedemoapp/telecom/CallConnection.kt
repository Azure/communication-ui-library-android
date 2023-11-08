package com.azure.android.communication.ui.callingcompositedemoapp.telecom

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telecom.CallAudioState
import android.telecom.Connection
import android.telecom.DisconnectCause
import android.util.Log
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallInfo
import com.azure.android.communication.ui.callingcompositedemoapp.CallCompositeManager

@RequiresApi(Build.VERSION_CODES.M)
class TelecomConnection(private val context: Context,
                        private val callComposite: CallComposite,
                        private var pushNotificationInfo: CallCompositeIncomingCallInfo? = null) : Connection() {
    companion object {
        private const val TAG = "communication.ui.demo"
    }

    override fun onStateChanged(state: Int) {
        super.onStateChanged(state)

        val stateName = when (state) {
            STATE_ACTIVE -> "ACTIVE"
            STATE_DIALING -> "DIALING"
            STATE_DISCONNECTED -> "DISCONNECTED"
            STATE_INITIALIZING -> "INITIALIZING"
            STATE_HOLDING -> "HOLDING"
            STATE_NEW -> "NEW"
            STATE_PULLING_CALL -> "PULLING_CALL"
            STATE_RINGING -> "RINGING"
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
        setActive()
        callComposite.acceptIncomingCall(context)
    }

    override fun onAnswer() {
        super.onAnswer()
        Log.d(TAG, "onAnswer" )
        setActive()
        callComposite.acceptIncomingCall(context)
    }

    override fun onHold() {
        super.onHold()
        callComposite.hold()
        setOnHold()
        Log.d(TAG, "onHold")
    }

    override fun onUnhold() {
        super.onUnhold()
        callComposite?.resume()
        setActive()
        Log.d(TAG, "onUnhold")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onShowIncomingCallUi() {
        super.onShowIncomingCallUi()
        Log.d(TAG, "onShowIncomingCallUi")
        // TODO: show UI notification. If user accepts then call setActive(),
        //  if user declines call setDisconnected(DisconnectCause(DisconnectCause.REJECTED, "Rejected"))
        pushNotificationInfo?.let { CallCompositeManager.getInstance().showIncomingCallUI(it) }
    }

    override fun onCallEvent(event: String?, extras: Bundle?) {
        super.onCallEvent(event, extras)
        Log.d(TAG, "onCallEvent: $event $extras")
    }

    override fun onReject() {
        super.onReject()
        setDisconnected(DisconnectCause(DisconnectCause.REMOTE, "Rejected"))
        destroy()
    }
}