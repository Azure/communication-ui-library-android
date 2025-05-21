package com.azure.android.communication.ui.calling.redux.middleware;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

import com.azure.android.communication.ui.calling.redux.action.AudioFocusAction;
import com.azure.android.communication.ui.calling.redux.Store;
import com.azure.android.communication.ui.calling.redux.state.ReduxState;
import com.azure.android.communication.ui.calling.redux.Dispatch;
import com.azure.android.communication.ui.calling.redux.Middleware;

/**
 * Middleware for handling audio focus operations.
 */
public final class AudioFocusMiddleware implements Middleware<ReduxState> {
    private final AudioManager audioManager;
    private final AudioFocusHandler audioFocusHandler;
    private AudioFocusRequest audioFocusRequest;

    public AudioFocusMiddleware(Context context) {
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.audioFocusHandler = new AudioFocusHandler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

            this.audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener(audioFocusHandler)
                .build();
        }
    }

    @Override
    public Dispatch.Fn invoke(final Store<ReduxState> store) {
        audioFocusHandler.setDispatch(store::dispatch);
        return next -> action -> {
            if (action instanceof AudioFocusAction.RequestFocus) {
                return handleRequestFocus(next);
            }

            if (action instanceof AudioFocusAction.ReleaseFocus) {
                return handleReleaseFocus(next);
            }

            // Pass through all other actions
            return next.invoke(action);
        };
    }

    private Object handleRequestFocus(Dispatch next) {
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result = audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            result = audioManager.requestAudioFocus(audioFocusHandler,
                    AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN);
        }

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return next.invoke(new AudioFocusAction.FocusGranted());
        } else {
            return next.invoke(new AudioFocusAction.FocusDenied("Audio focus request denied"));
        }
    }

    private Object handleReleaseFocus(Dispatch next) {
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result = audioManager.abandonAudioFocusRequest(audioFocusRequest);
        } else {
            result = audioManager.abandonAudioFocus(audioFocusHandler);
        }

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return next.invoke(new AudioFocusAction.ReleaseFocus());
        }
        return null;
    }

    private static class AudioFocusHandler implements AudioManager.OnAudioFocusChangeListener {
        private Dispatch dispatch;

        void setDispatch(Dispatch dispatch) {
            this.dispatch = dispatch;
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (dispatch == null) return;

            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    dispatch.invoke(new AudioFocusAction.FocusLost(false));
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    dispatch.invoke(new AudioFocusAction.FocusLost(true));
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    dispatch.invoke(new AudioFocusAction.FocusInterrupted("Ducking required"));
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    dispatch.invoke(new AudioFocusAction.FocusRestored());
                    break;
            }
        }
    }
}
