package com.azure.android.communication.ui.calling.telecom

import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.calling.CallCompositeInstanceManager

@RequiresApi(Build.VERSION_CODES.M)
class TelecomConnectionService : ConnectionService() {

    companion object {
        var connection: TelecomConnection? = null
        private const val TAG = "TelecomIntegration"
    }

    override fun onCreateIncomingConnection(
            connectionManagerPhoneAccount: PhoneAccountHandle?,
            request: ConnectionRequest,
    ): Connection? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val bundle = request.extras
            val name = bundle.getString("NAME")
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
        Log.e(TAG, "onCreateOutgoingFailed: ${request.toString()}")

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

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun createTelecomConnection(
            originalBundle: Bundle
    ): TelecomConnection {
        val instanceId = originalBundle.getInt("instanceId")
        val callComposite = CallCompositeInstanceManager.getCallComposite(instanceId)
        val connection = TelecomConnection(callComposite)
        connection.extras = originalBundle
        //custom UI
        connection.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED)
        connection.connectionCapabilities = Connection.CAPABILITY_SUPPORT_HOLD or Connection.CAPABILITY_HOLD

        val callerDisplayName = originalBundle.getString("EXTRA_CALLER_DISPLAY_NAME")
        connection.setCallerDisplayName(callerDisplayName, TelecomManager.PRESENTATION_ALLOWED)
        connection.setAudioModeIsVoip(true)

        return connection
    }
}