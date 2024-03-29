// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.features

import com.azure.android.communication.ui.calling.features.interfaces.SupportFilesFeature

/**
 * Factory for getting the different UIFeatureType not available in all release channels.
 *
 * This is to allow Beta API be compiled and linked when
 * `USE_CALLING_SDK_BETA=true` is set in local.properties
 *
 * I.e. if Beta Calling SDK has a new method, getWidgets(), compile time features can
 * be used to scope as appropriately and provide fallback, stub or disabled implementations to our
 * GA branch, until the time it can be appropriately promoted (dependencies GA'd)
 *
 * Usage Instructions:
 * 1. Add a new feature interface in the features.interfaces package.
 * 2. Update build.gradle and specify your feature name (folder)
 * 3. Create a Stub Implementation
 * 4. Create a Real Implementation
 * 5. Update the ACSFeaturesFactory to register your feature
 *
 * At Compile time, gradle will choose the folder with the Stub or the Full version,
 * and will link that against your code. It does this with the FeatureList
 * for Beta and GA.
 *
 * In your code, you can use
 * ACSFeaturesFactory.instance.getACSFeature(YourFeatureInterface.class)
 * to get the feature instance.
 *
 * Features are initialized with the instance.
 */
internal class ACSFeatureFactory
/**
 * Constructor used by the factory
 */
private constructor() {
    companion object {
        /**
         * Return and instance of the required feature
         *
         * @param feature interface of the required feature
         * @param <F> type of the feature interface
         * @return requested interface feature
         </F> */
        inline fun <reified F : ACSFeature> getFeature(): F {
            return featureList[F::class.java]!! as F
        }

        private val featureList: Map<Class<*>, ACSFeature> =
            mapOf(
                SupportFilesFeature::class.java to SupportFilesFeatureImpl()
            )
    }
}
