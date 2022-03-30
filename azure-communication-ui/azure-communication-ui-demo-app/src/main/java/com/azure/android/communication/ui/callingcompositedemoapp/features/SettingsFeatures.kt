// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.features

import android.content.Context
import android.util.LayoutDirection
import com.azure.android.communication.ui.callingcompositedemoapp.BaseApplication
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_LANGUAGE_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_LOCALE_CODE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_RTL_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.utilities.implementation.FEATURE_FLAG_SHARED_PREFS_KEY
import java.util.Locale

class SettingsFeatures {

    companion object {

        fun language(): String? {
            return BaseApplication.getInstance()
                .getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
                .getString(LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY, DEFAULT_LANGUAGE_VALUE)
        }

        fun isRTL(): Int {
            val isRTLKey =
                LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY + BaseApplication.getInstance().getSharedPreferences(
                    FEATURE_FLAG_SHARED_PREFS_KEY,
                    Context.MODE_PRIVATE
                )
                    .getString(
                        LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                        DEFAULT_LANGUAGE_VALUE
                    )
            return if (BaseApplication.getInstance()
                .getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
                .getBoolean(isRTLKey, DEFAULT_RTL_VALUE)
            ) LayoutDirection.RTL else LayoutDirection.LTR
        }

        fun getLanguageCode(languageDisplayName: String): String? {
            return BaseApplication.getInstance()
                .getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
                .getString(languageDisplayName, DEFAULT_LOCALE_CODE)
        }

        fun getDisplayLanguageName(languageCode: String): String {
            val displayName = Locale.forLanguageTag(languageCode).displayName
            BaseApplication.getInstance()
                .getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE).edit()
                .putString(displayName, languageCode).apply()
            return displayName
        }
    }
}
