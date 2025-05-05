package com.azure.android.communication.ui.calling.redux.state;

/**
 * Represents the state of audio mode in the application.
 */
public final class AudioModeState {
    private final AudioMode currentMode;
    private final AudioMode previousMode;
    private final String lastError;

    public AudioModeState() {
        this(AudioMode.NORMAL, null, null);
    }

    public AudioModeState(AudioMode currentMode, AudioMode previousMode, String lastError) {
        this.currentMode = currentMode;
        this.previousMode = previousMode;
        this.lastError = lastError;
    }

    public AudioMode getCurrentMode() {
        return currentMode;
    }

    public AudioMode getPreviousMode() {
        return previousMode;
    }

    public String getLastError() {
        return lastError;
    }

    public AudioModeState copy(AudioMode newMode) {
        return new AudioModeState(newMode, this.currentMode, null);
    }

    public AudioModeState copy(AudioMode newMode, String error) {
        return new AudioModeState(newMode, this.currentMode, error);
    }

    public AudioModeState copyWithError(String error) {
        return new AudioModeState(this.currentMode, this.previousMode, error);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AudioModeState other = (AudioModeState) obj;
        return currentMode == other.currentMode &&
                previousMode == other.previousMode &&
                (lastError == null ? other.lastError == null : lastError.equals(other.lastError));
    }

    @Override
    public int hashCode() {
        int result = currentMode != null ? currentMode.hashCode() : 0;
        result = 31 * result + (previousMode != null ? previousMode.hashCode() : 0);
        result = 31 * result + (lastError != null ? lastError.hashCode() : 0);
        return result;
    }
}
