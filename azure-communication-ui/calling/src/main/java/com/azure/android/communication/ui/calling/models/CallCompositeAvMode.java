// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Represents the audio and video modes for a call composite.
 * This class defines the modes in which a call can operate, such as normal (audio and video) or audio-only.
 */
public final class CallCompositeAvMode extends ExpandableStringEnum<CallCompositeAvMode> {

    /**
     * The NORMAL mode where both audio and video are enabled for the call.
     */
    public static final CallCompositeAvMode NORMAL = fromString("normal");

    /**
     * The AUDIO_ONLY mode where only audio is enabled, and video is disabled for the call.
     */
    public static final CallCompositeAvMode AUDIO_ONLY = fromString("audio_only");

    /**
     * Creates an instance of {@link CallCompositeAvMode} from a string name.
     *
     * @param name The name of the mode as a string.
     * @return An instance of {@link CallCompositeAvMode} corresponding to the provided name.
     */
    public static CallCompositeAvMode fromString(final String name) {
        return fromString(name, CallCompositeAvMode.class);
    }

    /**
     * Returns all the available values for {@link CallCompositeAvMode}.
     *
     * @return A collection of all {@link CallCompositeAvMode} values.
     */
    public static Collection<CallCompositeAvMode> values() {
        return values(CallCompositeAvMode.class);
    }
}