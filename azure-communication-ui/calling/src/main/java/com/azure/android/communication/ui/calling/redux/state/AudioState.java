package com.azure.android.communication.ui.calling.redux.state;

/**
 * Represents the complete audio state for the application.
 */
public final class AudioState {
    private final AudioFocusState focusState;
    private final AudioModeState modeState;
    private final AudioDeviceState deviceState;

    public AudioState() {
        this(new AudioFocusState(), new AudioModeState(), new AudioDeviceState());
    }

    public AudioState(AudioFocusState focusState, AudioModeState modeState, AudioDeviceState deviceState) {
        this.focusState = focusState;
        this.modeState = modeState;
        this.deviceState = deviceState;
    }

    public AudioFocusState getFocusState() {
        return focusState;
    }

    public AudioModeState getModeState() {
        return modeState;
    }

    public AudioDeviceState getDeviceState() {
        return deviceState;
    }

    public AudioState copy(AudioFocusState newFocusState, AudioModeState newModeState, AudioDeviceState newDeviceState) {
        return new AudioState(
            newFocusState != null ? newFocusState : this.focusState,
            newModeState != null ? newModeState : this.modeState,
            newDeviceState != null ? newDeviceState : this.deviceState
        );
    }

    public AudioState copyWithFocusState(AudioFocusState newFocusState) {
        return copy(newFocusState, null, null);
    }

    public AudioState copyWithModeState(AudioModeState newModeState) {
        return copy(null, newModeState, null);
    }

    public AudioState copyWithDeviceState(AudioDeviceState newDeviceState) {
        return copy(null, null, newDeviceState);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AudioState other = (AudioState) obj;
        return focusState.equals(other.focusState) &&
                modeState.equals(other.modeState) &&
                deviceState.equals(other.deviceState);
    }

    @Override
    public int hashCode() {
        int result = focusState != null ? focusState.hashCode() : 0;
        result = 31 * result + (modeState != null ? modeState.hashCode() : 0);
        result = 31 * result + (deviceState != null ? deviceState.hashCode() : 0);
        return result;
    }
}
