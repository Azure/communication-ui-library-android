// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

public final class CallCompositeAudioDevice extends ExpandableStringEnum<CallCompositeAudioDevice> {

    public static final CallCompositeAudioDevice SPEAKER = fromString("SPEAKER");

    public static final CallCompositeAudioDevice EARPEACE = fromString("EARPEACE");

    public static final CallCompositeAudioDevice BLUETOOTH = fromString("BLUETOOTH");

    public static CallCompositeAudioDevice fromString(final String name) {
        return fromString(name, CallCompositeAudioDevice.class);
    }

    /**
     * @return known {@link CallCompositeAudioDevice} values.
     */
    public static Collection<CallCompositeAudioDevice> values() {
        return values(CallCompositeAudioDevice.class);
    }
}
