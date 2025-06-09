package com.azure.android.communication.ui.calling.redux.reducer;

import com.azure.android.communication.ui.calling.redux.action.AudioFocusAction;
import com.azure.android.communication.ui.calling.redux.state.AudioFocusState;
import com.azure.android.communication.ui.calling.redux.state.AudioFocusStatus;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AudioFocusReducerTest {

    @Test
    public void audioFocusReducer_whenRequestFocus_shouldSetStatusToRequesting() {
        // Arrange
        AudioFocusState initialState = new AudioFocusState();
        AudioFocusAction.RequestFocus action = new AudioFocusAction.RequestFocus();

        // Act
        AudioFocusState newState = AudioFocusReducer.reduce(initialState, action);

        // Assert
        assertNotNull(newState);
        assertEquals(AudioFocusStatus.REQUESTING, newState.getStatus());
    }

    @Test
    public void audioFocusReducer_whenFocusGranted_shouldSetStatusToApproved() {
        // Arrange
        AudioFocusState initialState = new AudioFocusState(AudioFocusStatus.REQUESTING, null);
        AudioFocusAction.FocusGranted action = new AudioFocusAction.FocusGranted();

        // Act
        AudioFocusState newState = AudioFocusReducer.reduce(initialState, action);

        // Assert
        assertNotNull(newState);
        assertEquals(AudioFocusStatus.APPROVED, newState.getStatus());
    }

    @Test
    public void audioFocusReducer_whenFocusDenied_shouldSetStatusToRejected() {
        // Arrange
        AudioFocusState initialState = new AudioFocusState(AudioFocusStatus.REQUESTING, null);
        String reason = "Test denial reason";
        AudioFocusAction.FocusDenied action = new AudioFocusAction.FocusDenied(reason);

        // Act
        AudioFocusState newState = AudioFocusReducer.reduce(initialState, action);

        // Assert
        assertNotNull(newState);
        assertEquals(AudioFocusStatus.REJECTED, newState.getStatus());
        assertEquals(reason, newState.getLastError());
    }

    @Test
    public void audioFocusReducer_whenFocusLostPermanently_shouldSetStatusToNone() {
        // Arrange
        AudioFocusState initialState = new AudioFocusState(AudioFocusStatus.APPROVED, null);
        AudioFocusAction.FocusLost action = new AudioFocusAction.FocusLost(false);

        // Act
        AudioFocusState newState = AudioFocusReducer.reduce(initialState, action);

        // Assert
        assertNotNull(newState);
        assertEquals(AudioFocusStatus.NONE, newState.getStatus());
    }

    @Test
    public void audioFocusReducer_whenFocusLostTransient_shouldSetStatusToInterrupted() {
        // Arrange
        AudioFocusState initialState = new AudioFocusState(AudioFocusStatus.APPROVED, null);
        AudioFocusAction.FocusLost action = new AudioFocusAction.FocusLost(true);

        // Act
        AudioFocusState newState = AudioFocusReducer.reduce(initialState, action);

        // Assert
        assertNotNull(newState);
        assertEquals(AudioFocusStatus.INTERRUPTED, newState.getStatus());
    }

    @Test
    public void audioFocusReducer_whenReleaseFocus_shouldSetStatusToNone() {
        // Arrange
        AudioFocusState initialState = new AudioFocusState(AudioFocusStatus.APPROVED, null);
        AudioFocusAction.ReleaseFocus action = new AudioFocusAction.ReleaseFocus();

        // Act
        AudioFocusState newState = AudioFocusReducer.reduce(initialState, action);

        // Assert
        assertNotNull(newState);
        assertEquals(AudioFocusStatus.NONE, newState.getStatus());
    }

    @Test
    public void audioFocusReducer_whenFocusInterrupted_shouldSetStatusToInterrupted() {
        // Arrange
        AudioFocusState initialState = new AudioFocusState(AudioFocusStatus.APPROVED, null);
        String reason = "Test interruption reason";
        AudioFocusAction.FocusInterrupted action = new AudioFocusAction.FocusInterrupted(reason);

        // Act
        AudioFocusState newState = AudioFocusReducer.reduce(initialState, action);

        // Assert
        assertNotNull(newState);
        assertEquals(AudioFocusStatus.INTERRUPTED, newState.getStatus());
        assertEquals(reason, newState.getLastError());
    }

    @Test
    public void audioFocusReducer_whenFocusRestored_shouldSetStatusToApproved() {
        // Arrange
        AudioFocusState initialState = new AudioFocusState(AudioFocusStatus.INTERRUPTED, null);
        AudioFocusAction.FocusRestored action = new AudioFocusAction.FocusRestored();

        // Act
        AudioFocusState newState = AudioFocusReducer.reduce(initialState, action);

        // Assert
        assertNotNull(newState);
        assertEquals(AudioFocusStatus.APPROVED, newState.getStatus());
    }

    @Test
    public void audioFocusReducer_whenUnknownAction_shouldReturnSameState() {
        // Arrange
        AudioFocusState initialState = new AudioFocusState();
        Object unknownAction = new Object();

        // Act
        AudioFocusState newState = AudioFocusReducer.reduce(initialState, unknownAction);

        // Assert
        assertEquals(initialState, newState);
    }
}
