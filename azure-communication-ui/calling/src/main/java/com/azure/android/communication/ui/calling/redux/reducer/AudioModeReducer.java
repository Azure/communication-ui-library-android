package com.azure.android.communication.ui.calling.redux.reducer;

import com.azure.android.communication.ui.calling.redux.action.AudioModeAction;
import com.azure.android.communication.ui.calling.redux.state.AudioModeState;

/**
 * Reducer for handling audio mode state changes.
 */
public final class AudioModeReducer {
    private AudioModeReducer() {
    }

    /**
     * Handles state updates based on audio mode actions.
     *
     * @param state Current audio mode state
     * @param action Action to process
     * @return Updated audio mode state
     */
    public static AudioModeState reduce(final AudioModeState state, final Object action) {
        if (action instanceof AudioModeAction.SetModeRequested) {
            return state.copy(((AudioModeAction.SetModeRequested) action).getMode());
        }

        if (action instanceof AudioModeAction.ModeChangeSucceeded) {
            return state.copy(((AudioModeAction.ModeChangeSucceeded) action).getMode());
        }

        if (action instanceof AudioModeAction.ModeChangeFailed) {
            AudioModeAction.ModeChangeFailed failedAction = (AudioModeAction.ModeChangeFailed) action;
            return state.copy(failedAction.getFallbackMode(), failedAction.getError());
        }

        if (action instanceof AudioModeAction.RestorePreviousModeRequested) {
            if (state.getPreviousMode() != null) {
                return state.copy(state.getPreviousMode());
            }
            return state;
        }

        if (action instanceof AudioModeAction.SystemModeChanged) {
            return state.copy(((AudioModeAction.SystemModeChanged) action).getMode());
        }

        return state;
    }
}
