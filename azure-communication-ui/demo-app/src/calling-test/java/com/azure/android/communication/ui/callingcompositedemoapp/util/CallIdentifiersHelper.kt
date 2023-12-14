// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.util

import com.azure.android.communication.ui.callingcompositedemoapp.BuildConfig
import java.util.UUID

class CallIdentifiersHelper {
    companion object {
        fun getGroupId() = UUID.randomUUID().toString()
        fun getACSToken(): String {
            return BuildConfig.ACS_TOKEN
        }
    }
}
