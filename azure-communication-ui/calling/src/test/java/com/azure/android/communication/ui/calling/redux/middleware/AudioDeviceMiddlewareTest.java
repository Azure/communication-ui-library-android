package com.azure.android.communication.ui.calling.redux.middleware;

import android.os.Handler;
import android.os.Looper;

import com.azure.android.communication.ui.calling.redux.Store;
import com.azure.android.communication.ui.calling.redux.action.AudioDeviceAction;
import com.azure.android.communication.ui.calling.redux.state.AudioDevice;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceState;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceType;
import com.azure.android.communication.ui.calling.service.AudioDeviceService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.LooperMode;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.PAUSED)
public class AudioDeviceMiddlewareTest {
    @Mock
    private Store mockStore;
    @Mock
    private AudioDeviceService mockAudioDeviceService;
    @Mock
    private AudioDeviceMiddleware.Dispatch mockNext;

    private AudioDeviceMiddleware middleware;
    private AudioDeviceState initialState;
    private AudioDevice speaker;
    private AudioDevice wiredHeadset;
    private AudioDevice bluetooth;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        middleware = new AudioDeviceMiddleware(mockAudioDeviceService);

        speaker = new AudioDevice(AudioDeviceType.SPEAKER, "speaker", "Speaker");
        wiredHeadset = new AudioDevice(AudioDeviceType.WIRED_HEADSET, "wired_headset", "Wired Headset");
        bluetooth = new AudioDevice(AudioDeviceType.BLUETOOTH, "bluetooth", "Bluetooth");

        initialState = new AudioDeviceState(
            speaker,
            Arrays.asList(speaker, wiredHeadset, bluetooth),
            true,
            true,
            com.azure.android.communication.ui.calling.redux.state.AudioDeviceSwitchingState.NONE,
            null
        );

        when(mockStore.getCurrentState()).thenReturn(initialState);
    }

    @Test
    public void testSelectDeviceRequested_Success() {
        when(mockAudioDeviceService.configureAudioRouting(wiredHeadset)).thenReturn(true);

        middleware.process(mockStore, new AudioDeviceAction.SelectDeviceRequested(wiredHeadset), mockNext);

        // Verify DeviceSwitchStarted action is dispatched immediately
        ArgumentCaptor<Object> actionCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockNext, times(1)).dispatch(actionCaptor.capture());
        assertTrue(actionCaptor.getValue() instanceof AudioDeviceAction.DeviceSwitchStarted);

        // Execute pending Looper tasks
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        // Verify DeviceSwitchCompleted action is dispatched after async operation
        verify(mockNext, times(2)).dispatch(actionCaptor.capture());
        assertTrue(actionCaptor.getAllValues().get(1) instanceof AudioDeviceAction.DeviceSwitchCompleted);
    }

    @Test
    public void testSelectDeviceRequested_Failure() {
        when(mockAudioDeviceService.configureAudioRouting(bluetooth)).thenReturn(false);

        middleware.process(mockStore, new AudioDeviceAction.SelectDeviceRequested(bluetooth), mockNext);

        // Verify DeviceSwitchStarted action is dispatched immediately
        ArgumentCaptor<Object> actionCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockNext, times(1)).dispatch(actionCaptor.capture());
        assertTrue(actionCaptor.getValue() instanceof AudioDeviceAction.DeviceSwitchStarted);

        // Execute pending Looper tasks
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        // Verify DeviceSwitchFailed action is dispatched after async operation
        verify(mockNext, times(2)).dispatch(actionCaptor.capture());
        assertTrue(actionCaptor.getAllValues().get(1) instanceof AudioDeviceAction.DeviceSwitchFailed);

        // Verify fallback to previous device is attempted
        verify(mockAudioDeviceService).configureAudioRouting(speaker);
    }

    @Test
    public void testSelectDeviceRequested_UnavailableDevice() {
        AudioDevice unavailableDevice = new AudioDevice(AudioDeviceType.BLUETOOTH, "unavailable", "Unavailable");

        middleware.process(mockStore, new AudioDeviceAction.SelectDeviceRequested(unavailableDevice), mockNext);

        // Verify DeviceSwitchFailed action is dispatched immediately
        ArgumentCaptor<Object> actionCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockNext, times(1)).dispatch(actionCaptor.capture());
        assertTrue(actionCaptor.getValue() instanceof AudioDeviceAction.DeviceSwitchFailed);

        // Verify no device configuration is attempted
        verify(mockAudioDeviceService, never()).configureAudioRouting(any());
    }

    @Test
    public void testSelectDeviceRequested_AlreadySelected() {
        middleware.process(mockStore, new AudioDeviceAction.SelectDeviceRequested(speaker), mockNext);

        // Verify no actions are dispatched
        verify(mockNext, never()).dispatch(any());
        verify(mockAudioDeviceService, never()).configureAudioRouting(any());
    }

    @Test
    public void testSelectDeviceRequested_AlreadySwitching() {
        AudioDeviceState switchingState = new AudioDeviceState(
            speaker,
            Arrays.asList(speaker, wiredHeadset, bluetooth),
            true,
            true,
            com.azure.android.communication.ui.calling.redux.state.AudioDeviceSwitchingState.SWITCHING,
            null
        );
        when(mockStore.getCurrentState()).thenReturn(switchingState);

        middleware.process(mockStore, new AudioDeviceAction.SelectDeviceRequested(bluetooth), mockNext);

        // Verify no actions are dispatched
        verify(mockNext, never()).dispatch(any());
        verify(mockAudioDeviceService, never()).configureAudioRouting(any());
    }

    @Test
    public void testSelectDeviceRequested_Exception() {
        when(mockAudioDeviceService.configureAudioRouting(bluetooth))
            .thenThrow(new RuntimeException("Test error"));

        middleware.process(mockStore, new AudioDeviceAction.SelectDeviceRequested(bluetooth), mockNext);

        // Verify DeviceSwitchStarted action is dispatched immediately
        ArgumentCaptor<Object> actionCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockNext, times(1)).dispatch(actionCaptor.capture());
        assertTrue(actionCaptor.getValue() instanceof AudioDeviceAction.DeviceSwitchStarted);

        // Execute pending Looper tasks
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        // Verify DeviceSwitchFailed action is dispatched after async operation
        verify(mockNext, times(2)).dispatch(actionCaptor.capture());
        Object failedAction = actionCaptor.getAllValues().get(1);
        assertTrue(failedAction instanceof AudioDeviceAction.DeviceSwitchFailed);
        assertEquals("Error configuring audio routing: Test error",
            ((AudioDeviceAction.DeviceSwitchFailed) failedAction).getError());

        // Verify fallback to previous device is attempted
        verify(mockAudioDeviceService).configureAudioRouting(speaker);
    }
}
