// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.helper

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration

internal object TelevisionDetection {
    private var override : Boolean? = null

    // To Force TV On/Off for testing on a non-tv device
    fun debugSetIsTelevision(override: Boolean?) {
        this.override = override
    }

    // Detect if this a television running
    fun isTelevision(context : Context) : Boolean {
        override?.apply {
            return this
        }
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    }
}