// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration

import com.azure.android.communication.ui.calling.configuration.events.CallCompositeEventsHandler
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import java.lang.RuntimeException

internal class CallCompositeConfiguration {
    var themeConfig: Int? = null
    var localizationConfig: CallCompositeLocalizationOptions? = null
    var callCompositeEventsHandler = CallCompositeEventsHandler()
    var callConfig: CallConfiguration? = null
    var callCompositeLocalOptions: CallCompositeLocalOptions? = null
    val remoteParticipantsConfiguration: RemoteParticipantsConfiguration =
        RemoteParticipantsConfiguration()

    /*
    CallCompositeConfiguration Storage

    The configuration for the call requires callbacks, but these callbacks
    can not be passed via intent (not primitive/serializable data).

    This is a storage container for Configuration objects, it uses a weak reference
    to prevent CallCompositeConfiguration from leaking Activities via it's callbacks.
     */
    companion object {
        private val configs: HashMap<Int, CallCompositeConfiguration> = HashMap()

        // Store a Config by Instance ID
        //
        // Pass a null configuration to explicitly remove an instance
        fun putConfig(id: Int, configuration: CallCompositeConfiguration?) {
            if (configuration == null) {
                configs.remove(id)
            } else {
                configs[id] = configuration
            }
        }

        // Gets a config by it's ID
        // May return null if the Configuration becomes garbage collected
        fun getConfig(id: Int): CallCompositeConfiguration = configs[id]
            ?: throw RuntimeException(
                "This ID is not valid, and no entry exists in the map. Please file a bug, this is an error in the composite"
            )

        // Check if config exists
        fun hasConfig(id: Int): Boolean = configs[id] != null
    }
}
