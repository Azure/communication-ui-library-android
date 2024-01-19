// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Enum representing the audio and video modes available in a call composite.
 * This enum helps in selecting between different modes like normal (audio and video) or audio-only.
 */
public final class CallCompositeAvMode extends ExpandableStringEnum<CallCompositeAvMode> {

    /**
     * The Normal mode with both audio and video.
     * This mode is used when both audio and video functionalities are required during the call.
     */
    public static final CallCompositeAvMode NORMAL = fromString("normal");

    /**
     * The Audio Only mode with audio but no video.
     * This mode is used when only the audio functionality is required during the call,
     * with video being either disabled or not needed.
     */
    public static final CallCompositeAvMode AUDIO_ONLY = fromString("audio_only");

    /**
     * Creates or finds a CallCompositeAvMode instance based on the provided name.
     *
     * @param name the name of the required CallCompositeAvMode
     * @return an instance of CallCompositeAvMode corresponding to the provided name
     */
    public static CallCompositeAvMode fromString(final String name) {
        return fromString(name, CallCompositeAvMode.class);
    }

    /**
     * Retrieves a collection of all values in the CallCompositeAvMode enum.
     *
     * @return a collection of all CallCompositeAvMode values
     */
    public static Collection<CallCompositeAvMode> values() {
        return values(CallCompositeAvMode.class);
    }
}
