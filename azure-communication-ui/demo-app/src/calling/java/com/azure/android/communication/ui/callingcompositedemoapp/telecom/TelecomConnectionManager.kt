// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.telecom

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Context.TELECOM_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEvent
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherApplication

/***
 * phoneAccountId - an unique per application string to register phone account.
 */
class TelecomConnectionManager(
    private val context: Context,
    private val phoneAccountId: String
) {
    private var phoneAccountHandle: PhoneAccountHandle? = null

    init {
        if (isConnectionServiceSupported()) {
            val telecomManager = context.getSystemService(TELECOM_SERVICE) as TelecomManager
            val componentName = ComponentName(context, TelecomConnectionService::class.java.name)
            val phoneAccountHandle = PhoneAccountHandle(componentName, phoneAccountId)
            registerPhoneAccount(telecomManager, phoneAccountHandle)
            this.phoneAccountHandle = phoneAccountHandle
        } else {
            this.phoneAccountHandle = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startIncomingConnection(callInfo: CallCompositeIncomingCallEvent, isVideoCall: Boolean) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun setConnectionActive() {
        (context.applicationContext as CallLauncherApplication).telecomConnectionServiceListener?.setActive()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startOutgoingConnection(callerDisplayName: String) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun declineCall() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.MANAGE_OWN_CALLS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            (context.applicationContext as CallLauncherApplication).telecomConnectionServiceListener?.onReject()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun endConnection() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.MANAGE_OWN_CALLS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            (context.applicationContext as CallLauncherApplication).telecomConnectionServiceListener?.endConnection()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setAudioSelection(audioSelection: CallCompositeAudioSelectionChangedEvent) {
        (context.applicationContext as CallLauncherApplication).telecomConnectionServiceListener?.setAudioSelection(audioSelection)
    }

    private fun isConnectionServiceSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    private fun registerPhoneAccount(
        telecomManager: TelecomManager,
        phoneAccountHandle: PhoneAccountHandle
    ) {
        if (isConnectionServiceSupported()) {
            clearExistingAccounts(telecomManager)
            val account = PhoneAccount.builder(phoneAccountHandle, phoneAccountId)
                .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED) // custom UI
                .build()
            try {
                telecomManager.registerPhoneAccount(account)
            } catch (ex: java.lang.Exception) {
                Log.e(CallLauncherActivity.TAG, "registerPhoneAccount failed: ${ex.message}", ex)
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
        } catch (ex: Exception) {
            Log.e(CallLauncherActivity.TAG, "clearExistingAccounts failed: ${ex.message}", ex)
        } catch (ex: NoSuchMethodException) {
            Log.e(CallLauncherActivity.TAG, "clearExistingAccounts failed: ${ex.message}", ex)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun callExtras(callInfo: CallCompositeIncomingCallEvent, isVideoCall: Boolean): Bundle {
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
}
