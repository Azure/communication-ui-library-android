// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for CallCompositeAudioSelectionType.
 */
public final class CallCompositeAudioSelectionType extends ExpandableStringEnum<CallCompositeAudioSelectionType> {

    /**
     * Receiver.
     */
    public static final CallCompositeAudioSelectionType RECEIVER = fromString("receiver");

    /**
     * Speaker.
     */
    public static final CallCompositeAudioSelectionType SPEAKER = fromString("speaker");

    /**
     * Bluetooth.
     */
    public static final CallCompositeAudioSelectionType BLUETOOTH = fromString("bluetooth");

    /**
     * Creates or finds a {@link CallCompositeAudioSelectionType} from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding CallCompositeAudioSelectionType.
     */
    public static CallCompositeAudioSelectionType fromString(final String name) {
        return fromString(name, CallCompositeAudioSelectionType.class);
    }

    /**
     * @return known {@link CallCompositeAudioSelectionType} values.
     */
    public static Collection<CallCompositeAudioSelectionType> values() {
        return values(CallCompositeAudioSelectionType.class);
    }
}
