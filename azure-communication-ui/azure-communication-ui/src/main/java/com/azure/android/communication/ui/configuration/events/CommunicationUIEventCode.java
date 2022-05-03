// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration.events;


import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for CommunicationUIEventCode.
 */
public class CommunicationUIEventCode extends ExpandableStringEnum<CommunicationUIEventCode> {
    public static final CommunicationUIEventCode NOP = fromString("nop");
    public static final CommunicationUIEventCode CALL_EVICTED = fromString("callEvicted");

    /**
     * Creates or finds a CommunicationUIEventCode from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding CommunicationUIEventCode.
     */
    private static CommunicationUIEventCode fromString(final String name) {
        return fromString(name, CommunicationUIEventCode.class);
    }

    /**
     * @return known CommunicationUIEventCode values.
     */
    public static Collection<CommunicationUIEventCode> values() {
        return values(CommunicationUIEventCode.class);
    }
}
