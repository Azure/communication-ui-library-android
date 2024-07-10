// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.text.TextUtils
import android.widget.LinearLayout.LAYOUT_DIRECTION_RTL
import java.util.Locale

internal class LocaleHelper {
    companion object {
        fun getLocaleDisplayName(localeCode: String?): String? {
            if (localeCode == null) {
                return null
            }
            val parts = localeCode.split("-")
            val locale = if (parts.size == 2) {
                Locale(parts[0], parts[1])
            } else {
                Locale(localeCode)
            }
            return locale.getDisplayName(locale)
        }

        fun isRTL(localeCode: String?): Boolean {
            if (localeCode == null) {
                return false
            }
            val parts = localeCode.split("-")
            val locale = if (parts.size == 2) {
                Locale(parts[0], parts[1])
            } else {
                Locale(localeCode)
            }
            return TextUtils.getLayoutDirectionFromLocale(locale) == LAYOUT_DIRECTION_RTL
        }
    }
}
