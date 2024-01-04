package com.azure.android.communication.ui.calling.telecom

import android.Manifest
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Context.TELECOM_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.telecom.DisconnectCause
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

@RequiresApi(Build.VERSION_CODES.M)
internal class TelecomConnectionManager(
    context: Context,
    val phoneAccountId: String,
    private val instanceId: Int
) {

    private val TAG = "TelecomIntegration"
    private var phoneAccountHandle: PhoneAccountHandle?

    companion object {
        var instance: TelecomConnectionManager? = null
    }

    init {
        if (isConnectionServiceSupported()) {
            val telecomManager = context.getSystemService(TELECOM_SERVICE) as TelecomManager
            val componentName = ComponentName(context, TelecomConnectionService::class.java.name)
            val phoneAccountHandle = PhoneAccountHandle(componentName, phoneAccountId)
            registerPhoneAccount(telecomManager, phoneAccountHandle)

            this.phoneAccountHandle = phoneAccountHandle

            instance = this
        } else {
            this.phoneAccountHandle = null
        }
    }

    fun TEST_setActive() {
        TelecomConnectionService.connection?.setActive()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun startIncomingConnection(context: Context, fromDisplayName: String, isVideoCall: Boolean) {
        if (context.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            try {
                val telecomManager = context.getSystemService(TELECOM_SERVICE) as TelecomManager
                telecomManager.addNewIncomingCall(phoneAccountHandle, callExtras(fromDisplayName, isVideoCall))
            } catch (e: SecurityException) {
                val intent = Intent()
                intent.setClassName(
                    "com.android.server.telecom",
                    "com.android.server.telecom.settings.EnableAccountPreferenceActivity"
                )
                context.startActivity(intent)
                Log.e(TAG, "startIncomingCall: ${e.message}", e)
            } catch (e: Exception) {
                Toast.makeText(context, "Error occurred:" + e.message, Toast.LENGTH_LONG).show()
            }
        } else {
            Log.e(TAG, "startIncomingCall: Permission not granted")
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun startOutgoingConnection(context: Context, callerDisplayName: String, isVideoCall: Boolean) {

        if (context.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS) ==
            PackageManager.PERMISSION_GRANTED
        ) {

            if (context.checkSelfPermission(Manifest.permission.READ_CALL_LOG) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                var columns = arrayOf<String>(CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.TYPE)

                val cursor = context.contentResolver.query(CallLog.Calls.CONTENT_URI, columns, null, null, "${CallLog.Calls.LAST_MODIFIED} DESC")

                cursor?.use {
                    while (cursor.moveToNext()) {
                        val number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
                        Log.d(TAG, "startOutgoingConnection $number")
                    }
                }
            }

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
                Log.e(TAG, "startIncomingCall: ${e.message}", e)
            } catch (e: Exception) {
                Log.e(TAG, "startOutgoingCall: ${e.message}", e)
            }
        } else {
            Log.e(TAG, "startOutgoingCall: Permission not granted")
        }
    }

    fun endConnection(context: Context, callerDisplayName: String) {
        Log.d(TAG, "endConnection")
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.MANAGE_OWN_CALLS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val connection = TelecomConnectionService.connection

            connection?.apply {
                setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
                destroy()
            }
            TelecomConnectionService.connection = null
        }

        val values = ContentValues()
        values.put(CallLog.Calls.NUMBER, callerDisplayName)
        values.put(CallLog.Calls.DATE, System.currentTimeMillis())
        values.put(CallLog.Calls.DURATION, 0)
        values.put(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE)
        values.put(CallLog.Calls.NEW, 1)
        values.put(CallLog.Calls.CACHED_NAME, "CACHED_NAME")
        values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0)
        values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "CACHED_NUMBER_LABEL")

//        context.contentResolver.insert(CallLog.Calls.CONTENT_URI, values)
    }

    private fun isConnectionServiceSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    private fun registerPhoneAccount(
        telecomManager: TelecomManager, phoneAccountHandle: PhoneAccountHandle
    ) {
        if (isConnectionServiceSupported()) {
            clearExistingAccounts(telecomManager)
            val account = PhoneAccount.builder(phoneAccountHandle, phoneAccountId)
                .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED) // custom UI
                .build()
            try {
                telecomManager.registerPhoneAccount(account)
            } catch (ex: java.lang.Exception) {
                Log.e(TAG, "registerPhoneAccount ${ex.message}", ex)
            }
        }
    }

    private fun clearExistingAccounts(telecomManager: TelecomManager) {
        try {
            // NOTE: This is done one time to clear existing phone accounts registered on the phone
            // There is an issue related to 911 on Android OS.
            // Native phone app is crashing when user dials 911 when large number of accounts are registered with the phone
            val clearMethod = TelecomManager::class.java.getMethod("clearPhoneAccounts", null)
            clearMethod.invoke(telecomManager)
//            logger.log(LogPriority.INFO, LOG_TAG, "Called clearPhoneAccounts successfully")
        } catch (ex: java.lang.Exception) {
            Log.e(TAG, "clearExistingAccounts ${ex.message}", ex)
        }
    }

    private fun callExtras(fromDisplayName: String, isVideoCall: Boolean): Bundle {
        val extras = Bundle()
        extras.putString("NAME", fromDisplayName)
        extras.putInt("instanceId", instanceId)
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
//        extras.putInt(TelecomManager.EXTRA_LOG_SELF_MANAGED_CALLS)

        return extras
    }
}
