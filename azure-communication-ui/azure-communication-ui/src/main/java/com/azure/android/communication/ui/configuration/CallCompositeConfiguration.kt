// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

import com.azure.android.communication.ui.configuration.events.CallCompositeEventsHandler
import java.lang.ref.WeakReference

internal class CallCompositeConfiguration {
    var themeConfig: ThemeConfiguration? = null
    var callCompositeEventsHandler = CallCompositeEventsHandler()
    var callConfig: CallConfiguration? = null

    /*
    CallCompositeConfiguration
     */
    companion object {
        private val configs : HashMap<Int, WeakReference<CallCompositeConfiguration>> = HashMap()

        /// Puts a config for later retrieval
        fun putConfig(id: Int, configuration: CallCompositeConfiguration) {
            configs[id] = WeakReference(configuration)
        }

        /// Gets a config by it's ID
        fun getConfig(id: Int) : CallCompositeConfiguration? = configs[id]?.get()
    }
}
