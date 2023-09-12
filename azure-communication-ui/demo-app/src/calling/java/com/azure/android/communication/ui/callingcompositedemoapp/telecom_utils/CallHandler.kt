package com.azure.android.communication.ui.callingcompositedemoapp.telecom_utils

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Context.TELECOM_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.DisconnectCause
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.azure.android.communication.calling.PushNotificationInfo

@RequiresApi(Build.VERSION_CODES.M)
class CallHandler(context: Context) {

    var callManagerContext = context;
    var telecomManager : TelecomManager = context.getSystemService(TELECOM_SERVICE) as TelecomManager
    lateinit var phoneAccountHandle: PhoneAccountHandle

    init {
        val componentName = ComponentName(callManagerContext, CallConnectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            phoneAccountHandle = PhoneAccountHandle(componentName, "VoIP Calling")
            val phoneAccount = PhoneAccount.builder(phoneAccountHandle, "VoIP Calling")
                .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER).build()
            telecomManager.registerPhoneAccount(phoneAccount)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun startIncomingCall(notification: PushNotificationInfo) {
        if (callManagerContext.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS) ==
            PackageManager.PERMISSION_GRANTED) {
            try {
                telecomManager.addNewIncomingCall(phoneAccountHandle, callExtras(notification))
            } catch (e: SecurityException) {
                val intent = Intent()
                intent.action = TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS
                //TODO: Request permissions in advance
                val componentName = ComponentName("com.android.server.telecom", "com.android.server.telecom.settings.EnableAccountPreferenceActivity")
                intent.component = componentName
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                callManagerContext.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(callManagerContext,"Error occurred:"+e.message,Toast.LENGTH_LONG).show()
            }
        } else {
            Log.e("startIncomingCall: ","Permission not granted")
        }
    }

    fun endOngoingCall(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                callManagerContext,
                Manifest.permission.MANAGE_OWN_CALLS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            CallConnectionService.conn?.setDisconnected(DisconnectCause(DisconnectCause.REMOTE, "MISSED"))
        }

        return true
    }

    private fun callExtras(notification: PushNotificationInfo): Bundle {
        val extras = Bundle()
        val uri = Uri.fromParts(
            PhoneAccount.SCHEME_TEL, "ACS Demo Call",
            null)
        extras.putString("NAME", notification.fromDisplayName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (notification.isIncomingWithVideo) {
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
        extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri)
        extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
        extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)
        return extras
    }
}