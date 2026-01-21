package com.azure.android.communication.ui.calling.redux.reducer;

import com.azure.android.communication.ui.calling.redux.action.AudioFocusAction;
import com.azure.android.communication.ui.calling.redux.state.AudioFocusState;
import com.azure.android.communication.ui.calling.redux.state.AudioFocusStatus;

/**
 * Reducer for handling audio focus state changes.
 */
public final class AudioFocusReducer {
    private AudioFocusReducer() {
    }

    /**
     * Handles state updates based on audio focus actions.
     *
     * @param state Current audio focus state
     * @param action Action to process
     * @return Updated audio focus state
     */
    public static AudioFocusState reduce(final AudioFocusState state, final Object action) {
        if (action instanceof AudioFocusAction.RequestFocus) {
            return state.copy(AudioFocusStatus.REQUESTING);
        }

        if (action instanceof AudioFocusAction.FocusGranted) {
            return state.copy(AudioFocusStatus.APPROVED);
        }

        if (action instanceof AudioFocusAction.FocusDenied) {
            return state.copy(AudioFocusStatus.REJECTED, 
                ((AudioFocusAction.FocusDenied) action).getReason());
        }

        if (action instanceof AudioFocusAction.FocusLost) {
            AudioFocusAction.FocusLost lostAction = (AudioFocusAction.FocusLost) action;
            return state.copy(
                lostAction.isTransient() ? AudioFocusStatus.INTERRUPTED : AudioFocusStatus.NONE
            );
        }

        if (action instanceof AudioFocusAction.ReleaseFocus) {
            return state.copy(AudioFocusStatus.NONE);
        }

        if (action instanceof AudioFocusAction.FocusInterrupted) {
            return state.copy(AudioFocusStatus.INTERRUPTED, 
                ((AudioFocusAction.FocusInterrupted) action).getReason());
        }

        if (action instanceof AudioFocusAction.FocusRestored) {
            return state.copy(AudioFocusStatus.APPROVED);
        }

        return state;
    }
}
