package com.azure.android.communication.ui.callingcompositedemoapp.telecom_utils

import android.os.Build
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.M)
class CallConnectionService : ConnectionService() {

    companion object {
        var conn : CallConnection? = null
    }
    override fun onCreateIncomingConnection(connectionManagerPhoneAccount: PhoneAccountHandle?, request: ConnectionRequest?): Connection {
        val bundle = request!!.extras
        val name = bundle.getString("NAME")
        conn = CallConnection(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            conn?.connectionProperties = Connection.PROPERTY_SELF_MANAGED
        }
        conn?.setCallerDisplayName(name, TelecomManager.PRESENTATION_ALLOWED)
        conn?.setAddress(request.address, TelecomManager.PRESENTATION_ALLOWED)
        conn?.setInitializing()
        conn?.setActive()
        return conn!!
    }

    override fun onCreateIncomingConnectionFailed(connectionManagerPhoneAccount: PhoneAccountHandle?, request: ConnectionRequest?) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
        Log.e("onCreateIncomingFailed:",request.toString())
        Toast.makeText(applicationContext,"onCreateIncomingConnectionFailed",Toast.LENGTH_LONG).show()
    }

    override fun onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount: PhoneAccountHandle?, request: ConnectionRequest?) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
        Log.e("onCreateOutgoingFailed:",request.toString())
        Toast.makeText(applicationContext,"onCreateOutgoingConnectionFailed",Toast.LENGTH_LONG).show();
    }

    override fun onCreateOutgoingConnection(connectionManagerPhoneAccount: PhoneAccountHandle?, request: ConnectionRequest?): Connection {
        val bundle = request!!.extras
        val name = bundle.getString("NAME")
        conn = CallConnection(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            conn?.connectionProperties = Connection.PROPERTY_SELF_MANAGED
        }
        conn?.setCallerDisplayName(name, TelecomManager.PRESENTATION_ALLOWED)
        conn?.setAddress(request.address, TelecomManager.PRESENTATION_ALLOWED)
        conn?.setInitializing()
        conn?.setActive()
        return conn!!
    }
}