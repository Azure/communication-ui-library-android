// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.telecom

import android.os.Build
import android.os.Bundle
import android.telecom.CallAudioState
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallInfo
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity.Companion.TAG
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherApplication

@RequiresApi(Build.VERSION_CODES.O)
class TelecomConnectionService : ConnectionService(), TelecomConnectionServiceListener {
    private var connection: TelecomConnection? = null

    override fun onCreate() {
        super.onCreate()
        val application = application as CallLauncherApplication
        application.telecomConnectionServiceListener = this
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest,
    ): Connection? {
        Log.e(TAG, "onCreateIncomingConnection")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val bundle = request.extras
            val name = bundle.getString("DISPLAY_NAME")
            val connection = createTelecomConnection(bundle)

            connection.setCallerDisplayName(name, TelecomManager.PRESENTATION_ALLOWED)
            connection.setAddress(request.address, TelecomManager.PRESENTATION_ALLOWED)
            this.connection = connection
            connection
        } else {
            null
        }
    }

    override fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?,
    ) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
        Log.e(TAG, "onCreateIncomingFailed: $request")
    }

    override fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?,
    ) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
        Log.e(TAG, "onCreateOutgoingFailed: $request")
    }

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest,
    ): Connection? {
        val bundle = request.extras

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            try {
                val connection = createTelecomConnection(bundle)
                connection.setDialing()
                this.connection = connection
                return connection
            } catch (_: Exception) {
            }
        }

        return null
    }

    private fun createTelecomConnection(
        originalBundle: Bundle
    ): TelecomConnection {
        val callInfo = CallCompositeIncomingCallInfo(
            originalBundle.getString("CALL_ID"),
            originalBundle.getString("DISPLAY_NAME"),
            originalBundle.getString("RAW_ID")
        )

        val connection = TelecomConnection(callInfo, context = this)
        connection.extras = originalBundle
        connection.connectionProperties = Connection.PROPERTY_SELF_MANAGED
        connection.connectionCapabilities = Connection.CAPABILITY_SUPPORT_HOLD or Connection.CAPABILITY_HOLD

        val callerDisplayName = originalBundle.getString("EXTRA_CALLER_DISPLAY_NAME")
        connection.setCallerDisplayName(callerDisplayName, TelecomManager.PRESENTATION_ALLOWED)
        connection.audioModeIsVoip = true

        return connection
    }

    override fun setActive() {
        connection?.setActive()
    }

    override fun onReject() {
        connection?.onReject()
        connection = null
    }

    override fun endConnection() {
        connection?.apply {
            setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
            destroy()
        }
        connection = null
    }

    override fun setAudioSelection(selectionType: String) {
        when (selectionType) {
            "SPEAKER_SELECTED" -> {
                connection?.setAudioRoute(CallAudioState.ROUTE_SPEAKER)
            }
            "RECEIVER_SELECTED" -> {
                connection?.setAudioRoute(CallAudioState.ROUTE_EARPIECE)
            }
            "BLUETOOTH_SCO_SELECTED" -> {
                connection?.setAudioRoute(CallAudioState.ROUTE_BLUETOOTH)
            }
        }
    }
}

interface TelecomConnectionServiceListener {
    fun setActive()
    fun onReject()
    fun endConnection()
    fun setAudioSelection(selectionType: String)
}
