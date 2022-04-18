// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.features

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LayoutDirection
import com.azure.android.communication.ui.callingcompositedemoapp.AVATAR_IMAGE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_LANGUAGE_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_LOCALE_CODE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_RTL_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.RENDERED_DISPLAY_NAME
import com.azure.android.communication.ui.callingcompositedemoapp.SETTINGS_SHARED_PREFS
import com.azure.android.communication.ui.configuration.LanguageCode
import com.azure.android.communication.ui.persona.CommunicationUIPersonaData
import java.util.Locale

class SettingsFeatures {

    companion object {
        private lateinit var sharedPrefs: SharedPreferences

        @JvmStatic
        fun initialize(context: Context) {
            sharedPrefs = context.getSharedPreferences(
                SETTINGS_SHARED_PREFS,
                Context.MODE_PRIVATE
            )
        }

        @JvmStatic
        fun language(): String? {
            return sharedPrefs.getString(
                LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                DEFAULT_LANGUAGE_VALUE
            )
        }

        @JvmStatic
        fun isRTL(): Int {
            val isRTLKey =
                LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY + sharedPrefs.getString(
                    LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                    DEFAULT_LANGUAGE_VALUE
                )
            return if (sharedPrefs.getBoolean(isRTLKey, DEFAULT_RTL_VALUE)
            ) LayoutDirection.RTL else LayoutDirection.LTR
        }

        @JvmStatic
        fun languageCode(languageDisplayName: String): String? {
            return sharedPrefs.getString(languageDisplayName, DEFAULT_LOCALE_CODE)
        }

        @JvmStatic
        fun displayLanguageName(languageCode: String): String {
            val displayName = Locale.forLanguageTag(languageCode).displayName
            sharedPrefs.edit().putString(displayName, languageCode).apply()
            return displayName
        }

        @JvmStatic
        fun selectedLanguageCode(languageCode: String): LanguageCode {
            for (language in LanguageCode.values()) {
                if (languageCode == language.toString()) {
                    return language
                }
            }
            return LanguageCode.ENGLISH_US
        }

        @JvmStatic
        fun getCommunicationUIPersonaData(context: Context): CommunicationUIPersonaData? {
            val displayName = sharedPrefs.getString(RENDERED_DISPLAY_NAME, "")
            val avatarImageName = sharedPrefs.getString(AVATAR_IMAGE, "")
            var avatarImageBitmap: Bitmap? = null
            avatarImageName?.let {
                if (it.isNotEmpty()) {
                    avatarImageBitmap = BitmapFactory.decodeResource(context.resources, it.toInt())
                }
            }

            if (!displayName.isNullOrEmpty() && avatarImageBitmap != null) {
                return CommunicationUIPersonaData(displayName, avatarImageBitmap)
            }

            if (avatarImageBitmap != null) {
                return CommunicationUIPersonaData(avatarImageBitmap)
            }

            if (!displayName.isNullOrEmpty()) {
                return CommunicationUIPersonaData(displayName)
            }

            return null
        }
    }
}
