/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.azure.android.communication.ui.callingcompositedemoapp.features

import android.content.Context
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_ISRTL_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_LANGUAGE_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.utilities.FEATURE_FLAG_SHARED_PREFS_KEY

class SettingsFeatures {

    companion object {
        var isLanguageFeatureEnabled: Boolean = true

        // ---------------------- Language Features -------------------------------------
        fun language(context: Context): String? {
            return context.applicationContext
                .getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
                .getString(LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY, DEFAULT_LANGUAGE_VALUE)
        }

        fun isRTL(context: Context): Boolean {
            return context.applicationContext
                .getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
                .getBoolean(LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY, DEFAULT_ISRTL_VALUE)
        }
    }
}