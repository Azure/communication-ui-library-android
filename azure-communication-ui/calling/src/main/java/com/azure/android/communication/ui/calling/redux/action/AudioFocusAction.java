package com.azure.android.communication.ui.calling.redux.action;

/**
 * Actions related to audio focus management.
 */
public abstract class AudioFocusAction {
    private AudioFocusAction() {
    }

    /**
     * Action dispatched when requesting audio focus.
     */
    public static final class RequestFocus {
        public RequestFocus() {
        }
    }

    /**
     * Action dispatched when audio focus is granted.
     */
    public static final class FocusGranted {
        public FocusGranted() {
        }
    }

    /**
     * Action dispatched when audio focus is denied.
     */
    public static final class FocusDenied {
        private final String reason;

        public FocusDenied(String reason) {
            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }
    }

    /**
     * Action dispatched when audio focus is lost.
     */
    public static final class FocusLost {
        private final boolean isTransient;

        public FocusLost(boolean isTransient) {
            this.isTransient = isTransient;
        }

        public boolean isTransient() {
            return isTransient;
        }
    }

    /**
     * Action dispatched when requesting to release audio focus.
     */
    public static final class ReleaseFocus {
        public ReleaseFocus() {
        }
    }

    /**
     * Action dispatched when audio focus is interrupted.
     */
    public static final class FocusInterrupted {
        private final String reason;

        public FocusInterrupted(String reason) {
            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }
    }

    /**
     * Action dispatched when audio focus is restored after interruption.
     */
    public static final class FocusRestored {
        public FocusRestored() {
        }
    }
}
