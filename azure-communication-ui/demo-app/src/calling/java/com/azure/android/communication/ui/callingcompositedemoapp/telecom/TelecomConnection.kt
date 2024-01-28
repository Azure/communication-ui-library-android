// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.telecom

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.CallAudioState
import android.telecom.Connection
import android.telecom.DisconnectCause
import android.util.Log
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEvent
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity

@RequiresApi(Build.VERSION_CODES.M)
class TelecomConnection(
    private var pushNotificationInfo: CallCompositeIncomingCallEvent? = null,
    private val context: Context
) : Connection() {

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
        Log.d(CallLauncherActivity.TAG, "onStateChanged: $stateName")
    }

    override fun onCallAudioStateChanged(state: CallAudioState?) {
        super.onCallAudioStateChanged(state)
        Log.d(CallLauncherActivity.TAG, "onCallAudioStateChange:" + state.toString())
    }

    override fun onDisconnect() {
        super.onDisconnect()
        Log.d(CallLauncherActivity.TAG, "onDisconnect")
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL, "Missed"))
        destroy()
    }

    override fun onAnswer(videoState: Int) {
        super.onAnswer(videoState)
        Log.d(CallLauncherActivity.TAG, "onAnswer videoState: $videoState")
        setActive()
        launchIntent("answer")
    }

    override fun onAnswer() {
        super.onAnswer()
        Log.d(CallLauncherActivity.TAG, "onAnswer")
        setActive()
        launchIntent("answer")
    }

    override fun onHold() {
        super.onHold()
        launchIntent("hold")
        setOnHold()
        Log.d(CallLauncherActivity.TAG, "hold")
    }

    override fun onUnhold() {
        super.onUnhold()
        launchIntent("resume")
        setActive()
        Log.d(CallLauncherActivity.TAG, "resume")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onShowIncomingCallUi() {
        super.onShowIncomingCallUi()
        Log.d(CallLauncherActivity.TAG, "onShowIncomingCallUi")
        pushNotificationInfo?.let {
            launchIntent("incoming_call")
        }
    }

    override fun onCallEvent(event: String?, extras: Bundle?) {
        super.onCallEvent(event, extras)
        Log.d(CallLauncherActivity.TAG, "onCallEvent: $event $extras")
    }

    override fun onReject() {
        super.onReject()
        setDisconnected(DisconnectCause(DisconnectCause.REMOTE, "Rejected"))
        destroy()
    }

    private fun launchIntent(action: String) {
        if (CallLauncherActivity.isActivityRunning) {
            val intent = Intent(CallLauncherActivity.CALL_LAUNCHER_BROADCAST_ACTION)
            intent.putExtra("tag", action)
            context.sendBroadcast(intent)
        } else {
            val intent =
                Intent(context, CallLauncherActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            intent.action = action
            intent.putExtra("action", action)
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            ).send()
        }
    }
}
