// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

public final class CallCompositeAvMode extends ExpandableStringEnum<CallCompositeAvMode> {
    /**
     * Normal - Audio and Video.
     */
    public static final CallCompositeAvMode NORMAL = fromString("normal");
    /**
     * Audio Only - Audio only.
     */
    public static final CallCompositeAvMode AUDIO_ONLY = fromString("audio_only");

    public static CallCompositeAvMode fromString(final String name) {
        return fromString(name, CallCompositeAvMode.class);
    }

    public static Collection<CallCompositeAvMode> values() {
        return values(CallCompositeAvMode.class);
    }
}
