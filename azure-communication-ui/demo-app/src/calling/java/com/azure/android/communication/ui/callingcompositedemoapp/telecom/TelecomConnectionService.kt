// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.telecom

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.CallAudioState
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEndEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallInfo
import com.azure.android.communication.ui.callingcompositedemoapp.CallCompositeManager
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity

@RequiresApi(Build.VERSION_CODES.O)
class TelecomConnectionService : ConnectionService(), TelecomConnectionCallBack, ConnectionServiceCallComposite {
    private var connection: TelecomConnection? = null
    private val callStateEventHandler = CallStateEventHandler(this)
    private val callEndEventHandler = CallEndEventHandler(this)
    private val incomingCallEventHandler = IncomingCallEventHandler(this)
    private val audioSelectionChangedEventHandler = AudioSelectionChangedEventHandler(this)
    private val compositeDismissedEventHandler = CompositeDismissedEventHandler(this)
    private var phoneAccountHandle: PhoneAccountHandle? = null

    companion object {
        private const val TAG = "communication.ui.demo"
        private const val PHONE_ACCOUNT_ID = ""
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate TelecomConnectionService")
        phoneAccountHandle = PhoneAccountHandle(
            ComponentName(
                applicationContext,
                TelecomConnectionService::class.java
            ),
            PHONE_ACCOUNT_ID
        )
        val telecomManager = applicationContext.getSystemService(TELECOM_SERVICE) as TelecomManager
        registerPhoneAccount(telecomManager, phoneAccountHandle!!)
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
            } catch (e: Exception) {
            }
        }

        return null
    }

    private fun isConnectionServiceSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    private fun clearExistingAccounts(telecomManager: TelecomManager) {
        try {
            // NOTE: This is done one time to clear existing phone accounts registered on the phone
            // There is an issue related to 911 on Android OS.
            // Native phone app is crashing when user dials 911 when large number of accounts are registered with the phone
            val clearMethod = TelecomManager::class.java.getMethod("clearPhoneAccounts", null)
            clearMethod.invoke(telecomManager)
        } catch (ex: Exception) {
            Log.e(CallLauncherActivity.TAG, "clearExistingAccounts failed: ${ex.message}", ex)
        } catch (ex: NoSuchMethodException) {
            Log.e(CallLauncherActivity.TAG, "clearExistingAccounts failed: ${ex.message}", ex)
        }
    }

    private fun registerPhoneAccount(
        telecomManager: TelecomManager,
        phoneAccountHandle: PhoneAccountHandle
    ) {
        if (isConnectionServiceSupported()) {
            clearExistingAccounts(telecomManager)
            val account = PhoneAccount.builder(phoneAccountHandle, PHONE_ACCOUNT_ID)
                .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED) // custom UI
                .build()
            try {
                telecomManager.registerPhoneAccount(account)
            } catch (ex: java.lang.Exception) {
                Log.e(CallLauncherActivity.TAG, "registerPhoneAccount failed: ${ex.message}", ex)
            }
        }
    }

    private fun createTelecomConnection(
        originalBundle: Bundle,
    ): TelecomConnection {

        var callComposite: CallComposite? = CallCompositeManager.getInstance().getCallComposite()
        if (callComposite == null) {
            callComposite = CallCompositeManager.getInstance().createCallComposite(applicationContext)
        }
        callComposite.addOnCallStateChangedEventHandler(callStateEventHandler)
        callComposite.addOnIncomingCallEndEventHandler(callEndEventHandler)
        callComposite.addOnIncomingCallEventHandler(incomingCallEventHandler)
        callComposite.addOnAudioSelectionChangedEventHandler(audioSelectionChangedEventHandler)
        callComposite.addOnDismissedEventHandler(compositeDismissedEventHandler)

        val connection = TelecomConnection(this)
        connection.extras = originalBundle
        connection.connectionProperties = Connection.PROPERTY_SELF_MANAGED
        connection.connectionCapabilities = Connection.CAPABILITY_SUPPORT_HOLD or Connection.CAPABILITY_HOLD

        val callerDisplayName = originalBundle.getString("EXTRA_CALLER_DISPLAY_NAME")
        connection.setCallerDisplayName(callerDisplayName, TelecomManager.PRESENTATION_ALLOWED)
        connection.audioModeIsVoip = true

        return connection
    }

    override fun onShowIncomingCallUi() {
        val incomingCallIntent =
            Intent(applicationContext, CallLauncherActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        incomingCallIntent.action = "incoming_call"
        incomingCallIntent.putExtra("action", "incoming_call")
        applicationContext.startActivity(incomingCallIntent)
    }

    override fun onAnswer() {
        val answerCallIntent =
            Intent(applicationContext, CallLauncherActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        answerCallIntent.action = "answer"
        answerCallIntent.putExtra("action", "answer")
        applicationContext.startActivity(answerCallIntent)
    }

    override fun hold() {
        var callComposite: CallComposite? = CallCompositeManager.getInstance().getCallComposite()
        if (callComposite == null) {
            callComposite = CallCompositeManager.getInstance().createCallComposite(applicationContext)
        }
        callComposite.hold()
    }

    override fun resume() {
        var callComposite: CallComposite? = CallCompositeManager.getInstance().getCallComposite()
        if (callComposite == null) {
            callComposite = CallCompositeManager.getInstance().createCallComposite(applicationContext)
        }
        callComposite.resume()
    }

    private fun setConnectionActive() {
        connection?.setActive()
    }

    private fun startOutgoingConnection(context: Context, callerDisplayName: String) {
        if (context.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            try {
                val telecomManager = context.getSystemService(TELECOM_SERVICE) as TelecomManager

                val callExtras = Bundle()
                callExtras.putString("EXTRA_CALL_GUID", "callGuid")
                callExtras.putString("EXTRA_CALLER_DISPLAY_NAME", callerDisplayName)

                val extras = Bundle()
                extras.putParcelable(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, callExtras)
                extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
                extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)

                val uri = Uri.fromParts("tel", callerDisplayName, "")
                telecomManager.placeCall(uri, extras)
            } catch (e: SecurityException) {
                val intent = Intent()
                intent.setClassName(
                    "com.android.server.telecom",
                    "com.android.server.telecom.settings.EnableAccountPreferenceActivity"
                )
                context.startActivity(intent)
                Log.e(CallLauncherActivity.TAG, "startOutgoingConnection: ${e.message}", e)
            } catch (e: Exception) {
                Log.e(CallLauncherActivity.TAG, "startOutgoingConnection: ${e.message}", e)
            }
        } else {
            Log.e(CallLauncherActivity.TAG, "startOutgoingConnection: Permission not granted")
        }
    }

    private fun endConnection(context: Context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.MANAGE_OWN_CALLS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            connection?.apply {
                setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
                destroy()
            }
            connection = null
        }
    }

    private fun declineCall(context: Context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.MANAGE_OWN_CALLS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            connection?.onReject()
            connection = null
        }
    }

    private fun startIncomingConnection(context: Context, callInfo: CallCompositeIncomingCallInfo, isVideoCall: Boolean) {
        if (context.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            try {
                Log.e(CallLauncherActivity.TAG, "startIncomingConnection")
                val telecomManager = context.getSystemService(TELECOM_SERVICE) as TelecomManager
                telecomManager.addNewIncomingCall(phoneAccountHandle, callExtras(callInfo, isVideoCall))
            } catch (e: SecurityException) {
                val intent = Intent()
                intent.setClassName(
                    "com.android.server.telecom",
                    "com.android.server.telecom.settings.EnableAccountPreferenceActivity"
                )
                context.startActivity(intent)
                Log.e(CallLauncherActivity.TAG, "startIncomingConnection failed: ${e.message}", e)
            } catch (e: Exception) {
                Log.e(CallLauncherActivity.TAG, "startIncomingConnection failed: ${e.message}", e)
                Toast.makeText(context, "Error occurred: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Log.e(CallLauncherActivity.TAG, "startIncomingCall: Permission not granted")
        }
    }

    private fun callExtras(callInfo: CallCompositeIncomingCallInfo, isVideoCall: Boolean): Bundle {
        val extras = Bundle()
        extras.putString("DISPLAY_NAME", callInfo.callerDisplayName)
        extras.putString("CALL_ID", callInfo.callId)
        extras.putString("RAW_ID", callInfo.callerIdentifierRawId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (isVideoCall) {
                extras.putInt(
                    TelecomManager.EXTRA_INCOMING_VIDEO_STATE,
                    VideoProfile.STATE_BIDIRECTIONAL
                )
            } else {
                extras.putInt(
                    TelecomManager.EXTRA_INCOMING_VIDEO_STATE,
                    VideoProfile.STATE_AUDIO_ONLY
                )
            }
        }
        val uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, "ACS Call", null)
        extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri)
        extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
        extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)

        return extras
    }

    override fun onIncomingCall(incomingCallInfo: CallCompositeIncomingCallInfo) {
        startIncomingConnection(
            applicationContext,
            incomingCallInfo,
            false
        )
    }

    override fun onIncomingCallEnded() {
        declineCall(applicationContext)
    }

    override fun dismissed() {
        endConnection(applicationContext)
        val callComposite = CallCompositeManager.getInstance().getCallComposite()
        callComposite?.removeOnCallStateChangedEventHandler(callStateEventHandler)
        callComposite?.removeOnIncomingCallEndEventHandler(callEndEventHandler)
        callComposite?.removeOnIncomingCallEventHandler(incomingCallEventHandler)
        callComposite?.removeOnAudioSelectionChangedEventHandler(audioSelectionChangedEventHandler)
        callComposite?.removeOnDismissedEventHandler(compositeDismissedEventHandler)
    }

    override fun onCallStateChanged(code: CallCompositeCallStateCode) {
        if (code == CallCompositeCallStateCode.CONNECTING) {
            startOutgoingConnection(
                applicationContext,
                "Outgoing call"
            )
        }

        if (code == CallCompositeCallStateCode.CONNECTED) {
            setConnectionActive()
        }

        if (code == CallCompositeCallStateCode.DISCONNECTING ||
            code == CallCompositeCallStateCode.DISCONNECTED
        ) {
            endConnection(applicationContext)
        }
    }

    override fun onAudioStateChanged(code: String) {
        when (code) {
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

    class CallStateEventHandler(
        private val connectionServiceCallComposite: ConnectionServiceCallComposite
    ) : CallCompositeEventHandler<CallCompositeCallStateChangedEvent> {
        override fun handle(eventArgs: CallCompositeCallStateChangedEvent) {
            connectionServiceCallComposite.onCallStateChanged(eventArgs.code)
        }
    }

    class CallEndEventHandler(
        private val connectionServiceCallComposite: ConnectionServiceCallComposite
    ) : CallCompositeEventHandler<CallCompositeIncomingCallEndEvent> {
        override fun handle(eventArgs: CallCompositeIncomingCallEndEvent?) {
            connectionServiceCallComposite.onIncomingCallEnded()
        }
    }

    class IncomingCallEventHandler(
        private val connectionServiceCallComposite: ConnectionServiceCallComposite
    ) : CallCompositeEventHandler<CallCompositeIncomingCallEvent> {
        override fun handle(eventArgs: CallCompositeIncomingCallEvent) {
            connectionServiceCallComposite.onIncomingCall(eventArgs.incomingCallInfo)
        }
    }

    class AudioSelectionChangedEventHandler(
        private val connectionServiceCallComposite: ConnectionServiceCallComposite
    ) : CallCompositeEventHandler<CallCompositeAudioSelectionChangedEvent> {
        override fun handle(eventArgs: CallCompositeAudioSelectionChangedEvent) {
            connectionServiceCallComposite.onAudioStateChanged(eventArgs.selectionType)
        }
    }

    class CompositeDismissedEventHandler(
        private val connectionServiceCallComposite: ConnectionServiceCallComposite
    ) : CallCompositeEventHandler<CallCompositeDismissedEvent> {
        override fun handle(eventArgs: CallCompositeDismissedEvent) {
            connectionServiceCallComposite.dismissed()
        }
    }
}

interface ConnectionServiceCallComposite {
    fun onIncomingCall(incomingCallInfo: CallCompositeIncomingCallInfo)
    fun dismissed()
    fun onIncomingCallEnded()
    fun onCallStateChanged(code: CallCompositeCallStateCode)
    fun onAudioStateChanged(code: String)
}
