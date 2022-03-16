// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.features

import android.content.Context
import com.azure.android.communication.ui.callingcompositedemoapp.*
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
            val customTranslationMap: Map<String, String> = HashMap()
            (customTranslationMap as HashMap<String, String>)["azure_communication_ui_setup_view_button_mic_on"] = "মাইক অন"
            (customTranslationMap as HashMap<String, String>)["azure_communication_ui_setup_view_button_mic_off"] = "মাইক বন্ধ"
            return customTranslationMap
        }
    }
}

const val IS_LANGUAGE_SETTING_ENABLED = "IS_LANGUAGE_SETTING_ENABLED"
