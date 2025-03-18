package com.azure.android.communication.ui.calling.redux.state;

/**
 * Represents the state of audio focus in the application.
 */
public final class AudioFocusState {
    private final AudioFocusStatus status;
    private final String lastError;

    public AudioFocusState() {
        this(AudioFocusStatus.NONE, null);
    }

    public AudioFocusState(AudioFocusStatus status, String lastError) {
        this.status = status;
        this.lastError = lastError;
    }

    public AudioFocusStatus getStatus() {
        return status;
    }

    public String getLastError() {
        return lastError;
    }

    public AudioFocusState copy(AudioFocusStatus newStatus, String newError) {
        return new AudioFocusState(newStatus, newError);
    }

    public AudioFocusState copy(AudioFocusStatus newStatus) {
        return new AudioFocusState(newStatus, this.lastError);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AudioFocusState other = (AudioFocusState) obj;
        return status == other.status &&
                (lastError == null ? other.lastError == null : lastError.equals(other.lastError));
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (lastError != null ? lastError.hashCode() : 0);
        return result;
    }
}
