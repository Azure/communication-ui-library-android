package com.azure.android.communication.ui.calling.redux.action;

import com.azure.android.communication.ui.calling.redux.state.AudioMode;

/**
 * Actions related to audio mode management.
 */
public abstract class AudioModeAction {
    private AudioModeAction() {
    }

    /**
     * Action dispatched when requesting to set a specific audio mode.
     */
    public static final class SetModeRequested {
        private final AudioMode mode;

        public SetModeRequested(AudioMode mode) {
            this.mode = mode;
        }

        public AudioMode getMode() {
            return mode;
        }
    }

    /**
     * Action dispatched when mode change is successful.
     */
    public static final class ModeChangeSucceeded {
        private final AudioMode mode;

        public ModeChangeSucceeded(AudioMode mode) {
            this.mode = mode;
        }

        public AudioMode getMode() {
            return mode;
        }
    }

    /**
     * Action dispatched when mode change fails.
     */
    public static final class ModeChangeFailed {
        private final AudioMode targetMode;
        private final AudioMode fallbackMode;
        private final String error;

        public ModeChangeFailed(AudioMode targetMode, AudioMode fallbackMode, String error) {
            this.targetMode = targetMode;
            this.fallbackMode = fallbackMode;
            this.error = error;
        }

        public AudioMode getTargetMode() {
            return targetMode;
        }

        public AudioMode getFallbackMode() {
            return fallbackMode;
        }

        public String getError() {
            return error;
        }
    }

    /**
     * Action dispatched when requesting to restore previous audio mode.
     */
    public static final class RestorePreviousModeRequested {
        public RestorePreviousModeRequested() {
        }
    }

    /**
     * Action dispatched when audio mode is forcibly changed by the system.
     */
    public static final class SystemModeChanged {
        private final AudioMode mode;

        public SystemModeChanged(AudioMode mode) {
            this.mode = mode;
        }

        public AudioMode getMode() {
            return mode;
        }
    }
}
