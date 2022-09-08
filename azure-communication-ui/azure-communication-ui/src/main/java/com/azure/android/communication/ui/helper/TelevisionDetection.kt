package com.azure.android.communication.ui.helper

import kotlin.apply

internal object TelevisionDetection {
    private var override : Boolean? = null

    // To Force TV On/Off for testing on a non-tv device
    fun debugSetIsTelevision(override: Boolean?) {
        this.override = override
    }

    // Detect if this a television running
    fun isTelevision(context : android.content.Context) : Boolean {
        override?.apply {
            return this
        }
        val uiModeManager = context.getSystemService(android.content.Context.UI_MODE_SERVICE) as android.app.UiModeManager
        return uiModeManager.currentModeType == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
    }
}