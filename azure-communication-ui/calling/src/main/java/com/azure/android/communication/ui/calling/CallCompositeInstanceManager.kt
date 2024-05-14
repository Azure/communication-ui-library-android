// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKInitializer
import java.lang.IllegalStateException

internal class CallCompositeInstanceManager {

    /**
     * CallCompositeInstance Storage
     *
     * The configuration for the call requires callbacks, but these callbacks
     *  can not be passed via intent (not primitive/serializable data).
     *
     * This is a storage container for CallComposite objects, it uses a weak reference
     * to prevent CallComposite from leaking Activities via it's callbacks.
     */

    companion object {
        private val instances = mutableMapOf<Int, CallComposite>()

        /**
         * Store a Config by Instance ID
         */
        @JvmStatic
        fun putCallComposite(id: Int, callComposite: CallComposite) {
            instances[id] = callComposite
        }

        @JvmStatic
        fun removeCallComposite(id: Int) {
            instances.remove(id)
        }

        /**
         * Gets a config by it's ID
         * May return null if the Configuration becomes garbage collected
         */
        @JvmStatic
        fun getCallComposite(id: Int): CallComposite = instances[id]
            ?: throw CallCompositeException(
                "This ID is not valid, and no entry exists in the map. Please file a bug, this is an error in the composite",
                IllegalStateException()
            )

        /**
         *  Check if CallComposite exists
         */
        @JvmStatic
        fun hasCallComposite(id: Int): Boolean = instances.containsKey(id)
    }
}

internal fun CallComposite.getConfig(): CallCompositeConfiguration {
    return this.configuration
}

internal fun CallComposite.getCallingSDKInitialization(): CallingSDKInitializer {
    return this.sdkInitialization
}
