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
import com.azure.android.communication.ui.callingcompositedemoapp.AUDIO_ONLY_MODE_ON_BY_DEFAULT_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.AUDIO_ONLY_MODE_ON_BY_DEFAULT_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.AVATAR_IMAGE
import com.azure.android.communication.ui.callingcompositedemoapp.CALL_SCREEN_ORIENTATION_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.CALL_SUBTITLE
import com.azure.android.communication.ui.callingcompositedemoapp.CALL_TITLE
import com.azure.android.communication.ui.callingcompositedemoapp.CAMERA_ON_BY_DEFAULT_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_CAMERA_ON_BY_DEFAULT_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_END_CALL_ON_BY_DEFAULT_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_LANGUAGE_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_MIC_ON_BY_DEFAULT_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_PERSONA_INJECTION_VALUE_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_RTL_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_SKIP_SETUP_SCREEN_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.ENABLE_MULTITASKING
import com.azure.android.communication.ui.callingcompositedemoapp.ENABLE_MULTITASKING_DEFAULT_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.ENABLE_PIP_WHEN_MULTITASKING
import com.azure.android.communication.ui.callingcompositedemoapp.ENABLE_PIP_WHEN_MULTITASKING_DEFAULT_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.END_CALL_ON_BY_DEFAULT_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.MIC_ON_BY_DEFAULT_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.RENDERED_DISPLAY_NAME
import com.azure.android.communication.ui.callingcompositedemoapp.SETTINGS_SHARED_PREFS
import com.azure.android.communication.ui.callingcompositedemoapp.SETUP_SCREEN_ORIENTATION_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.SKIP_SETUP_SCREEN_VALUE_KEY
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.Locale

class SettingsFeatures {
    companion object {
        private lateinit var sharedPrefs: SharedPreferences
        private val defaultLocaleString = Gson().toJson(Locale.US)

        fun initialize(context: Context) {
            sharedPrefs = context.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
        }

        fun language(): String? {
            return sharedPrefs.getString(
                LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                null,
            )
        }

        fun getLayoutDirection(): Int? {
            val isRTLKey =
                LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY +
                    sharedPrefs.getString(
                        LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                        DEFAULT_LANGUAGE_VALUE,
                    )
            return if (sharedPrefs.contains(isRTLKey)) {
                if (sharedPrefs.getBoolean(isRTLKey, DEFAULT_RTL_VALUE)) {
                    LayoutDirection.RTL
                } else {
                    LayoutDirection.LTR
                }
            } else {
                null
            }
        }

        fun locale(languageDisplayName: String?): Locale? {
            if (languageDisplayName == null) {
                return null
            }

            val localeString = sharedPrefs.getString(languageDisplayName, null)
            return if (localeString != null) {
                GsonBuilder().create().fromJson(localeString, Locale::class.java)
            } else {
                null
            }
        }

        fun orientation(orientationDisplayName: String?): CallCompositeSupportedScreenOrientation? {
            return orientationDisplayName?.let {
                CallCompositeSupportedScreenOrientation.fromString(orientationDisplayName)
            }
        }

        fun displayLanguageName(locale: Locale): String {
            val displayName = locale.displayName
            val localeString = Gson().toJson(locale)
            sharedPrefs.edit().putString(displayName, localeString).apply()
            return displayName
        }

        fun displayOrientationName(orientation: CallCompositeSupportedScreenOrientation): String {
            return orientation.toString()
        }

        fun getRemoteParticipantPersonaInjectionSelection(): Boolean {
            return sharedPrefs.getBoolean(DEFAULT_PERSONA_INJECTION_VALUE_PREF_KEY, false)
        }

        fun getSkipSetupScreenFeatureOption(): Boolean? {
            return if (sharedPrefs.contains(SKIP_SETUP_SCREEN_VALUE_KEY)) {
                sharedPrefs.getBoolean(SKIP_SETUP_SCREEN_VALUE_KEY, DEFAULT_SKIP_SETUP_SCREEN_VALUE)
            } else {
                null
            }
        }

        fun getMicOnByDefaultOption(): Boolean? {
            return if (sharedPrefs.contains(MIC_ON_BY_DEFAULT_KEY)) {
                sharedPrefs.getBoolean(MIC_ON_BY_DEFAULT_KEY, DEFAULT_MIC_ON_BY_DEFAULT_VALUE)
            } else {
                null
            }
        }

        fun getCameraOnByDefaultOption(): Boolean? {
            return if (sharedPrefs.contains(CAMERA_ON_BY_DEFAULT_KEY)) {
                sharedPrefs.getBoolean(CAMERA_ON_BY_DEFAULT_KEY, DEFAULT_CAMERA_ON_BY_DEFAULT_VALUE)
            } else {
                null
            }
        }

        fun getAudioOnlyByDefaultOption(): Boolean? {
            return if (sharedPrefs.contains(AUDIO_ONLY_MODE_ON_BY_DEFAULT_KEY)) {
                sharedPrefs.getBoolean(AUDIO_ONLY_MODE_ON_BY_DEFAULT_KEY, AUDIO_ONLY_MODE_ON_BY_DEFAULT_VALUE)
            } else {
                null
            }
        }

        fun getEndCallOnByDefaultOption(): Boolean {
            return sharedPrefs.getBoolean(END_CALL_ON_BY_DEFAULT_KEY, DEFAULT_END_CALL_ON_BY_DEFAULT_VALUE)
        }

        fun getParticipantViewData(context: Context): CallCompositeParticipantViewData? {
            val displayName = sharedPrefs.getString(RENDERED_DISPLAY_NAME, "")
            val avatarImageName = sharedPrefs.getString(AVATAR_IMAGE, "")
            var avatarImageBitmap: Bitmap? = null
            avatarImageName?.let {
                if (it.isNotEmpty()) {
                    avatarImageBitmap = BitmapFactory.decodeResource(context.resources, it.toInt())
                }
            }

            if (!displayName.isNullOrEmpty() || avatarImageBitmap != null) {
                return CallCompositeParticipantViewData()
                    .setDisplayName(displayName)
                    .setAvatarBitmap(avatarImageBitmap)
            }

            return null
        }

        fun getTitle(): String? = sharedPrefs.getString(CALL_TITLE, null)

        fun getSubtitle(): String? = sharedPrefs.getString(CALL_SUBTITLE, null)

        fun callScreenOrientation(): String? =
            sharedPrefs.getString(
                CALL_SCREEN_ORIENTATION_SHARED_PREF_KEY,
                null,
            )

        fun setupScreenOrientation(): String? =
            sharedPrefs.getString(
                SETUP_SCREEN_ORIENTATION_SHARED_PREF_KEY,
                null,
            )

        fun enableMultitasking(): Boolean? {
            return if (sharedPrefs.contains(ENABLE_MULTITASKING)) {
                sharedPrefs.getBoolean(ENABLE_MULTITASKING, ENABLE_MULTITASKING_DEFAULT_VALUE)
            } else {
                null
            }
        }

        fun enablePipWhenMultitasking(): Boolean? {
            return if (sharedPrefs.contains(ENABLE_PIP_WHEN_MULTITASKING)) {
                sharedPrefs.getBoolean(ENABLE_PIP_WHEN_MULTITASKING, ENABLE_PIP_WHEN_MULTITASKING_DEFAULT_VALUE)
            } else {
                null
            }
        }
    }
}
