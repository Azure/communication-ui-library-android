// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling.models

import com.azure.android.core.util.ExpandableStringEnum

/**
 * Defines values for CallCompositeEventCode.
 */
internal class CallCompositeEventCode : ExpandableStringEnum<CallCompositeEventCode?>() {
    companion object {
        val CALL_EVICTED = fromString("callEvicted", CallCompositeEventCode::class.java)
        val CALL_DECLINED = fromString("callDeclined", CallCompositeEventCode::class.java)
    }

    /**
     * Creates or finds a CallCompositeEventCode from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding {@link CallCompositeEventCode}.
     */
    private fun fromString(name: String): CallCompositeEventCode {
        return fromString(name, CallCompositeEventCode::class.java)
    }

    /**
     * @return known CallCompositeEventCode values.
     */
    fun values(): Collection<CallCompositeEventCode> {
        return values(CallCompositeEventCode::class.java)
    }
}
