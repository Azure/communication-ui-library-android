// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Event of entering and exiting Picture-in-Picture mode.
 */
public final class CallCompositePictureInPictureChangedEvent {
    private final boolean isInPictureInPicture;

    /**
     * Creates {@link CallCompositePictureInPictureChangedEvent}
     * @param isInPictureInPicture
     */
    public CallCompositePictureInPictureChangedEvent(final boolean isInPictureInPicture) {
        this.isInPictureInPicture = isInPictureInPicture;
    }

    /**
     * Is Picture-in-Picture mode entered.
     * @return
     */
    public boolean isInPictureInPicture() {
        return this.isInPictureInPicture;
    }
}
