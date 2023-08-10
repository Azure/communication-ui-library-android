// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchatdemoapp.features

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LayoutDirection
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeParticipantViewData
import com.azure.android.communication.ui.callwithchatdemoapp.AVATAR_IMAGE
import com.azure.android.communication.ui.callwithchatdemoapp.CALL_SUBTITLE
import com.azure.android.communication.ui.callwithchatdemoapp.CALL_TITLE
import com.azure.android.communication.ui.callwithchatdemoapp.DEFAULT_LANGUAGE_VALUE
import com.azure.android.communication.ui.callwithchatdemoapp.DEFAULT_PERSONA_INJECTION_VALUE_PREF_KEY
import com.azure.android.communication.ui.callwithchatdemoapp.DEFAULT_RTL_VALUE
import com.azure.android.communication.ui.callwithchatdemoapp.LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.callwithchatdemoapp.LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.callwithchatdemoapp.RENDERED_DISPLAY_NAME
import com.azure.android.communication.ui.callwithchatdemoapp.SETTINGS_SHARED_PREFS
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.Locale

class SettingsFeatures {

    companion object {
        private lateinit var sharedPrefs: SharedPreferences
        private val defaultLocaleString = Gson().toJson(Locale.US)

        @JvmStatic
        fun initialize(context: Context) {
            sharedPrefs = context.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
        }

        @JvmStatic
        fun language(): String? {
            return sharedPrefs.getString(
                LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY, DEFAULT_LANGUAGE_VALUE
            )
        }

        @JvmStatic
        fun getLayoutDirection(): Int {
            val isRTLKey =
                LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY + sharedPrefs.getString(
                    LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                    DEFAULT_LANGUAGE_VALUE
                )
            return if (sharedPrefs.getBoolean(isRTLKey, DEFAULT_RTL_VALUE))
                LayoutDirection.RTL else LayoutDirection.LTR
        }

        @JvmStatic
        fun locale(languageDisplayName: String): Locale {
            val localeString = sharedPrefs.getString(languageDisplayName, defaultLocaleString)
            return GsonBuilder().create().fromJson(localeString, Locale::class.java)
        }

        @JvmStatic
        fun displayLanguageName(locale: Locale): String {
            val displayName = locale.displayName
            val localeString = Gson().toJson(locale)
            sharedPrefs.edit().putString(displayName, localeString).apply()
            return displayName
        }

        @JvmStatic
        fun getRemoteParticipantPersonaInjectionSelection(): Boolean {
            return sharedPrefs.getBoolean(DEFAULT_PERSONA_INJECTION_VALUE_PREF_KEY, false)
        }

        @JvmStatic
        fun getParticipantViewData(context: Context): CallWithChatCompositeParticipantViewData? {
            val displayName = sharedPrefs.getString(RENDERED_DISPLAY_NAME, "")
            val avatarImageName = sharedPrefs.getString(AVATAR_IMAGE, "")
            var avatarImageBitmap: Bitmap? = null
            avatarImageName?.let {
                if (it.isNotEmpty()) {
                    avatarImageBitmap = BitmapFactory.decodeResource(context.resources, it.toInt())
                }
            }

            if (!displayName.isNullOrEmpty() || avatarImageBitmap != null)
                return CallWithChatCompositeParticipantViewData()
                    .setDisplayName(displayName)
                    .setAvatarBitmap(avatarImageBitmap)

            return null
        }

        @JvmStatic
        fun getTitle(): String? = sharedPrefs.getString(CALL_TITLE, null)

        @JvmStatic
        fun getSubtitle(): String? = sharedPrefs.getString(CALL_SUBTITLE, null)
    }
}
