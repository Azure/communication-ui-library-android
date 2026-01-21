package com.azure.android.communication.ui.calling.redux.state;

/**
 * Represents the status of audio focus request.
 */
public enum AudioFocusStatus {
    NONE,
    REQUESTING,
    APPROVED,
    REJECTED,
    INTERRUPTED
}
