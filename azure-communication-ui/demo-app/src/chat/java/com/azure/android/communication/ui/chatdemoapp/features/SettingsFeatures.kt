// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp.features

import android.content.Context
import android.content.SharedPreferences
import android.util.LayoutDirection
import com.azure.android.communication.ui.chatdemoapp.DEFAULT_LANGUAGE_VALUE
import com.azure.android.communication.ui.chatdemoapp.DEFAULT_RTL_VALUE
import com.azure.android.communication.ui.chatdemoapp.LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.chatdemoapp.LANGUAGE_IS_RTL_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.chatdemoapp.SETTINGS_SHARED_PREFS
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
                LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                DEFAULT_LANGUAGE_VALUE,
            )
        }

        @JvmStatic
        fun getLayoutDirection(): Int {
            val isRTLKey =
                LANGUAGE_IS_RTL_VALUE_SHARED_PREF_KEY +
                    sharedPrefs.getString(
                        LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                        DEFAULT_LANGUAGE_VALUE,
                    )
            return if (sharedPrefs.getBoolean(
                    isRTLKey,
                    DEFAULT_RTL_VALUE,
                )
            ) {
                LayoutDirection.RTL
            } else {
                LayoutDirection.LTR
            }
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
    }
}
