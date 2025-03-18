package com.azure.android.communication.ui.calling.redux.reducer;

import com.azure.android.communication.ui.calling.redux.action.AudioModeAction;
import com.azure.android.communication.ui.calling.redux.state.AudioMode;
import com.azure.android.communication.ui.calling.redux.state.AudioModeState;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AudioModeReducerTest {

    @Test
    public void audioModeReducer_whenSetModeRequested_shouldUpdateMode() {
        // Arrange
        AudioModeState initialState = new AudioModeState();
        AudioMode targetMode = AudioMode.IN_COMMUNICATION;
        AudioModeAction.SetModeRequested action = new AudioModeAction.SetModeRequested(targetMode);

        // Act
        AudioModeState newState = AudioModeReducer.reduce(initialState, action);

        // Assert
        assertNotNull(newState);
        assertEquals(targetMode, newState.getCurrentMode());
        assertEquals(AudioMode.NORMAL, newState.getPreviousMode());
    }

    @Test
    public void audioModeReducer_whenModeChangeSucceeded_shouldUpdateMode() {
        // Arrange
        AudioModeState initialState = new AudioModeState();
        AudioMode targetMode = AudioMode.IN_CALL;
        AudioModeAction.ModeChangeSucceeded action = new AudioModeAction.ModeChangeSucceeded(targetMode);

        // Act
        AudioModeState newState = AudioModeReducer.reduce(initialState, action);

        // Assert
        assertNotNull(newState);
        assertEquals(targetMode, newState.getCurrentMode());
    }

    @Test
    public void audioModeReducer_whenModeChangeFailed_shouldRevertToFallbackMode() {
        // Arrange
        AudioModeState initialState = new AudioModeState(AudioMode.IN_COMMUNICATION, AudioMode.NORMAL, null);
        AudioMode targetMode = AudioMode.IN_CALL;
        AudioMode fallbackMode = AudioMode.NORMAL;
        String error = "Test error";
        AudioModeAction.ModeChangeFailed action = new AudioModeAction.ModeChangeFailed(targetMode, fallbackMode, error);

        // Act
        AudioModeState newState = AudioModeReducer.reduce(initialState, action);

        // Assert
        assertNotNull(newState);
        assertEquals(fallbackMode, newState.getCurrentMode());
        assertEquals(error, newState.getLastError());
    }

    @Test
    public void audioModeReducer_whenRestorePreviousModeRequested_shouldRestorePreviousMode() {
        // Arrange
        AudioMode previousMode = AudioMode.IN_CALL;
        AudioModeState initialState = new AudioModeState(AudioMode.IN_COMMUNICATION, previousMode, null);
        AudioModeAction.RestorePreviousModeRequested action = new AudioModeAction.RestorePreviousModeRequested();

        // Act
        AudioModeState newState = AudioModeReducer.reduce(initialState, action);

        // Assert
        assertNotNull(newState);
        assertEquals(previousMode, newState.getCurrentMode());
    }

    @Test
    public void audioModeReducer_whenRestorePreviousModeRequestedWithNoPreviousMode_shouldKeepCurrentMode() {
        // Arrange
        AudioModeState initialState = new AudioModeState();
        AudioModeAction.RestorePreviousModeRequested action = new AudioModeAction.RestorePreviousModeRequested();

        // Act
        AudioModeState newState = AudioModeReducer.reduce(initialState, action);

        // Assert
        assertNotNull(newState);
        assertEquals(initialState.getCurrentMode(), newState.getCurrentMode());
    }

    @Test
    public void audioModeReducer_whenSystemModeChanged_shouldUpdateMode() {
        // Arrange
        AudioModeState initialState = new AudioModeState();
        AudioMode systemMode = AudioMode.RINGTONE;
        AudioModeAction.SystemModeChanged action = new AudioModeAction.SystemModeChanged(systemMode);

        // Act
        AudioModeState newState = AudioModeReducer.reduce(initialState, action);

        // Assert
        assertNotNull(newState);
        assertEquals(systemMode, newState.getCurrentMode());
    }

    @Test
    public void audioModeReducer_whenUnknownAction_shouldReturnSameState() {
        // Arrange
        AudioModeState initialState = new AudioModeState();
        Object unknownAction = new Object();

        // Act
        AudioModeState newState = AudioModeReducer.reduce(initialState, unknownAction);

        // Assert
        assertEquals(initialState, newState);
    }
}
