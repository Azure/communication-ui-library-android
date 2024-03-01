// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Represents the audio and video modes for a call composite.
 * This class defines the modes in which a call can operate, such as normal (audio and video) or audio-only.
 */
public final class CallCompositeAudioVideoMode extends ExpandableStringEnum<CallCompositeAudioVideoMode> {

    /**
     * The NORMAL mode where both audio and video are enabled for the call.
     */
    public static final CallCompositeAudioVideoMode AUDIO_AND_VIDEO = fromString("audio_and_video");

    /**
     * The AUDIO_ONLY mode where only audio is enabled, and video is disabled for the call.
     * Note: Content Sharing/Screen Share will continue to work.
     */
    public static final CallCompositeAudioVideoMode AUDIO_ONLY = fromString("audio_only");

    /**
     * Creates an instance of {@link CallCompositeAudioVideoMode} from a string name.
     *
     * @param name The name of the mode as a string.
     * @return An instance of {@link CallCompositeAudioVideoMode} corresponding to the provided name.
     */
    public static CallCompositeAudioVideoMode fromString(final String name) {
        return fromString(name, CallCompositeAudioVideoMode.class);
    }

    /**
     * Returns all the available values for {@link CallCompositeAudioVideoMode}.
     *
     * @return A collection of all {@link CallCompositeAudioVideoMode} values.
     */
    public static Collection<CallCompositeAudioVideoMode> values() {
        return values(CallCompositeAudioVideoMode.class);
    }
}
