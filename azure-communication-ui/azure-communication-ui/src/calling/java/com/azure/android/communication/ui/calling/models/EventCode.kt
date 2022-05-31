// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling.models

import com.azure.android.core.util.ExpandableStringEnum

/**
 * Defines values for EventCode.
 */
internal class EventCode : ExpandableStringEnum<EventCode?>() {
    companion object {
        val CALL_EVICTED = fromString("callEvicted", EventCode::class.java)
        val CALL_DECLINED = fromString("callDeclined", EventCode::class.java)
    }

    /**
     * Creates or finds a EventCode from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding EventCode.
     */
    private fun fromString(name: String): EventCode {
        return fromString(name, EventCode::class.java)
    }

    /**
     * @return known EventCode values.
     */
    fun values(): Collection<EventCode> {
        return values(EventCode::class.java)
    }
}
