package com.azure.android.communication.ui.callingcompositedemoapp.telecom

import android.os.Build
import android.os.Bundle
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallInfo
import com.azure.android.communication.ui.callingcompositedemoapp.CallCompositeManager
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherViewModel

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
            val connection = createTelecomConnection(bundle)

            connection.setCallerDisplayName(name, TelecomManager.PRESENTATION_ALLOWED)
            connection.setAddress(request.address, TelecomManager.PRESENTATION_ALLOWED)
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
        Log.e(TAG, "onCreateIncomingFailed: ${request.toString()}")
    }

    override fun onCreateOutgoingConnectionFailed(
            connectionManagerPhoneAccount: PhoneAccountHandle?,
            request: ConnectionRequest?,
    ) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
        Log.e(TAG ,"onCreateOutgoingFailed: ${request.toString()}")

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

                TelecomConnectionService.connection = connection
                return connection
            } catch (e: Exception) {

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

        var callComposite: CallComposite? = CallCompositeManager.getInstance().getCallComposite()
        if(callComposite == null) {
            callComposite = CallCompositeManager.getInstance().createCallComposite()
        }
        val connection = TelecomConnection(applicationContext, callComposite, callInfo)
        connection.extras = originalBundle
        connection.connectionProperties = Connection.PROPERTY_SELF_MANAGED
        connection.connectionCapabilities = Connection.CAPABILITY_SUPPORT_HOLD or Connection.CAPABILITY_HOLD

        val callerDisplayName = originalBundle.getString("EXTRA_CALLER_DISPLAY_NAME")
        connection.setCallerDisplayName(callerDisplayName, TelecomManager.PRESENTATION_ALLOWED)
        connection.audioModeIsVoip = true

        return connection
    }
}