// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.telecom

import android.os.Build
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class TelecomConnectionService : ConnectionService() {

    companion object {
        var connection: TelecomConnection? = null
        private const val TAG = "communication.ui.demo"
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest,
    ): Connection? {
        Log.e(TAG, "onCreateIncomingConnection")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val bundle = request.extras
            val name = bundle.getString("DISPLAY_NAME")
            val connection = null; // createTelecomConnection(bundle)

            // connection.setCallerDisplayName(name, TelecomManager.PRESENTATION_ALLOWED)
            // connection.setAddress(request.address, TelecomManager.PRESENTATION_ALLOWED)
            TelecomConnectionService.connection = connection
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
        /*
        val bundle = request.extras

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            try {
                val connection = createTelecomConnection(bundle)
                connection.setDialing()

                TelecomConnectionService.connection = connection
                return connection
            } catch (e: Exception) {
            }
        }
        */
        return null
    }

/*
    private fun createTelecomConnection(
        originalBundle: Bundle
    ): TelecomConnection {

        val callInfo = CallCompositeIncomingCallInfo(
            originalBundle.getString("CALL_ID"),
            originalBundle.getString("DISPLAY_NAME"),
            originalBundle.getString("RAW_ID")
        )

        var callComposite: CallComposite? = CallCompositeManager.getInstance().getCallComposite()
        if (callComposite == null) {
            callComposite = CallCompositeManager.getInstance().createCallComposite()
        }
        val connection = TelecomConnection(callComposite, callInfo)
        connection.extras = originalBundle
        connection.connectionProperties = Connection.PROPERTY_SELF_MANAGED
        connection.connectionCapabilities = Connection.CAPABILITY_SUPPORT_HOLD or Connection.CAPABILITY_HOLD

        val callerDisplayName = originalBundle.getString("EXTRA_CALLER_DISPLAY_NAME")
        connection.setCallerDisplayName(callerDisplayName, TelecomManager.PRESENTATION_ALLOWED)
        connection.audioModeIsVoip = true

        return connection
    }
 */
}
