// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

public final class CallCompositeControlCode extends ExpandableStringEnum<CallCompositeControlCode> {
    public static final CallCompositeControlCode CAMERA_CONTROL = fromString("CAMERA_CONTROL");
    public static final CallCompositeControlCode MIC_CONTROL = fromString("MIC_CONTROL");
    public static final CallCompositeControlCode AUDIO_CONTROL = fromString("AUDIO_CONTROL");
    public static final CallCompositeControlCode HANGUP_CONTROL = fromString("HANGUP_CONTROL");

    private static CallCompositeControlCode fromString(final String name) {
        return fromString(name, CallCompositeControlCode.class);
    }
}
