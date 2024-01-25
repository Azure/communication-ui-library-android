// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Represents an event that indicates a change in the Picture-in-Picture (PiP) mode status.
 * This class provides information about whether the application is currently in PiP mode.
 */
public final class CallCompositePictureInPictureChangedEvent {
    private final boolean isInPictureInPicture;

    /**
     * Constructs a new CallCompositePictureInPictureChangedEvent with the specified PiP status.
     *
     * @param isInPictureInPicture A boolean value indicating whether the app is in Picture-in-Picture mode.
     */
    public CallCompositePictureInPictureChangedEvent(final boolean isInPictureInPicture) {
        this.isInPictureInPicture = isInPictureInPicture;
    }

    /**
     * Returns the current status of Picture-in-Picture mode.
     *
     * @return true if the app is currently in Picture-in-Picture mode, false otherwise.
     */
    public boolean isInPictureInPicture() {
        return this.isInPictureInPicture;
    }
}
