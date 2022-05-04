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
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_RTL_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.RENDERED_DISPLAY_NAME
import com.azure.android.communication.ui.callingcompositedemoapp.SETTINGS_SHARED_PREFS
import com.azure.android.communication.ui.persona.PersonaData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.Locale

class SettingsFeatures {

    companion object {
        private lateinit var sharedPrefs: SharedPreferences
        private val defaultLocaleString = Gson().toJson(Locale.US)

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
        fun getLayoutDirection(): Int {
            val isRTLKey =
                LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY + sharedPrefs.getString(
                    LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                    DEFAULT_LANGUAGE_VALUE
                )
            return if (sharedPrefs.getBoolean(isRTLKey, DEFAULT_RTL_VALUE)
            ) LayoutDirection.RTL else LayoutDirection.LTR
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
        fun getPersonaData(context: Context): PersonaData? {
            val displayName = sharedPrefs.getString(RENDERED_DISPLAY_NAME, "")
            val avatarImageName = sharedPrefs.getString(AVATAR_IMAGE, "")
            var avatarImageBitmap: Bitmap? = null
            avatarImageName?.let {
                if (it.isNotEmpty()) {
                    avatarImageBitmap = BitmapFactory.decodeResource(context.resources, it.toInt())
                }
            }

            if (!displayName.isNullOrEmpty() && avatarImageBitmap != null) {
                return PersonaData(
                    displayName,
                    avatarImageBitmap
                )
            }

            if (avatarImageBitmap != null) {
                return PersonaData(
                    avatarImageBitmap
                )
            }

            if (!displayName.isNullOrEmpty()) {
                return PersonaData(
                    displayName
                )
            }

            return null
        }
    }
}
