package com.azure.android.communication.ui.calling.redux.middleware;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import com.azure.android.communication.ui.calling.redux.Store;
import com.azure.android.communication.ui.calling.redux.action.AudioFocusAction;
import com.azure.android.communication.ui.calling.redux.state.ReduxState;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AudioFocusMiddlewareTest {
    @Mock
    private Context mockContext;

    @Mock
    private AudioManager mockAudioManager;

    @Mock
    private Store<ReduxState> mockStore;

    private AudioFocusMiddleware middleware;
    private ArgumentCaptor<AudioManager.OnAudioFocusChangeListener> listenerCaptor;

    @Before
    public void setup() {
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);
        listenerCaptor = ArgumentCaptor.forClass(AudioManager.OnAudioFocusChangeListener.class);
        middleware = new AudioFocusMiddleware(mockContext);
    }

    @Test
    public void requestFocus_whenGranted_shouldDispatchGrantedAction() {
        // Arrange
        when(mockAudioManager.requestAudioFocus(any(), anyInt(), anyInt()))
            .thenReturn(AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
        AudioFocusAction.RequestFocus action = new AudioFocusAction.RequestFocus();

        // Act
        middleware.invoke(mockStore).apply(next -> action).invoke(action);

        // Assert
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            verify(mockAudioManager).requestAudioFocus(any());
        } else {
            verify(mockAudioManager).requestAudioFocus(any(), eq(AudioManager.STREAM_VOICE_CALL),
                    eq(AudioManager.AUDIOFOCUS_GAIN));
        }
        verify(next).invoke(any(AudioFocusAction.FocusGranted.class));
    }

    @Test
    public void requestFocus_whenDenied_shouldDispatchDeniedAction() {
        // Arrange
        when(mockAudioManager.requestAudioFocus(any(), anyInt(), anyInt()))
            .thenReturn(AudioManager.AUDIOFOCUS_REQUEST_FAILED);
        AudioFocusAction.RequestFocus action = new AudioFocusAction.RequestFocus();

        // Act
        middleware.invoke(mockStore).apply(next -> action).invoke(action);

        // Assert
        verify(next).invoke(any(AudioFocusAction.FocusDenied.class));
    }

    @Test
    public void releaseFocus_whenSuccessful_shouldDispatchReleaseAction() {
        // Arrange
        when(mockAudioManager.abandonAudioFocus(any()))
            .thenReturn(AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
        AudioFocusAction.ReleaseFocus action = new AudioFocusAction.ReleaseFocus();

        // Act
        middleware.invoke(mockStore).apply(next -> action).invoke(action);

        // Assert
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            verify(mockAudioManager).abandonAudioFocusRequest(any());
        } else {
            verify(mockAudioManager).abandonAudioFocus(any());
        }
        verify(next).invoke(any(AudioFocusAction.ReleaseFocus.class));
    }

    @Test
    public void onAudioFocusChange_whenLossFocusPermanent_shouldDispatchLostAction() {
        // Arrange
        captureAudioFocusListener();

        // Act
        listenerCaptor.getValue().onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS);

        // Assert
        verify(mockStore).dispatch(any(AudioFocusAction.FocusLost.class));
    }

    @Test
    public void onAudioFocusChange_whenLossFocusTransient_shouldDispatchLostTransientAction() {
        // Arrange
        captureAudioFocusListener();

        // Act
        listenerCaptor.getValue().onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT);

        // Assert
        verify(mockStore).dispatch(any(AudioFocusAction.FocusLost.class));
    }

    @Test
    public void onAudioFocusChange_whenLossFocusCanDuck_shouldDispatchInterruptedAction() {
        // Arrange
        captureAudioFocusListener();

        // Act
        listenerCaptor.getValue().onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK);

        // Assert
        verify(mockStore).dispatch(any(AudioFocusAction.FocusInterrupted.class));
    }

    @Test
    public void onAudioFocusChange_whenGainFocus_shouldDispatchRestoredAction() {
        // Arrange
        captureAudioFocusListener();

        // Act
        listenerCaptor.getValue().onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN);

        // Assert
        verify(mockStore).dispatch(any(AudioFocusAction.FocusRestored.class));
    }

    @Test
    public void unknownAction_shouldPassThrough() {
        // Arrange
        Object unknownAction = new Object();

        // Act
        middleware.invoke(mockStore).apply(next -> unknownAction).invoke(unknownAction);

        // Assert
        verify(next).invoke(unknownAction);
    }

    private void captureAudioFocusListener() {
        AudioFocusAction.RequestFocus action = new AudioFocusAction.RequestFocus();
        middleware.invoke(mockStore).apply(next -> action).invoke(action);
        verify(mockAudioManager).requestAudioFocus(listenerCaptor.capture(), anyInt(), anyInt());
    }
}
