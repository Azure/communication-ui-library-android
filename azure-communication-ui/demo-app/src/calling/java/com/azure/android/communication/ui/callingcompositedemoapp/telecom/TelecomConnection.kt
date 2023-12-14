// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.telecom

import android.os.Build
import android.os.Bundle
import android.telecom.CallAudioState
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.util.Log
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity.Companion.TAG

@RequiresApi(Build.VERSION_CODES.M)
class TelecomConnection(
    private val telecomConnectionCallBack: TelecomConnectionCallBack
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
        telecomConnectionCallBack.onAnswer()
    }

    override fun onAnswer() {
        super.onAnswer()
        Log.d(TAG, "onAnswer")
        setActive()
        telecomConnectionCallBack.onAnswer()
    }

    override fun onHold() {
        super.onHold()
        telecomConnectionCallBack.hold()
        setOnHold()
        Log.d(TAG, "onHold")
    }

    override fun onUnhold() {
        super.onUnhold()
        telecomConnectionCallBack.resume()
        setActive()
        Log.d(TAG, "onUnhold")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onShowIncomingCallUi() {
        super.onShowIncomingCallUi()
        Log.d(TAG, "onShowIncomingCallUi")
        telecomConnectionCallBack.onShowIncomingCallUi()
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

interface TelecomConnectionCallBack {
    fun onShowIncomingCallUi()
    fun onAnswer()
    fun hold()
    fun resume()
    fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest,
    ): Connection?
    fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest,
    ): Connection?
}
