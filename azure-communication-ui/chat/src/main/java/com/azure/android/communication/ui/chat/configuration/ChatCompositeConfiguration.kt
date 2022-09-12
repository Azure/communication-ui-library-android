// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.configuration

import com.azure.android.communication.ui.chat.ChatCompositeException
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalizationOptions
import java.lang.IllegalStateException

internal class ChatCompositeConfiguration {
    var localizationConfig: ChatCompositeLocalizationOptions? = null
    var chatConfig: ChatConfiguration? = null

    /*
    ChatCompositeConfiguration Storage

    The configuration for the chat requires callbacks, but these callbacks
    can not be passed via intent (not primitive/serializable data).

    This is a storage container for Configuration objects, it uses a weak reference
    to prevent ChatCompositeConfiguration from leaking Activities via it's callbacks.
     */
    companion object {
        private val configs: HashMap<Int, ChatCompositeConfiguration> = HashMap()

        // Store a Config by Instance ID
        //
        // Pass a null configuration to explicitly remove an instance
        fun putConfig(id: Int, configuration: ChatCompositeConfiguration?) {
            if (configuration == null) {
                configs.remove(id)
            } else {
                configs[id] = configuration
            }
        }

        // Gets a config by it's ID
        // May return null if the Configuration becomes garbage collected
        fun getConfig(id: Int): ChatCompositeConfiguration = configs[id]
            ?: throw ChatCompositeException(
                "This ID is not valid, and no entry exists in the map. Please file a bug, this is an error in the composite",
                IllegalStateException()
            )

        // Check if config exists
        fun hasConfig(id: Int): Boolean = configs[id] != null
    }
}
