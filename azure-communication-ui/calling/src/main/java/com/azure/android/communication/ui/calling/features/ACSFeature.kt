// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.features

/**
 * Expose common actions between the ACS features
 *
 * @author yassirb@microsoft.com
 */
internal interface ACSFeature {
    /**
     * Return feature availability
     *
     * @return true if available on the current release otherwise false
     */
    val isAvailable : Boolean
}
