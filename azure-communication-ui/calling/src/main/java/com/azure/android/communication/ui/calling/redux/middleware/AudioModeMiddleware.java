package com.azure.android.communication.ui.calling.redux.middleware;

import android.content.Context;
import android.media.AudioManager;

import com.azure.android.communication.ui.calling.redux.action.AudioModeAction;
import com.azure.android.communication.ui.calling.redux.state.AudioMode;
import com.azure.android.communication.ui.calling.redux.Store;
import com.azure.android.communication.ui.calling.redux.state.ReduxState;
import com.azure.android.communication.ui.calling.redux.Dispatch;
import com.azure.android.communication.ui.calling.redux.Middleware;

/**
 * Middleware for handling audio mode operations.
 */
public final class AudioModeMiddleware implements Middleware<ReduxState> {
    private final AudioManager audioManager;

    public AudioModeMiddleware(Context context) {
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public Dispatch.Fn invoke(final Store<ReduxState> store) {
        return next -> action -> {
            if (action instanceof AudioModeAction.SetModeRequested) {
                return handleSetMode((AudioModeAction.SetModeRequested) action, store, next);
            }

            if (action instanceof AudioModeAction.RestorePreviousModeRequested) {
                return handleRestoreMode(store, next);
            }

            // Pass through all other actions
            return next.invoke(action);
        };
    }

    private Object handleSetMode(
            AudioModeAction.SetModeRequested action,
            Store<ReduxState> store,
            Dispatch next) {
        AudioMode targetMode = action.getMode();
        AudioMode currentMode = store.getCurrentState().getAudioState().getModeState().getCurrentMode();

        if (currentMode == targetMode) {
            return null;
        }

        try {
            int androidAudioMode = convertToAndroidAudioMode(targetMode);
            audioManager.setMode(androidAudioMode);
            return next.invoke(new AudioModeAction.ModeChangeSucceeded(targetMode));
        } catch (Exception e) {
            return next.invoke(new AudioModeAction.ModeChangeFailed(
                targetMode,
                currentMode,
                "Failed to set audio mode: " + e.getMessage()
            ));
        }
    }

    private Object handleRestoreMode(Store<ReduxState> store, Dispatch next) {
        AudioMode previousMode = store.getCurrentState().getAudioState().getModeState().getPreviousMode();
        if (previousMode == null) {
            return null;
        }

        try {
            int androidAudioMode = convertToAndroidAudioMode(previousMode);
            audioManager.setMode(androidAudioMode);
            return next.invoke(new AudioModeAction.ModeChangeSucceeded(previousMode));
        } catch (Exception e) {
            return next.invoke(new AudioModeAction.ModeChangeFailed(
                previousMode,
                store.getCurrentState().getAudioState().getModeState().getCurrentMode(),
                "Failed to restore audio mode: " + e.getMessage()
            ));
        }
    }

    private int convertToAndroidAudioMode(AudioMode mode) {
        switch (mode) {
            case NORMAL:
                return AudioManager.MODE_NORMAL;
            case IN_CALL:
                return AudioManager.MODE_IN_CALL;
            case IN_COMMUNICATION:
                return AudioManager.MODE_IN_COMMUNICATION;
            case RINGTONE:
                return AudioManager.MODE_RINGTONE;
            default:
                return AudioManager.MODE_NORMAL;
        }
    }
}
