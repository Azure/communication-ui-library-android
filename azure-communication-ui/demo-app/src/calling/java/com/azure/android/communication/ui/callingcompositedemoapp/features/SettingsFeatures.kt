// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.features

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LayoutDirection
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedScreenOrientation
import com.azure.android.communication.ui.callingcompositedemoapp.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.Locale

class SettingsFeatures {

    companion object {
        private lateinit var sharedPrefs: SharedPreferences
        private val defaultLocaleString = Gson().toJson(Locale.US)
        private val defaultOrientationString = Gson()
            .toJson(CallCompositeSupportedScreenOrientation.PORTRAIT)

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
        fun orientation(orientationDisplayName: String): CallCompositeSupportedScreenOrientation {
            val orientationString = sharedPrefs.getString(orientationDisplayName, defaultOrientationString)
            return GsonBuilder().create()
                .fromJson(orientationString, CallCompositeSupportedScreenOrientation::class.java)
        }

        @JvmStatic
        fun displayLanguageName(locale: Locale): String {
            val displayName = locale.displayName
            val localeString = Gson().toJson(locale)
            sharedPrefs.edit().putString(displayName, localeString).apply()
            return displayName
        }

        @JvmStatic
        fun displayOrientationName(orientation: CallCompositeSupportedScreenOrientation): String {
            val displayName = orientation.name
            val orientationString = Gson().toJson(orientation)
            sharedPrefs.edit().putString(displayName, orientationString).apply()
            return displayName
        }

        @JvmStatic
        fun getRemoteParticipantPersonaInjectionSelection(): Boolean {
            return sharedPrefs.getBoolean(DEFAULT_PERSONA_INJECTION_VALUE_PREF_KEY, false)
        }

        @JvmStatic
        fun getSkipSetupScreenFeatureOption(): Boolean {
            return sharedPrefs.getBoolean(SKIP_SETUP_SCREEN_VALUE_KEY, DEFAULT_SKIP_SETUP_SCREEN_VALUE)
        }

        @JvmStatic
        fun getMicOnByDefaultOption(): Boolean {
            return sharedPrefs.getBoolean(MIC_ON_BY_DEFAULT_KEY, DEFAULT_MIC_ON_BY_DEFAULT_VALUE)
        }

        @JvmStatic
        fun getCameraOnByDefaultOption(): Boolean {
            return sharedPrefs.getBoolean(CAMERA_ON_BY_DEFAULT_KEY, DEFAULT_CAMERA_ON_BY_DEFAULT_VALUE)
        }

        @JvmStatic
        fun getParticipantViewData(context: Context): CallCompositeParticipantViewData? {
            val displayName = sharedPrefs.getString(RENDERED_DISPLAY_NAME, "")
            val avatarImageName = sharedPrefs.getString(AVATAR_IMAGE, "")
            var avatarImageBitmap: Bitmap? = null
            avatarImageName?.let {
                if (it.isNotEmpty()) {
                    avatarImageBitmap = BitmapFactory.decodeResource(context.resources, it.toInt())
                }
            }

            if (!displayName.isNullOrEmpty() || avatarImageBitmap != null)
                return CallCompositeParticipantViewData()
                    .setDisplayName(displayName)
                    .setAvatarBitmap(avatarImageBitmap)

            return null
        }

        @JvmStatic
        fun getTitle(): String? = sharedPrefs.getString(CALL_TITLE, null)

        @JvmStatic
        fun getSubtitle(): String? = sharedPrefs.getString(CALL_SUBTITLE, null)

        @JvmStatic
        fun callScreenOrientation(): String? {
            return sharedPrefs.getString(
                CALL_SCREEN_ORIENTATION_SHARED_PREF_KEY,
                DEFAULT_CALL_SCREEN_ORIENTATION_VALUE
            )
        }

        @JvmStatic
        fun setupScreenOrientation(): String? {
            return sharedPrefs.getString(
                SETUP_SCREEN_ORIENTATION_SHARED_PREF_KEY,
                DEFAULT_SETUP_SCREEN_ORIENTATION_VALUE
            )
        }
    }
}
