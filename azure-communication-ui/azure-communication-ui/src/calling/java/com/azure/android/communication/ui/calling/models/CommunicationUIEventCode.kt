// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling.models

import com.azure.android.core.util.ExpandableStringEnum

/**
 * Defines values for CommunicationUIEventCode.
 */
internal class CommunicationUIEventCode : ExpandableStringEnum<CommunicationUIEventCode?>() {
    companion object {
        val CALL_EVICTED = fromString("callEvicted", CommunicationUIEventCode::class.java)
    }

    /**
     * Creates or finds a CommunicationUIEventCode from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding CommunicationUIEventCode.
     */
    private fun fromString(name: String): CommunicationUIEventCode {
        return fromString(name, CommunicationUIEventCode::class.java)
    }

    /**
     * @return known CommunicationUIEventCode values.
     */
    fun values(): Collection<CommunicationUIEventCode> {
        return values(CommunicationUIEventCode::class.java)
    }
}
