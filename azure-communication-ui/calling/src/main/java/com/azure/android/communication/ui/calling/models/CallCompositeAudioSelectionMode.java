// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for CallCompositeAudioSelectionMode.
 */
public final class CallCompositeAudioSelectionMode extends ExpandableStringEnum<CallCompositeAudioSelectionMode> {

    /**
     * Receiver.
     */
    public static final CallCompositeAudioSelectionMode RECEIVER = fromString("receiver");

    /**
     * Speaker.
     */
    public static final CallCompositeAudioSelectionMode SPEAKER = fromString("speaker");

    /**
     * Bluetooth.
     */
    public static final CallCompositeAudioSelectionMode BLUETOOTH = fromString("bluetooth");

    /**
     * Creates or finds a {@link CallCompositeAudioSelectionMode} from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding CallCompositeAudioSelectionMode.
     */
    public static CallCompositeAudioSelectionMode fromString(final String name) {
        return fromString(name, CallCompositeAudioSelectionMode.class);
    }

    /**
     * @return known {@link CallCompositeAudioSelectionMode} values.
     */
    public static Collection<CallCompositeAudioSelectionMode> values() {
        return values(CallCompositeAudioSelectionMode.class);
    }
}
