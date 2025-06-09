package com.azure.android.communication.ui.calling.redux.middleware;

import android.content.Context;
import android.media.AudioManager;

import com.azure.android.communication.ui.calling.redux.Store;
import com.azure.android.communication.ui.calling.redux.action.AudioModeAction;
import com.azure.android.communication.ui.calling.redux.state.AudioMode;
import com.azure.android.communication.ui.calling.redux.state.AudioModeState;
import com.azure.android.communication.ui.calling.redux.state.AudioState;
import com.azure.android.communication.ui.calling.redux.state.ReduxState;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AudioModeMiddlewareTest {
    @Mock
    private Context mockContext;

    @Mock
    private AudioManager mockAudioManager;

    @Mock
    private Store<ReduxState> mockStore;

    @Mock
    private ReduxState mockState;

    @Mock
    private AudioState mockAudioState;

    @Mock
    private AudioModeState mockAudioModeState;

    private AudioModeMiddleware middleware;

    @Before
    public void setup() {
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);
        when(mockStore.getCurrentState()).thenReturn(mockState);
        when(mockState.getAudioState()).thenReturn(mockAudioState);
        when(mockAudioState.getModeState()).thenReturn(mockAudioModeState);
        middleware = new AudioModeMiddleware(mockContext);
    }

    @Test
    public void setMode_whenModeChangeSucceeds_shouldDispatchSuccessAction() {
        // Arrange
        AudioMode targetMode = AudioMode.IN_COMMUNICATION;
        when(mockAudioModeState.getCurrentMode()).thenReturn(AudioMode.NORMAL);
        AudioModeAction.SetModeRequested action = new AudioModeAction.SetModeRequested(targetMode);

        // Act
        middleware.invoke(mockStore).apply(next -> action).invoke(action);

        // Assert
        verify(mockAudioManager).setMode(AudioManager.MODE_IN_COMMUNICATION);
        verify(next).invoke(any(AudioModeAction.ModeChangeSucceeded.class));
    }

    @Test
    public void setMode_whenModeChangeFails_shouldDispatchFailureAction() {
        // Arrange
        AudioMode targetMode = AudioMode.IN_COMMUNICATION;
        when(mockAudioModeState.getCurrentMode()).thenReturn(AudioMode.NORMAL);
        doThrow(new RuntimeException("Test error")).when(mockAudioManager).setMode(anyInt());
        AudioModeAction.SetModeRequested action = new AudioModeAction.SetModeRequested(targetMode);

        // Act
        middleware.invoke(mockStore).apply(next -> action).invoke(action);

        // Assert
        verify(mockAudioManager).setMode(AudioManager.MODE_IN_COMMUNICATION);
        verify(next).invoke(any(AudioModeAction.ModeChangeFailed.class));
    }

    @Test
    public void setMode_whenSameMode_shouldNotDispatchAction() {
        // Arrange
        AudioMode currentMode = AudioMode.IN_COMMUNICATION;
        when(mockAudioModeState.getCurrentMode()).thenReturn(currentMode);
        AudioModeAction.SetModeRequested action = new AudioModeAction.SetModeRequested(currentMode);

        // Act
        Object result = middleware.invoke(mockStore).apply(next -> action).invoke(action);

        // Assert
        verify(mockAudioManager, never()).setMode(anyInt());
        verifyNoInteractions(next);
        assertNull(result);
    }

    @Test
    public void restoreMode_whenPreviousModeExists_shouldDispatchSuccessAction() {
        // Arrange
        AudioMode previousMode = AudioMode.IN_CALL;
        when(mockAudioModeState.getPreviousMode()).thenReturn(previousMode);
        AudioModeAction.RestorePreviousModeRequested action = new AudioModeAction.RestorePreviousModeRequested();

        // Act
        middleware.invoke(mockStore).apply(next -> action).invoke(action);

        // Assert
        verify(mockAudioManager).setMode(AudioManager.MODE_IN_CALL);
        verify(next).invoke(any(AudioModeAction.ModeChangeSucceeded.class));
    }

    @Test
    public void restoreMode_whenNoPreviousMode_shouldNotDispatchAction() {
        // Arrange
        when(mockAudioModeState.getPreviousMode()).thenReturn(null);
        AudioModeAction.RestorePreviousModeRequested action = new AudioModeAction.RestorePreviousModeRequested();

        // Act
        Object result = middleware.invoke(mockStore).apply(next -> action).invoke(action);

        // Assert
        verify(mockAudioManager, never()).setMode(anyInt());
        verifyNoInteractions(next);
        assertNull(result);
    }

    @Test
    public void restoreMode_whenModeChangeFails_shouldDispatchFailureAction() {
        // Arrange
        AudioMode previousMode = AudioMode.IN_CALL;
        when(mockAudioModeState.getPreviousMode()).thenReturn(previousMode);
        doThrow(new RuntimeException("Test error")).when(mockAudioManager).setMode(anyInt());
        AudioModeAction.RestorePreviousModeRequested action = new AudioModeAction.RestorePreviousModeRequested();

        // Act
        middleware.invoke(mockStore).apply(next -> action).invoke(action);

        // Assert
        verify(mockAudioManager).setMode(AudioManager.MODE_IN_CALL);
        verify(next).invoke(any(AudioModeAction.ModeChangeFailed.class));
    }

    @Test
    public void unknownAction_shouldPassThrough() {
        // Arrange
        Object unknownAction = new Object();

        // Act
        middleware.invoke(mockStore).apply(next -> unknownAction).invoke(unknownAction);

        // Assert
        verify(next).invoke(unknownAction);
        verifyNoInteractions(mockAudioManager);
    }
}
