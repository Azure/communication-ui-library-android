// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

public final class CallCompositeAVMode extends ExpandableStringEnum<CallCompositeAVMode> {
    /**
     * Normal - Audio and Video.
     */
    public static final CallCompositeAVMode NORMAL = fromString("normal");
    /**
     * Audio Only - Audio only.
     */
    public static final CallCompositeAVMode AUDIO_ONLY = fromString("audio_only");

    public static CallCompositeAVMode fromString(final String name) {
        return fromString(name, CallCompositeAVMode.class);
    }

    public static Collection<CallCompositeAVMode> values() {
        return values(CallCompositeAVMode.class);
    }
}