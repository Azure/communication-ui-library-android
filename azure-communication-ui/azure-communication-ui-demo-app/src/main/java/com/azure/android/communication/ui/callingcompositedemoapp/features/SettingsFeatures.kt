// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.features

import android.content.Context
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_ISRTL_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_LANGUAGE_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_CUSTOM_TRANSLATION_ENABLE
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.utilities.implementation.FEATURE_FLAG_SHARED_PREFS_KEY

class SettingsFeatures {

    companion object {
        var isLanguageFeatureEnabled: Boolean = false

        // Language Features
        fun language(context: Context): String? {
            return context.applicationContext
                .getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
                .getString(LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY, DEFAULT_LANGUAGE_VALUE)
        }

        fun isRTL(context: Context): Boolean {
            val isRTLKey = context.applicationContext.getSharedPreferences(
                FEATURE_FLAG_SHARED_PREFS_KEY,
                Context.MODE_PRIVATE
            )
                .getString(
                    LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                    DEFAULT_LANGUAGE_VALUE
                ) + LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY
            return context.applicationContext
                .getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
                .getBoolean(isRTLKey, DEFAULT_ISRTL_VALUE)
        }

        fun getIsLanguageFeatureEnabled(context: Context): Boolean {
            return context.applicationContext
                .getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
                .getBoolean(IS_LANGUAGE_SETTING_ENABLED, false)
        }

        fun setIsLanguageFeatureEnabled(context: Context, isFeatureEnabled: Boolean) {
            context.applicationContext
                .getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
                .edit().putBoolean(IS_LANGUAGE_SETTING_ENABLED, isFeatureEnabled).apply()
        }

        fun getIsCustomTranslationEnabled(context: Context): Boolean {
            return context.applicationContext
                .getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
                .getBoolean(LANGUAGE_CUSTOM_TRANSLATION_ENABLE, false)
        }

        fun getCustomTranslationMap(): Map<String, String> {
            return mapOf(
                "azure_communication_ui_setup_view_button_mic_on" to "মাইক অন",
                "azure_communication_ui_setup_view_button_mic_off" to "মাইক বন্ধ",
            )
        }

        fun getLanguageCode(languageCode: String): String {
            when(languageCode) {
                "SPANISH"-> return "es";
                "CHINESE_SIMPLIFIED"-> return "zh-CN";
                "RUSSIAN"-> return "ru";
                "JAPANESE"-> return "ja";
                "FRENCH"-> return "fr";
                "BRAZILIAN_PORTUGUESE"-> return "pt-BR";
                "GERMAN"-> return "de";
                "KOREAN"-> return "ko";
                "ITALIAN"-> return "it";
                "CHINESE_TRADITIONAL"-> return "zh-TW";
                "DUTCH"-> return "nl";
                "TURKISH"-> return "tr";
                "ENGLISH_UK"-> return "en-GB";
                else-> return "en";
            }
        }
    }
}

const val IS_LANGUAGE_SETTING_ENABLED = "IS_LANGUAGE_SETTING_ENABLED"
